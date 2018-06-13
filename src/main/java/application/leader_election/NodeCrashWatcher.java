package application.leader_election;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 1.羊群效应:若leader不是自己,则将此Watcher注册到Leader节点上
 * 2.避免羊群效应:若leader不是自己,则将此Watcher注册到比自己小的最大的节点上
 *
 * @author pfjia
 * @since 2018/6/13 19:46
 */
public class NodeCrashWatcher implements Watcher {

    private final Object sync;

    public NodeCrashWatcher(Object sync) {
        this.sync = sync;
    }

    @Override
    public void process(WatchedEvent event) {
        //若leader节点被删除,则重新进行选举算法
        if (event.getType() == Event.EventType.NodeDeleted) {
            synchronized (sync) {
                sync.notifyAll();
            }
        }
    }
}

