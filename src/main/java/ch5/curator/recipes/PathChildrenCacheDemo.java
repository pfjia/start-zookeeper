package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * 子节点监听
 *
 * @author pfjia
 * @since 2018/6/14 9:30
 */
public class PathChildrenCacheDemo {
    private static final String ROOT = "/zk-book";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new RetryNTimes(4, 1000))
                .build();
        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, ROOT, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADD, " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED, " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED, " + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });
        client.create().withMode(CreateMode.PERSISTENT).forPath(ROOT);
        TimeUnit.SECONDS.sleep(1);

        client.create().withMode(CreateMode.PERSISTENT).forPath(ROOT + "/c1");
        TimeUnit.SECONDS.sleep(1);

        client.delete().forPath(ROOT + "/c1");
        TimeUnit.SECONDS.sleep(1);

        client.delete().forPath(ROOT);
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
