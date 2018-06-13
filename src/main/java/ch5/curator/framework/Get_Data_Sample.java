package ch5.curator.framework;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author pfjia
 * @since 2018/6/13 20:22
 */
public class Get_Data_Sample {
    public static void main(String[] args) throws Exception {
        String path="/zk-book";
        CuratorFramework client=CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,"init".getBytes());
        Stat stat=new Stat();
        System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
    }
}
