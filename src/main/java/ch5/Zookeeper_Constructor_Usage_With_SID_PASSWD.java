package ch5;

import core.Const;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/12 14:31
 */
public class Zookeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event: " + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            latch.countDown();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new Zookeeper_Constructor_Usage_With_SID_PASSWD());
        latch.await();
        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();
        //use illegal sessionId and sessionPasswd
        zooKeeper = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new Zookeeper_Constructor_Usage_With_SID_PASSWD(), 1L, "test".getBytes());
        //use correct sessionId and sessionPasswd
        zooKeeper = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new Zookeeper_Constructor_Usage_With_SID_PASSWD(), sessionId, passwd);
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
