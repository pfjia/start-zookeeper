package ch5.curator.recipes;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author pfjia
 * @since 2018/6/13 23:38
 */
public class CyclicBarrierDemo {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new Runner("1号选手"));
        executorService.submit(new Runner("2号选手"));
        executorService.submit(new Runner("3号选手"));
        executorService.shutdown();
    }

    static class Runner implements Runnable {
        private String name;

        public Runner(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name + "准备好了");
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(name + "起跑!");
        }
    }
}
