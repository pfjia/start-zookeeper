package ch5.curator.recipes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author pfjia
 * @since 2018/6/13 21:02
 */
public class NoLock {
    public static void main(String[] args) {
        CountDownLatch latch=new CountDownLatch(1);
        for (int i=0;i<10;i++){
            new Thread(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo=sdf.format(new Date());
                System.err.println("生成的订单号是: "+orderNo);
            }).start();
        }
        latch.countDown();
    }
}
