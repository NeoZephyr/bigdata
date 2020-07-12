package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.util.Arrays;

public class ApiTest {

    FileSystem fileSystem = null;
    Configuration configuration = null;

    public static final String HDFS_URL = "hdfs://cdh:8020";

    @Before
    public void setUp() throws Exception {
        System.out.println("======== setUp ========");
        configuration = new Configuration();
        fileSystem = FileSystem.get(new URI(HDFS_URL), configuration, "vagrant");
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
        configuration = null;
        fileSystem = null;
        System.out.println("======== tearDown ========");
    }

    @Test
    public void mkdir() throws Exception {
        fileSystem.mkdirs(new Path("/test"));
    }

    @Test
    public void create() throws IOException {
        // default replication: 3
        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("/test/hello.txt"));
        fsDataOutputStream.write("hello hdfs".getBytes());
        fsDataOutputStream.flush();
        fsDataOutputStream.close();
    }

    @Test
    public void read() throws IOException {
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path("/test/hello.txt"));
        IOUtils.copyBytes(fsDataInputStream, System.out, 1024);
        System.out.println();
        fsDataInputStream.close();
    }

    @Test
    public void rename() throws IOException {
        Path srcPath = new Path("/test/tmp.md");
        Path destPath = new Path("/test/note.md");
        fileSystem.rename(srcPath, destPath);
    }

    @Test
    public void copyFromLocalFile() throws IOException {
        Path srcPath = new Path("/Users/pain/Documents/bigdata/hadoop/hadoop-learning/src/test/java/hdfs/ApiTest.java");
        Path destPath = new Path("/test");
        fileSystem.copyFromLocalFile(srcPath, destPath);
    }

    @Test
    public void copyFromLocalFileWithProcess() throws IOException {
        InputStream inputStream = new BufferedInputStream(
                new FileInputStream(
                        new File("/Users/pain/Downloads/package/kafka_2.12-2.3.0.tgz")));

        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("/hdfs/kafka_2.12-2.3.0.tgz"), new Progressable() {
            @Override
            public void progress() {
                System.out.print(".");
            }
        });

        IOUtils.copyBytes(inputStream, fsDataOutputStream, 4096);
    }

    @Test
    public void copyToLocalFile() throws IOException {
        Path src = new Path("/hdfs/test/hi");
        Path dest = new Path("/Users/pain/Documents/bigdata/hadoop/hadoop-learning/hi.txt");
        fileSystem.copyToLocalFile(src, dest);
    }

    @Test
    public void listFileStatus() throws IOException {
        FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/hello"));

        for (FileStatus fileStatus : fileStatuses) {
            boolean directory = fileStatus.isDirectory();
            long len = fileStatus.getLen();
            short replication = fileStatus.getReplication();
            String path = fileStatus.getPath().toString();

            System.out.println(String.format("类型：%s\t路径：%s\t副本数：%s\t文件大小：%s", directory ? "文件夹" : "文件", path, replication, len));
        }
    }

    @Test
    public void listFile() throws IOException {
        RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(new Path("/"), true);

        while (iterator.hasNext()) {
            LocatedFileStatus fileStatus = iterator.next();

            boolean directory = fileStatus.isDirectory();
            long len = fileStatus.getLen();
            short replication = fileStatus.getReplication();
            String path = fileStatus.getPath().toString();

            System.out.println(String.format("类型：%s\t路径：%s\t副本数：%s\t文件大小：%s", directory ? "文件夹" : "文件", path, replication, len));

            BlockLocation[] blockLocations = fileStatus.getBlockLocations();

            System.out.println("=== block info ===");
            for (BlockLocation blockLocation : blockLocations) {
                System.out.println(Arrays.toString(blockLocation.getHosts()));
            }
        }
    }

    @Test
    public void listBlockInfo() throws IOException {
        FileStatus fileStatus = fileSystem.getFileStatus(new Path("/hdfs/hadoop.txt"));
        BlockLocation[] fileBlockLocations = fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());

        for (BlockLocation fileBlockLocation : fileBlockLocations) {
            System.out.println(String.format("name: %s, offset: %d, length: %d, hosts: %s",
                    Arrays.toString(fileBlockLocation.getNames()), fileBlockLocation.getOffset(), fileBlockLocation.getLength(), Arrays.toString(fileBlockLocation.getHosts())));
        }
    }

    @Test
    public void delete() throws IOException {
        fileSystem.delete(new Path("/user"), true);
    }
}
