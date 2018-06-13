package application;

import core.Const;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/13 13:49
 */
public class ConfigUpdater {
    public static final String PATH = "/config";
    private ActiveKeyValueStore store;
    private Random random = new Random();

    public ConfigUpdater(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    public void run() throws KeeperException, InterruptedException {
        while (true) {
            String value = random.nextInt(100) + "";
            store.write(PATH, value);
            System.out.printf("Set %s to %s\n", PATH, value);
            TimeUnit.SECONDS.sleep(random.nextInt(10));
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        ConfigUpdater configUpdater = new ConfigUpdater(Const.HOSTS);
        configUpdater.run();
    }
}
