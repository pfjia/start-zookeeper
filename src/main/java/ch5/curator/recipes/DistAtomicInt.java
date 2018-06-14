package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;

/**
 * 原子计数器
 *
 * @author pfjia
 * @since 2018/6/13 22:52
 */
public class DistAtomicInt {
    private static final String ATOMIC_ROOT = "/curator-atomic";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .sessionTimeoutMs(5000)
                .retryPolicy(new RetryOneTime(1000))
                .build();
        client.start();
        DistributedAtomicInteger integer = new DistributedAtomicInteger(client, ATOMIC_ROOT, new RetryNTimes(3, 1000));
        AtomicValue<Integer> rc = integer.add(8);
        System.out.println("Result: " + rc.succeeded());
    }
}
