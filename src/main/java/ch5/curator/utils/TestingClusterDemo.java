package ch5.curator.utils;

import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.curator.test.TestingZooKeeperServer;

import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/14 9:10
 */
public class TestingClusterDemo {
    public static void main(String[] args) throws Exception {
        TestingCluster cluster=new TestingCluster(3);
        cluster.start();
        TimeUnit.SECONDS.sleep(2);
        TestingZooKeeperServer leader=null;
        for (TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.print(zs.getInstanceSpec().getServerId()+"-");
            System.out.print(zs.getQuorumPeer().getServerState()+"-");
            System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
            if (zs.getQuorumPeer().getServerState().equals("leading")){
                leader=zs;
            }
        }
        leader.kill();
        System.out.println("--After leader kill: ");
        for (TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.print(zs.getInstanceSpec().getServerId()+"-");
            System.out.print(zs.getQuorumPeer().getServerState()+"-");
            System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
        }
        cluster.stop();
    }
}
