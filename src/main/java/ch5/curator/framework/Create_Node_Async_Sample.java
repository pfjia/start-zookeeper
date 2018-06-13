package ch5.curator.framework;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author pfjia
 * @since 2018/6/13 20:33
 */
public class Create_Node_Async_Sample {

    public static CountDownLatch semaphore = new CountDownLatch(2);
    public static ExecutorService tp = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        System.out.println("Main thread: " + Thread.currentThread().getName());
        BackgroundCallback cb = (client1, event) -> {
            System.out.println("event[code:" + event.getResultCode() + ", type: " + event.getType() + " ]" );
            System.out.println("Thread of processResult: " + Thread.currentThread().getName());
            semaphore.countDown();
        };
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground(cb, tp)
                .forPath(path, "init".getBytes());

        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground(cb)
                .forPath(path, "init".getBytes());
        semaphore.await();
        tp.shutdown();
    }
}
