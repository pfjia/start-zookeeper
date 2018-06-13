package ch5;

import core.Const;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/12 14:23
 */
public class Zookeeper_Constructor_Usage_simple implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(Const.HOSTS, Const.SESSION_TIMEOUT, new Zookeeper_Constructor_Usage_simple());
        System.out.println(zooKeeper.getState());
        latch.await();
        System.out.println("Zookeeper session established");
    }
}
