package ch5.curator.modeled;

import core.Const;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.modeled.JacksonModelSerializer;
import org.apache.curator.x.async.modeled.ModelSpec;
import org.apache.curator.x.async.modeled.ModeledFramework;
import org.apache.curator.x.async.modeled.ZPath;

import java.util.concurrent.TimeUnit;

/**
 * @author pfjia
 * @since 2018/6/14 12:42
 */
public class ModeledCuratorSample {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(Const.HOSTS)
                .sessionTimeoutMs(1000)
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        client.start();
        AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);
        JacksonModelSerializer<Person> serializer = JacksonModelSerializer.build(Person.class);
        ModelSpec<Person> modelSpec = ModelSpec.builder(ZPath.parse("/example/path"), serializer).build();
        ModeledFramework<Person> modeled = ModeledFramework.wrap(async, modelSpec);
        Person model = new Person("j", "pf", 21);
        ModeledFramework<Person> atId = modeled.child(model.getId());
        atId.set(model);
        modeled.child(model.getId()).read().whenComplete((person, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println(person);
            }
        });
        System.out.println("结束");
        TimeUnit.SECONDS.sleep(1000);
    }
}
