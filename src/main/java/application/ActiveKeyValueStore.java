package application;

import core.Const;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/13 13:45
 */
public class ActiveKeyValueStore extends ConnectionWatcher {
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_PERIOD_SECONDS = 10;

    public void write(String path, String value) throws KeeperException, InterruptedException {
        int retries = 0;
        while (true) {
            try {
                Stat stat = zk.exists(path, false);
                if (stat == null) {
                    zk.create(path, value.getBytes(Const.CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } else {
                    zk.setData(path, value.getBytes(Const.CHARSET), stat.getVersion());
                }
                return;
            } catch (KeeperException.SessionExpiredException e) {
                //当session过期后,zookeeper变为CLOSED状态,无法重新连接
                throw e;
            } catch (KeeperException e) {
                if (retries++ == MAX_RETRIES) {
                    throw e;
                }
                TimeUnit.SECONDS.sleep(RETRY_PERIOD_SECONDS);
            }
        }
    }

    public String read(String path, Watcher watcher) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(path, watcher, null);
        return new String(data, Const.CHARSET);
    }
}
