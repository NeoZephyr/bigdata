## 主要组件
### NameNode
1. 管理节点，负责管理文件系统和命名空间，存放 hdfs 的元数据
2. 元数据信息包含文件目录结构，文件名，文件属性（生成时间、副本数、文件权限），每个文件的块列表、块位置信息（datanode 上报）
3. 元数据信息以命名空间镜像文件 fsimage 和编辑日志 edits log 的方式保存

fsimage: 元数据镜像文件，保存了文件系统目录树信息以及文件和块的对应关系
edits log: 日志文件，保存文件系统的更改记录

### DataNode
存储 block，以及 block 元数据，包括数据块的长度、块数据的校验和、时间戳

### SecondaryNameNode
合并 edits log，减少 namenode 启动时间

合并过程：
1. 让 namenode 滚动 edits 并生成一个空的 edits.inprogress，以后的操作记录都写入 edits.inprogress
2. 拷贝其他未合并的 edits 和 fsimage 到 SecondaryNameNode 本地
3. 将拷贝过来的 edits 和 fsimage 加载到内存中进行合并，生成新 fsimage.chkpoint
4. 将 fsimage.chkpoint 拷贝到 namenode，重命名为 fsimage 替换掉原来的 fsimage
5. namenode 下次启动时只需要加载之前未合并的 edits 和 fsimage 即可

合并时机：
1. 根据配置文件设置的时间间隔 `fs.checkpoint.period`，默认 3600s
2. 根据配置文件设置 edits log 大小，`fs.checkpoint.size` 规定 edits log 最大值默认为 64MB


## 启动
1. namenode 启动时，首先将 fsimage 加载进入内存，并执行 edits log 中的操作
2. 一旦在内存中建立了文件系统元数据的映射，则创建一个新的 fsimage 文件和一个空的 edits log 文件
3. 此时，namenode 处于安全模式，即 namenode 的文件系统对于客户端是只读的
4. namenode 接收各个 datanode 的报告，当报告的数据块达到最小副本数以上时，会被认为是安全的。当有处于安全状态的数据块达到一定比例时，安全模式结束
5. 当检测到副本数不足的数据块时，该数据块会被复制知道达到最小副本数

```sh
# 查看安全模式状态
hdfs dfsadmin -safemode get

# 进入安全模式状态
hdfs dfsadmin -safemode enter

# 离开安全模式状态
hdfs dfsadmin -safemode leave

# 等待安全模式状态
hdfs dfsadmin -safemode wait
```


## 心跳
1. datanode 启动后向 namenode 注册
2. datanode 每隔一段时间（1 小时）向 namenode 上报所以块信息
3. datanode 每隔 3 秒向 namenode 发送一次心跳，心跳返回带有 namenode 的命令
4. 如果 namenode 超过 10 分钟没有收到 datanode 的心跳，则认为该节点不可用，并拷贝其上的 block 到其它的 datanode


## 读流程
1. 调用 `DistributedFileSystem` 的 `open` 方法，通过 rpc 调用从 NameNode 获取文件的第一批 block 的位置信息
2. 对每一个 block，客户端都根据网络拓扑，对拥有 block 副本的 DataNode 进行排序
3. `DistributedFileSystem` 选中第一个 block 所在的最近的 DataNode 进行通信，并返回 `FSDataInputStream`
4. 客户端调用 `FSDataInputStream` 的 `read` 方法进行读取操作。当读取到 block 的尾部时，`FSDataInputStream` 关闭与对应 DataNode 的连接，然后与下一个 block 的最佳 DataNode 建立连接
5. 当上一批 block 读取完成后，`FSDataInputStream` 就向 NameNode 请求下一批 block 的位置信息
6. 文件读取完成后，关闭 `FSDataInputStream`

如果读取文件过程中，与 DataNode 通信出现错误，会尝试下一个拥有 block 副本的且最佳的 DataNode。同时，会记录读取文件发生错误的 DataNode，这样后续的 block 就不用尝试读取这些失败的 DataNode 了

如果读取文件过程中，block 内容校验失败，会尝试从其它 DataNode 读取 block 副本的内容，同时也会将内容校验出错的 block 上报给 NameNode


## 写流程
1. 调用 `DistributedFileSystem` 的 `create` 方法，通过 rpc 调用告诉 NameNode 创建一个新的文件
2. NameNode 进行一系列检查：确保要创建的文件之前不存在，且客户端具有创建文件的权限。如果检查没有通过，则会抛出异常
3. `DistributedFileSystem` 返回 `FSDataOutputStream`，用来处理与 DataNode 和 NameNode 的通信
4. `FSDataOutputStream` 将要写入的数据拆分成一个个 packet，然后写入一个叫 data queue 的内部队列
5. `DataStreamer` 负责消费 data queue，并寻求 NameNode 分配一系列 DataNode 用来存储新的 block 副本。存储 block 副本的 DataNode 组成一条链路，`DataStreamer` 首先将 packet 写入链路中的第一个 DataNode，第一个 DataNode 存储 packet 并将其转发到链路中的下一个 DataNode
6. `FSDataOutputStream` 还维护了一个叫 ack queue 的内部队列，用来接收 DataNode 的通知。当链路中的所有 DataNode 都回复收到的 packet，才会从 ack queue 中移除
7. 只要保证有 `dfs.namenode.replication.min` 个副本写入，就表示写入成功。之后将在整个群集中异步复制，直到副本数达到 `dfs.replication` 为止
8. 当客户端写入结束之后，调用 `close` 关闭输出流。这个操作将所有剩余数据包刷新到链路中，然后告知 NameNode 写入完成

如果写入文件的过程中，出现了错误，将会进行以下操作：
1. 关闭 DataNode 链路，并将 ack queue 中的数据包添加到 data queue 的队首，以便故障节点下游的 DataNode 不会丢失任何数据包
2. 没有故障的 DataNode 上的当前 block 会被赋予一个新的标识，该标识被传送到 NameNode。这样，当失败的 DataNode 恢复后，则将删除失败的 DataNode 上的部分块
3. 从链路中删除失败的 DataNode，并根据剩下的 DataNode 构建新的链路，然后将剩余的 block 继续写入链路
4. NameNode 检测到 block 的副本数不够，就会安排其它 DataNode 节点创建副本


## 数据一致性
1. 即使流已刷新，也不能保证写入文件的任何内容都可见
2. 正在写入的当前块，不能被读取到，之前写入完成的块可见
3. 通过 `hflush()` 方法强制将缓存中的数据刷新到 DataNode。当 `hflush()` 返回成功后，能够保证已经写入的数据到达了链路中的所有 DataNode，并且读取可见
4. `hflush()` 不保证 DataNode 已经数据写入磁盘，只是位于 DataNode 中的内存中。如果要保证数据写入磁盘，需要调用 `hsync()` 方法
5. `clsoe` 会隐式地调用 `hflush` 方法


## 容错处理
### 数据存储故障
对于存储在 DataNode 上的数据块，计算并存储校验和（CheckSum）。在读取数据的时候，重新计算读取出来的数据的校验和，如果校验不正确就抛出异常，应用程序捕获异常后就到其他 DataNode 上读取备份数据

### 磁盘故障
DataNode 监测到本机的某块磁盘损坏，就将该块磁盘上存储的所有 BlockID 报告给 NameNode，NameNode 检查这些数据块在其它 DataNode 上的备份，通知相应的 DataNode 服务器将对应的数据块复制到其他服务器上，以保证数据块的备份数满足要求

### DataNode 故障
DataNode 会通过心跳和 NameNode 保持通信，如果 DataNode 超时未发送心跳，NameNode 就会认为这个 DataNode 已经宕机失效，立即查找这个 DataNode 上存储的数据块，以及这些数据块还存储在哪些服务器上，随后通知这些服务器再复制一份数据块到其他服务器上

datanode 掉线参数设置
dfs.namenode.heartbeat.recheck.interval 默认 300000ms
dfs.heartbeat.interval 默认 3s

### 添加 DataNode
```sh
hadoop-daemon.sh start datanode
yarn-daemon.sh start nodemanager
```
如果数据不均衡，可以用命令实现集群的再平衡
```sh
start-balancer.sh
```

### 白名单
添加到白名单的主机节点，都允许访问 namenode，不在白名单的主机节点，都会被退出
hdfs-site.xml
```xml
<property>
    <name>dfs.hosts</name>
    <value>/app/hadoop/etc/hadoop/dfs.hosts</value>
</property>
```
刷新 namenode
```sh
hdfs dfsadmin -refreshNodes
```
更新 resource manager 节点
```sh
yarn rmadmin -refreshNodes
```
```sh
start-balancer.sh
```

### 黑名单
在黑名单上面的主机都会被退役。但是，如果服役的节点小于副本数，则会退役失败
hdfs-site.xml
```xml
<property>
    <name>dfs.hosts.exclude</name>
    <value>/app/hadoop/etc/hadoop/dfs.hosts.exclude</value>
</property>
```
刷新 namenode
```sh
hdfs dfsadmin -refreshNodes
```
更新 resource manager 节点
```sh
yarn rmadmin -refreshNodes
```
```sh
start-balancer.sh
```

```sh
hadoop-daemon.sh stop datanode
yarn-daemon.sh stop nodemanager
```

### 多目录
```xml
<property>
    <name>dfs.datanode.data.dir</name>
    <value>file:///${hadoop.tmp.dir}/dfs/data1,file:///${hadoop.tmp.dir}/dfs/data2</value>
</property>
```

### NameNode 故障
集群部署两台 NameNode 服务器，一台作为主服务器提供服务，一台作为从服务器进行热备，两台服务器通过 ZooKeeper 选举，主要是通过争夺 znode 锁资源，决定谁是主服务器。DataNode 会向两个 NameNode 同时发送心跳数据，但是只有主 NameNode 才能向 DataNode 返回控制信息

正常运行期间，主从 NameNode 之间通过一个共享存储系统 shared edits 来同步文件系统的元数据信息。当主 NameNode 服务器宕机，从 NameNode 会通过 ZooKeeper 升级成为主服务器，并保证 HDFS 集群的元数据信息完整一致


## 集群间数据拷贝
```sh
hadoop distcp hdfs://ns1:9000/user hdfs://ns2:9000/user
```


## 小文件处理
```sh
# 归档
hadoop archive -archiveName input.har –p /input /input_archive

hadoop fs -lsr har:///input_archive/input.har

# 解档
hadoop fs -cp har:///input_archive/input.har/* /input

hadoop jar <appJar> wordcount har:///input_archive/input.har /archive_out
```


## 垃圾回收


## hdfs 优点
1. 高容错性
2. 适合批处理
3. 适合大数据处理
4. 可构建在廉价机器上


## hdfs 缺点
1. 低延迟数据访问
2. 小文件存取
3. 并发写入、文件随机修改


