package ch5.curator.recipes;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * leader选举
 * 1.创建"临时顺序"节点,最小序列号成为leader
 * 2.创建同一个"临时"节点,创建成功则为leader
 *
 * @author pfjia
 * @since 2018/6/13 20:50
 */
public class MasterSelect {
    private static final String LEADER_ROOT = "/curator-leader";

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        LeaderSelector selector = new LeaderSelector(
                client,
                LEADER_ROOT,
                //成为leader时回调,执行完此方法后立即释放leader权利,开始下一轮选举
                new LeaderSelectorListenerAdapter() {
                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception {
                        System.out.println("成为leader");
                        TimeUnit.SECONDS.sleep(3);
                        System.out.println("完成leader操作,放弃leader权利");
                    }
                }
        );
        selector.autoRequeue();
        selector.start();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
