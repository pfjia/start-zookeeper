package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * 节点监听
 *
 * @author pfjia
 * @since 2018/6/14 9:16
 */
public class NodeCacheDemo {
    private static final String NODE_CACHE_ROOT = "/zk-book/nodecache";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new RetryUntilElapsed(4, 1000))
                .build();
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(NODE_CACHE_ROOT, "init".getBytes());
        NodeCache cache = new NodeCache(client, NODE_CACHE_ROOT, false);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Node data update, new data: " + new String(cache.getCurrentData().getData()));
            }
        });
        client.setData()
                .forPath(NODE_CACHE_ROOT, "u".getBytes());
        TimeUnit.SECONDS.sleep(1);
        client.delete().deletingChildrenIfNeeded().forPath(NODE_CACHE_ROOT);
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
