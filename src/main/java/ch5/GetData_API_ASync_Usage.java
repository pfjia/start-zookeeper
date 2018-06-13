package ch5;

import core.Const;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper API 获取节点数据内容，使用异步(async)接口。
 *
 * @author pfjia
 * @since 2018/6/12 14:23
 */
public class GetData_API_ASync_Usage implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zk;

    public static void main(String[] args) throws Exception {

        String path = "/zk-book";
        zk = new ZooKeeper(Const.HOSTS,
                Const.SESSION_TIMEOUT,
                new GetData_API_ASync_Usage());
        latch.await();
        Stat stat = zk.exists(path, false);
        if (stat == null) {
            zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }

        zk.getData(path, true, new IDataCallback(), null);

        zk.setData(path, "123".getBytes(), -1);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            if (EventType.None == event.getType() && null == event.getPath()) {
                latch.countDown();
            } else if (event.getType() == EventType.NodeDataChanged) {
                try {
                    zk.getData(event.getPath(), true, new IDataCallback(), null);
                } catch (Exception e) {
                }
            }
        }
    }
}

class IDataCallback implements AsyncCallback.DataCallback {
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        System.out.println(rc + ", " + path + ", " + new String(data));
        System.out.println(stat.getCzxid() + "," +
                stat.getMzxid() + "," +
                stat.getVersion());
    }
}

