package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author pfjia
 * @since 2018/6/14 13:17
 */
public class DisQueue {
    private static final String QUEUE_ROOT = "/curator-queue";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        client.start();
        DistributedQueue<String> queue = QueueBuilder.builder(client, new QConsumer<String>(), new QSerializer<>(), QUEUE_ROOT).buildQueue();
        queue.put("123");
    }

    public static class QConsumer<T> implements QueueConsumer<T> {
        @Override
        public void consumeMessage(T message) throws Exception {

        }

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {

        }
    }

    public static class QSerializer<T> implements QueueSerializer<T> {

        @Override
        public byte[] serialize(T item) {
            return new byte[0];
        }

        @Override
        public T deserialize(byte[] bytes) {
            return null;
        }
    }
}
