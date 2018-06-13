package application.leader_election;

import core.Const;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;

/**
 * 1、客户端连接时，在指定的目录(这里假定为"/leader")创建一个EPHEMERAL_SEQUENTIAL的节点，把内网的IP数据存入创建节点。
 * 2、获取目录的子点节，并取得序列号最小的节点，我们把这个节点设置为leader。当此节点被删除时，证明leader断线。
 * 3、其它机器监听leader节点，当leader节点的删除时，再取目录的最小子点节作为leader。
 * <p>
 * 参考:https://blog.csdn.net/MassiveStars/article/details/53894551
 *
 * @author pfjia
 * @since 2018/6/13 15:33
 */
public class LeaderElection {
    private ZooKeeper zk;
    private static byte[] DEFAULT_DATA = {0x12, 0x34};
    /**
     * 通过mutex来控制是否进行新一轮的leader election,虽然在Watcher中再次调用选举算法同样可以达到相同目的,但使用mutex可以保证不会退出{@link #start()}
     */
    private static final Object MUTEX = new Object();
    private static String LEADER_ROOT = "/leader";
    /**
     * 本机ip地址,也是写入子节点的值
     */
    private byte[] localhost = getLocalAddressBytes();
    /**
     * 将本机ip地址写入子节点后子节点的序列号
     */
    private String sequenceNumber;
    private static CountDownLatch firstElectionSignal = new CountDownLatch(1);

    /**
     * leader的IP地址
     */
    private static String leader;

    public LeaderElection() throws IOException, InterruptedException, KeeperException {
        zk = ZookeeperClient.getInstance();
        ensureExists(LEADER_ROOT);
        ensureLocalNodeExists();
        System.out.println("----------------------------");
        System.out.println("local IP: " + RemotingUtil.getLocalAddress());
        System.out.println("local created node: " + sequenceNumber);
        System.out.println("----------------------------");
    }


    public void ensureLocalNodeExists() throws KeeperException, InterruptedException {
        //获取子节点
        List<String> list = zk.getChildren(LEADER_ROOT, false);
        //判断本机ip地址是否已写入ROOT的子节点中
        for (String node : list) {
            Stat stat = new Stat();
            String path = LEADER_ROOT + "/" + node;
            byte[] data = zk.getData(path, false, stat);
            if (Arrays.equals(data, localhost)) {
                sequenceNumber = node;
                return;
            }
        }
        //若未写入,则新建子节点
        sequenceNumber = zk.create(LEADER_ROOT + "/", localhost, ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL).replace(LEADER_ROOT + "/", "");
    }


    /**
     * 确保path对应的znode存在
     *
     * @param path path
     */
    public void ensureExists(String path) {
        try {
            Stat stat = zk.exists(path, false);
            if (stat == null) {
                zk.create(path, DEFAULT_DATA, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始选举算法,若leader是自己,则退出;否则,阻塞在该方法上
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void start() throws KeeperException, InterruptedException {
        do {
            synchronized (MUTEX) {
                System.out.println("begin leader election...");
                List<String> nodes = zk.getChildren(LEADER_ROOT, false);
                SortedSet<String> sortedNodes = new TreeSet<>(nodes);
                //取出序列号最小的节点(即此次选举的leader)
                String minSequenceNumber = sortedNodes.first();
                String minPath = LEADER_ROOT + "/" + sortedNodes.first();
                //监控序列号最小的节点(即leader对应的节点,易发生羊群效应)
                Watcher watcher = sequenceNumber.equals(minSequenceNumber) ? null : new NodeCrashWatcher(MUTEX);
                byte[] data = zk.getData(minPath, watcher, null);
                leader = new String(data);
                System.out.println("leader election end, the leader is : " + leader);
                if (firstElectionSignal.getCount() != 0) {
                    firstElectionSignal.countDown();
                }
                if (sequenceNumber.equals(minSequenceNumber)) {
                    return;
                }
                MUTEX.wait();
            }
        } while (true);
    }

    /**
     * 避免羊群效应的实现
     */
    public void startWithoutHerdEffect() throws InterruptedException, KeeperException {
        do {
            synchronized (MUTEX) {
                System.out.println("begin leader election(without herd effect)...");
                List<String> nodes = zk.getChildren(LEADER_ROOT, false);
                TreeSet<String> sortedNodes = new TreeSet<>(nodes);
                //取出序列号最小的节点
                String minSequenceNumber = sortedNodes.first();
                String minPath = LEADER_ROOT + "/" + minSequenceNumber;
                //获取最小节点的数据(即leader的ip地址)
                byte[] data = zk.getData(minPath, false, null);
                leader = new String(data);
                System.out.println("leader election end, the leader is : " + leader);
                //监控小于当前节点序列号的节点中序列号最大的节点避免羊群效应
                String lowerSequenceNumber = sortedNodes.lower(sequenceNumber);
                if (lowerSequenceNumber == null) {
                    return;
                } else {
                    zk.getData(LEADER_ROOT + "/" + lowerSequenceNumber, new NodeCrashWatcher(MUTEX), null);
                }
                if (firstElectionSignal.getCount() != 0) {
                    firstElectionSignal.countDown();
                }
                MUTEX.wait();
            }
        } while (true);
    }

    /**
     * @return leader的ip地址
     * @throws InterruptedException
     */
    public static String getLeader() throws InterruptedException {
        //确保第一次leader选举已经结束
        firstElectionSignal.await();
        return leader;
    }

    public static byte[] getLocalAddressBytes() {
        String ip = RemotingUtil.getLocalAddress();
        return Optional.ofNullable(ip).map(String::getBytes).orElse(null);
    }


    public static void main(String[] args) throws InterruptedException {
        Const.THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                LeaderElection leaderElection;
                try {
                    leaderElection = new LeaderElection();
                    leaderElection.start();
                } catch (IOException | InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
        String leader = LeaderElection.getLeader();
        System.out.println(leader);
    }
}
