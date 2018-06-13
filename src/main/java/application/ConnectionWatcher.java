package application;

import core.Const;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/13 13:41
 */
public class ConnectionWatcher implements Watcher {
    protected ZooKeeper zk;
    private CountDownLatch latch = new CountDownLatch(1);

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, Const.SESSION_TIMEOUT, this);
        latch.await();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }

    public void close() throws InterruptedException {
        zk.close();
    }
}
