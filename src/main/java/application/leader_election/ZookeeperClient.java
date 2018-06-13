package application.leader_election;

import application.ConnectionWatcher;
import core.Const;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/13 15:30
 */
public class ZookeeperClient {
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            }
        });
        latch.await();
        return zk;
    }

    public static void main(String[] args) throws IOException {
        ZooKeeper zk1 = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new ConnectionWatcher());
        ZooKeeper zk2 = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new ConnectionWatcher());
        long i1 = zk1.getSessionId();
        long i2 = zk2.getSessionId();
        System.out.println();
    }
}
