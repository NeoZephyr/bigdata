## 架构
### Client
包含访问 Hbase 的接口，维护对应的 cache 来加速 Hbase 的访问

### Zookeeper
保证集群中只有 1 个 master 在运行，如果 master 异常，会通过竞争机制产生新的 master 提供服务

监控 RegionServer 的状态，当 RegionSevrer 有异常的时候，通过回调的形式通知 Master

### HMaster
监控 RegionServer，处理 RegionServer 故障转移
处理 Region 的分配或转移， 为 RegionServer 分配 Region 
维护整个集群的负载均衡 
维护集群的元数据信息
通过 Zookeeper 发布自己的位置给客户端

### HRegionServer
 管理 master 为其分配的 Region
 处理来自客户端的读写请求 ，负责和底层 HDFS 的交互
 负责 Region 变大以后的拆分
负责 Storefile 的合并工作

### HDFS
底层数据存储服务

### 其他组件
Write-Ahead logs
当对 HBase 读写数据的时候，数据不是直接写进磁盘，它会在内存中保留一段时间。但把数据保存在内存中可能引起数据丢失，为了解决这个问题，数据会先写在 Write-Ahead logfile 中，然后再写入内存中

Region
HBase 表会根据 RowKey 值被切分成不同的 Region 存储在 RegionServer 中，在一个 RegionServer 中可以有多个不同的 Region

Store
HFile 存储在 Store 中，一个 Store 对应 HBase 表中的一个列族

MemStore
保存当前的数据操作，当数据保存在 WAL 中之后，RegsionServer 会在内存中存储键值对

HFile
在磁盘上保存原始数据的实际的物理文件，是实际的存储文件。StoreFile 是以 HFile 的形式存储在 HDFS 中的


## 数据结构
### RowKey
访问 Hbase 的行，只有三种方式：
1. 通过单个 RowKey 访问
2. 通过 RowKey 的 range
3. 全表扫描

### Column Family

### Cell

### TimeStamp
每个 Cell 都保存着同一份数据的多个版本，版本通过时间戳来索引。为了避免数据存在过多版本造成的的管理负担，HBASE 提供了两种数据版本回收方式：
1. 保存数据的最后 n 个版本
2. 保存最近一段时间内的版本，可以针对每个列族进行设置


## 读流程
1. 客户端访问 zookeeper，获取 meta 表的位置信息
2. 根据 namespace、表名 和 RowKey 在 meta 表中找到对应的 region 信息
3. 找到 region 对应的 regionserver，查找对应的 region
4. 先从 MemStore 找数据，如果没有，再到 BlockCache 里面读。如果还没有，再到 StoreFile 上读
5. 如果是从 StoreFile 里面读取的数据，先写入 BlockCache，再返回给客户端


## 写流程
1. 客户端向 HRegionServer 发送写请求
2. HRegionServer 将数据写到 HLog
3. HRegionServer 将数据写到内存（MemStore）


## flush
1. 当 MemStore 数据达到阈值，将数据刷到硬盘，将内存中的数据删除，同时删除 HLog 中的历史数据
2. 并将数据存储到 HDFS 中
3. 在 HLog 中做标记点


## 数据合并
1. 当数据块达到3块，Hmaster触发合并操作，Region将数据块加载到本地，进行合并；
2. 当合并的数据超过256M，进行拆分，将拆分后的Region分配给不同的HregionServer管理；
3. 当HregionServer宕机后，将HregionServer上的hlog拆分，然后分配给不同的HregionServer加载，修改.META.；
4. 注意：HLog会同步到HDFS

