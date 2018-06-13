package ch5.curator.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.jboss.netty.channel.CompleteChannelFuture;

import java.io.File;

/**
 * @author pfjia
 * @since 2018/6/14 0:43
 */
public class TestingServerDemo {
    public static void main(String[] args) throws Exception {
        TestingServer server=new TestingServer(2181,new File("D:/test"));
        CuratorFramework client=CuratorFrameworkFactory.builder()
                .connectString(server.getConnectString())
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();
        System.out.println(client.getChildren().forPath("/zookeeper"));
        server.close();
    }
}
