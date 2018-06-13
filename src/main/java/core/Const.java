package core;

import java.nio.charset.Charset;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pfjia
 * @since 2018/6/13 13:41
 */
public class Const {
    public static final String HOSTS = "39.108.72.52:2181";
    public static final int SESSION_TIMEOUT = 5000;
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r);
            thread.setName(String.valueOf(id.getAndIncrement()));
            return thread;
        }
    });
}
