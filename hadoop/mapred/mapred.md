## 优点
1. 易于编程
2. 良好的扩展性
3. 高容错性
4. 适合海量数据的离线处理


## 缺点
1. 不擅长实时计算
2. 不擅长流式计算
3. 不擅长 DAG 计算


## 序列化
java 序列化是一个重量级序列号框架，一个对象被序列化之后，包含了很多额外信息，不便于在网络中高效传输。hadoop 的序列化有以下特点：
1. 高效的使用存储空间
2. 读写数据的额外开销小
3. 可随着通信协议的升级而升级
4. 支持多语言的交互


## InputFormat
### FileInputFormat
### TextInputFormat
### KeyValueTextInputFormat
### NLineInputFormat
### SequenceFileInputFormat
### CombineTextInputFormat


## OutputFormat
### FileOutputFormat
### MapFileOutputFormat
### SequenceFileOutputFormat
### TextOutputFormat


## map
1. 客户端获取待处理数据的信息，然后根据参数配置，形成一个任务分配规划
2. 客户端提交切片信息
3. yarn 计算出 map task 数量
4. InputFormat 返回 RecordReader，从输入 InputSplit 解析出一个个 kv 值
5. map task 对 kv 值计算后，生成新的 kv 值
6. outputCollector 将结果输出到环形缓存区，内存满后，数据会溢出到磁盘
7. 内存缓冲区不断溢出，产生多个文件。在写入磁盘的过程中，对数据分区并快速排序。如果设置了 combiner，在写入磁盘之前，还会对每个分区中的数据进行一次聚集操作
8. 将多个溢出文件进行归并排序（先比较分区、分区相同再比较 k 值）。如果设置了 combiner，还会经过 combiner 处理。最终，合并成一个数据文件


## reduce
1. reduce task 根据自己的分区号，从各个 map task 的机器上下载对应分区的数据
2. 将来自不同 map task 的，属于同一分区的文件进行归并排序
3. 合并之后，reduce task 进行逻辑运算
4. 计算结果写到 hdfs 上


## shuffle
### map
1. map task 将计算结果输出到缓存
2. 缓存满后，对数据进行分区并排序
3. 然后通过 combiner 进行分区内合并
4. 将多个 map task 的输出按照分区进行归并排序，合并
5. 再次通过 combiner 进行分区内合并
6. 将结果压缩写入磁盘

### reduce
1. reduce task 将所有 map task 的输出中的对应分区数据下载到 reduce task 本地磁盘
2. 将下载下来的多个文件归并排序，合并
3. 按照 key 进行分组
4. reduce task 读取一组数据处理并输出



## 数据切片与并行度
1. 默认情况下切片大小为 blocksize
2. 切片不考虑数据集整体，而是逐个针对每一个文件单独切片
3. 每一个 split 切片分配一个 map task 进行处理
4. map task 并行度由客户端在提交 job 时决定


## 数据压缩
### 压缩原则
1. 运算密集型 job，少用压缩
2. IO 密集型 job，多用压缩

### 压缩方式
#### gzip
优点：压缩率高，压缩速度快；hadoop 支持；linux 自带命令
缺点：不支持 split

#### bzip2
优点：支持 split；具有很高的压缩率；hadoop 支持
缺点：压缩、解压缩慢

#### leo
优点：压缩、解压缩比较快，压缩率合理；支持 split
缺点：压缩率比 gzip 低；hadoop 不支持

#### snappy
优点：压缩速度快，压缩率合理
缺点：不支持 split；压缩率比 gzip 低；hadoop 不支持


## 优化
### 数据输入
1. 合并小文件
2. 采用 CombineTextInputFormat 作为输入

### map
1. 减少溢写次数，从而减少磁盘 io
2. 减少合并次数
3. 先进行 combine 处理，减少 io

### reduce
1. 合理设置 map 和 reduce 数
2. 设置 map 和 reduce 共存
3. 规避使用 reduce
4. 合理设置 reduce 端的 buffer

