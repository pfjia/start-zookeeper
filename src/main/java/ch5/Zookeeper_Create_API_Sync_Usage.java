package ch5;

import core.Const;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/12 14:40
 */
public class Zookeeper_Create_API_Sync_Usage implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper=new ZooKeeper(Const.HOSTS,Const.SESSION_TIMEOUT,new Zookeeper_Create_API_Sync_Usage());
        latch.await();
        String path1=zooKeeper.create("/zk-test-ephemeral-","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println("Success create znode: "+path1);

        String path2=zooKeeper.create("/zk-test-ephemeral-","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Success create znode: "+path2);
    }

}
