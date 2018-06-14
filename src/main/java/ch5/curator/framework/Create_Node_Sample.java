package ch5.curator.framework;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author pfjia
 * @since 2018/6/13 20:13
 */
public class Create_Node_Sample {
    public static void main(String[] args) throws Exception {
        CuratorFramework client=CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/zk-book/c1","init".getBytes());

    }
}
