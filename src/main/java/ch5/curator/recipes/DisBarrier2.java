package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.RetryUntilElapsed;

import java.util.concurrent.TimeUnit;

/**
 * 分布式屏障(内部释放barrier)
 * 思路:创建临时子节点,监听子节点列表变更,子节点数目达到阈值则打开barrier
 *
 * @author pfjia
 * @since 2018/6/14 0:18
 */
public class DisBarrier2 {
    private static final String BARRIER_ROOT = "/curator-barrier";

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework client = CuratorFrameworkFactory.builder()
                            .connectString(Const.HOSTS)
                            .retryPolicy(new RetryUntilElapsed(10, 1000))
                            .build();
                    client.start();
                    DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, BARRIER_ROOT, 5);
                    try {
                        TimeUnit.SECONDS.sleep(Math.round(Math.random() * 3));
                        System.out.println(Thread.currentThread().getName() + "号进入barrier");
                        barrier.enter();
                        System.out.println("启动");
                        TimeUnit.SECONDS.sleep(Math.round(Math.random() * 3));
                        barrier.leave();
                        System.out.println("退出");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
