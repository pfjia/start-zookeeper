package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/13 21:04
 */
public class Lock {
    private static final String LOCK_ROOT="/curator-lock";
    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client=CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new RetryNTimes(4,1000))
                .build();
        client.start();
        InterProcessMutex lock=new InterProcessMutex(client,LOCK_ROOT);
        CountDownLatch latch=new CountDownLatch(1);
        for (int i=0;i<30;i++){
            new Thread(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    lock.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo=sdf.format(new Date());
                System.err.println("生成的订单号是: "+orderNo);
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        latch.countDown();

    }
}
