package ch5.curator.framework;

import core.Const;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/13 20:03
 */
public class Create_Session_Sample {
    public static void main(String[] args) throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(Const.HOSTS, retryPolicy);
        client.start();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
