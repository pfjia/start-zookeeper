package ch5.curator.utils;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author pfjia
 * @since 2018/6/14 0:30
 */
public class ZKPathDemo {
    private static final String PATH ="/curator-zkpath";
    public static void main(String[] args) throws Exception {
        CuratorFramework client=CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .retryPolicy(new RetryNTimes(5,1000))
                .build();
        client.start();
        ZooKeeper zooKeeper=client.getZookeeperClient().getZooKeeper();
        System.out.println(ZKPaths.fixForNamespace(PATH,"/sub"));
        System.out.println(ZKPaths.makePath(PATH,"sub"));
        System.out.println(ZKPaths.getNodeFromPath(PATH+"/sub1"));
        ZKPaths.PathAndNode pn=ZKPaths.getPathAndNode(PATH+"/sub1/ss");
        System.out.println(pn.getPath());
        System.out.println(pn.getNode());
        String dir1=PATH+"/child1";
        String dir2=PATH+"/child2";
        ZKPaths.mkdirs(zooKeeper,dir1);
        ZKPaths.mkdirs(zooKeeper,dir2);
        System.out.println(ZKPaths.getSortedChildren(zooKeeper,PATH));
        ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(),PATH,true);
    }
}
