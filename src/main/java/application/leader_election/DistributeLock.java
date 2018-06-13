package application.leader_election;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 1、创建一个永久性节点，作锁的根目录
 * 2、当要获取一个锁时，在锁目录下创建一个临时有序列的节点
 * 3、检查锁目录的子节点是否有序列比它小，若有则监听比它小的上一个节点，当前锁处于等待状态
 * 4、当等待时间超过Zookeeper session的连接时间（sessionTimeout）时，当前session过期，Zookeeper自动删除此session创建的临时节点，等待状态结束，获取锁失败
 * 5、当监听器触发时，等待状态结束，获得锁
 * 参考:https://blog.csdn.net/massivestars/article/details/53771532
 *
 * @author pfjia
 * @since 2018/6/13 18:59
 */
public class DistributeLock implements Lock {

    private static final String LOCK_ROOT = "/lock";

    private Object sync;

    private ZooKeeper zk;

    public DistributeLock() {
        try {
            zk = ZookeeperClient.getInstance();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
