RAID（独立磁盘冗余阵列）技术是将多块普通磁盘组成一个阵列，共同对外提供服务。主要是为了改善磁盘的存储容量、读写速度，增强磁盘的可用性和容错能力。

假设服务器有 N 块磁盘

RAID0 是数据在从内存缓冲区写入磁盘时，根据磁盘数量将数据分成 N 份，这些数据同时并发写入 N 块磁盘，使得数据整体写入速度是一块磁盘的 N 倍，读取的时候也一样。因此 RAID0 具有极快的数据读写速度。但是 RAID0 不做数据备份，N 块磁盘中只要有一块损坏，数据完整性就被破坏

RAID1 是数据在写入磁盘时，将一份数据同时写入两块磁盘，这样任何一块磁盘损坏都不会导致数据丢失，插入一块新磁盘就可以通过复制数据的方式自动修复，具有极高的可靠性

结合 RAID0 和 RAID1 两种方案构成了RAID10，它是将所有磁盘 N 平均分成两份，数据同时在两份磁盘写入，相当于 RAID1；在每一份 N/2 的磁盘里面，利用 RAID0 技术并发读写，这样既提高可靠性又改善性能。不过 RAID10 的磁盘利用率较低，有一半的磁盘用来写备份数据


RAID3 可以在数据写入磁盘的时候，将数据分成 N-1 份，并发写入 N-1 块磁盘，并在第 N 块磁盘记录校验数据，这样任何一块磁盘损坏（包括校验数据磁盘）， 都可以利用其他 N-1 块磁盘的数据修复

但是在数据修改较多的场景中，任何磁盘数据的修改，都会导致第 N 块磁盘重写校验数据。频繁写入的后果是第 N 块磁盘比其他磁盘更容易损坏，需要频繁更换

RAID5 和 RAID3 很相似，但是校验数据不是写入第 N 块磁盘，而是螺旋式地写入所有磁盘中。这样校验数据的修改也被平均到所有磁盘上，避免 RAID3 频繁写坏一块磁盘的情况

RAID6 和 RAID5 类似，但是数据只写入 N-2 块磁盘，并螺旋式地在两块磁盘中写入校验信息




HDFS 的高可用设计
1. 数据存储故障容错：对于存储在 DataNode 上的数据块，计算并存储校验和(CheckSum)。在读取 数据的时候，重新计算读取出来的数据的校验和，如果校验不正确就抛出异常，应用程序捕 获异常后就到其他 DataNode 上读取备份数据
2. 磁盘故障容错：如果 DataNode 监测到本机的某块磁盘损坏，就将该块磁盘上存储的所有 BlockID 报告给 NameNode，NameNode 检查这些数据块还在哪些 DataNode 上有备份，通知相应的 DataNode 服务器将对应的数据块复制到其他服务器上，以保证数据块的备份数满足要求
3. DataNode 故障容错：DataNode 会通过心跳和 NameNode 保持通信，如果 DataNode 超时未发送心跳， NameNode 就会认为这个 DataNode 已经宕机失效，立即查找这个 DataNode 上存储的 数据块有哪些，以及这些数据块还存储在哪些服务器上，随后通知这些服务器再复制一份数 据块到其他服务器上，保证 HDFS 存储的数据块备份数符合用户设置的数目，即使再出现 服务器宕机，也不会丢失数据
4. NameNode 故障容错：如果 NameNode 故障，整个 HDFS 系统集群都无 法使用;如果 NameNode 上记录的数据丢失，整个集群所有 DataNode 存储的数据也就 没用了。


NameNode 高可用容错能力非常重要。NameNode 采用主从热备的方式提供高可 用服务
集群部署两台 NameNode 服务器，一台作为主服务器提供服务，一台作为从服务器进行 热备，两台服务器通过 ZooKeeper 选举，主要是通过争夺 znode 锁资源，决定谁是主服 务器。而 DataNode 则会向两个 NameNode 同时发送心跳数据，但是只有主 NameNode 才能向 DataNode 返回控制信息。
正常运行期间，主从 NameNode 之间通过一个共享存储系统 shared edits 来同步文件系 统的元数据信息。当主 NameNode 服务器宕机，从 NameNode 会通过 ZooKeeper 升 级成为主服务器，并保证 HDFS 集群的元数据信息，也就是文件分配表信息完整一致






mvn clean package -DskipTests

create maven project

1. split
2. map
3. shuffle
4. reduce

1. InputFormat
2. FileInputFormat
3. TextInputFormat
4. Split
5. RecordReader
6. map
7. partitioner
8. sort
9. reduce
10. OutputFormat

InputFormat: 将输入数据进行分片

MapperClass
ReducerClass

mvn clean package -DskipTests
hadoop jar a.jar MainClass hdfs://node01:8200/hello.txt hdfs://node01:8200/output

本地 reducer -> Combiner
根据使用场景使用

partitioner



TextInputFormat

```xml
<repositories>
    <repository>
        <id>cloudera</id>
        <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
    </repository>
</repositories>

<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-client</artifactId>
    <version>${hadoop.version}</version>
</dependency>
```

```java
String source = "/opt/a.mp4";
String dest = "hdfs://hadoop01:9000/data/a.mp4";
InputStream is = new BufferedInputStream(new FileInputStream(source));
Configuration conf = new Configuration();
FileSystem fs = FileSystem.get(URI.create(dest), conf);
OutputStream os = fs.create(new Path(dest));
IOUtils.copyBytes(is, os, 4096, true);
```
```java
String source = "hdfs://hadoop01:9000/data/a.mp4";
Configuration conf = new Configuration();
FileSystem fs = FileSystem.get(URI.create(source), conf);
FSDataInputStream is = fs.open(new Path(source));
BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/opt/b.mp4"));
IOUtils.copyBytes(is, bos, 4096, true);
```

存储大量小文件
```sh
hadoop archive -archiveName test.har -p /test -r 3 th1 th2 /outhar
hdfs dfs -ls -R har:///outhar/test.har

hdfs dfs -cp har:///outhar/test.har/th1 hdfs:/unarchivef
hadoop fs -ls /unarchivef

hadoop distcp har:///outhar/test.har/th1 hdfs:/unarchivef
```

SequenceFile
```java
String[] data = {"hello", "world"};
Configuration conf = new Configuration();
FileSystem fs = FileSystem.get(URI.create(uri), conf);
Path path = new Path(uri);
IntWritable key = new IntWritable();
Text value = new Text();
SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), value.getClass());

for (int i = 0; i < 100; ++i) {
    key.set(100 - i);
    value.set(data[i % data.length]);
    writer.append(key, value);
}

IOUtils.closeStream(writer);
```
```java
Configuration conf = new Configuration();
FileSystem fs = FileSystem.get(URI.create(uri), conf);
Path path = new Path(uri);
SequenceFile.Reader reader = null;
reader = new SequenceFile.Reader(fs, path, conf);
Writable key = (Writable)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
Writable value = (Writable)ReflectionUtils.newInstance(reader.getValueClass(), conf);
long pos = reader.getPosition();

while (reader.next(key, value)) {
    pos = reader.getPosition();
}

IOUtils.closeStream(reader);
```


java api 创建文件，采用 hadoop 自己的副本系数
hdfs 命令创建文件，采用设置的副本系数


```java
Configuration conf = new Configuration(true);
FileSystem fs = FileSystem.get(conf);

fs.close();
```
```java
Path path = new Path("/test");

if (fs.exists(path)) {
    fs.delete(path, true);
}
fs.mkdirs(path);
```
```java
Path path = new Path("/test");
FSDataOutputStream output = fs.create(path);
InputStream is = new BufferedInputStream(new FileInputStream(new File("")));
IOUtils.copyBytes(input, output, conf, true);
```
```java
FileStatus fileStatus = fs.getFileStatus(path);
BlockLocation[] blocks = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());

FSDataInputStream in = fs.open(path);
in.seek(2048);
```

Resource Manager
Node Manager

ApplicationManager
Container

Node Manager 与 Data Node 一比一

```sh
cp mapred-site.xml.template mapred-site.xml
```
mapred-site.xml
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
yarn-site.xml
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
    </property>
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>cluster1</value>
    </property>
    <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>node3</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>node4</value>
    </property>
    <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>node02:2181,node03:2181,node04:2181</value>
    </property>
</configuration>
```
```sh
start-yarn.sh
yarn-daemon.sh start resourcemanager
```

```java
public static void main(String[] args) {
    Configuration conf = new Configuration(true);
    Job job = Job.getInstance();
    // Job job = Job.getInstance(conf, MyJob.class.getSimpleName());
    job.setJarByClass(MyJob.class);
    job.setJobName("myjob");

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // job.setInputPath(new Path("in"));
    // job.setOutputPath(new Path("out"));

    // FileInputFormat.setInputPaths(job, new Path("in"));
    // FileOutputFormat.setOutputPaths(job, new Path("out"))''

    Path input = new Path("")
    Path output = new Path("");

    if (output.getFileSystem(conf).exists(output)) {
        output.getFileSystem(conf).delete(output, true);
    }

    FileInputFormat.addInputPath(job, input);
    FileOutputFormat.setOutputPath(job, output);

    job.setMapperClass(MyMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);

    // job.setCombinerClass(MyReducer.class);
    job.setReducerClass(MyReducer.class);

    // job.setOutputKeyClass(Text.class);
    // job.setOutputValueClass(IntWritable.class);

    job.waitForCompletion(true);
}
```
```java
public class MyMapper extends Mapper<Object, Text, Text, IntWritable> {
    private static final IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context) {
        StringTokenizer token = new StringTokenizer(value.toString());
        while (token.hasMoreTokens()) {
            word.set(token.nextToken());
            context.write(word, one);
        }
    }
}
```
```java
public class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context) {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}
```
mapred.xml -> local

```sh
hadoop jar example.jar com.pain.flame.hadoop.MyJob
```

Combine

```java
fs.getFileBlockLocations(file, 0, length);
FileInputFormat.getSplits(JobContext job);
```
