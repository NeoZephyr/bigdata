package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ApiTest {

    private ZooKeeper zooKeeper;
    private static final String CONNECT_STRING = "localhost:2181";
    private static final int SESSION_TIMEOUT = 2000;

    @Before
    public void setup() throws IOException {
        zooKeeper = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("default callback");
            }
        });
    }

    @After
    public void teardown() throws InterruptedException {
        if (zooKeeper != null) {
            zooKeeper.close();
        }
    }

    @Test
    public void ls() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("ls watcher callback");
            }
        });

        for (String child : children) {
            System.out.println(String.format(child));
        }
    }

    @Test
    public void create() throws KeeperException, InterruptedException {
        String znode = zooKeeper.create("/app", "app content".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println(znode);
    }

    @Test
    public void get() throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/app", true, new Stat());

        System.out.println(new String(data));
    }

    @Test
    public void set() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.setData("/app", "app hello".getBytes(), 0);

        System.out.println(stat.getDataLength());
    }

    @Test
    public void stat() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists("/app", false);

        System.out.println(stat.getDataLength());
    }

    @Test
    public void delete() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists("/app", false);

        if (stat != null) {
            zooKeeper.delete("/app", stat.getVersion());
        }
    }

    @Test
    public void register() throws KeeperException, InterruptedException {
        register("/app");
    }

    private void register(final String path) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    register(path);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, null);

        System.out.println(new String(data));
    }
}
