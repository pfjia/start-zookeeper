package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * 分布式屏障(外部释放barrier)
 *
 * @author pfjia
 * @since 2018/6/14 0:01
 */
public class DisBarrier {
    private static final String BARRIER_ROOT = "/curator-barrier";
    private static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework client = CuratorFrameworkFactory.builder()
                            .connectString(Const.HOSTS)
                            .retryPolicy(new ExponentialBackoffRetry(1000, 2))
                            .build();
                    client.start();
                    barrier = new DistributedBarrier(client, BARRIER_ROOT);
                    System.out.println(Thread.currentThread().getName() + "号barrier设置");
                    try {
                        barrier.setBarrier();
                        barrier.waitOnBarrier();
                        System.err.println("启动...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(5);
        barrier.removeBarrier();
    }
}
