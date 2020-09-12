## Driver
Driver 是运行 main 方法的程序，负责创建 SparkContext

主要负责以下任务：
1. 把用户程序转为作业
2. 跟踪 Executor 的运行状况
3. 为执行器节点调度任务
4. UI 展示应用运行状况


## Executor
Executor 是一个工作进程，负责在 Spark 作业中运行任务，任务间相互独立。Spark 应用启动时，Executor 节点被同时启动，并且始终伴随着整个 Spark 应用的生命周期而存在。如果有 Executor 节点发生了故障或崩溃，Spark 应用也可以继续执行，会将出错节点上的任务调度到其他 Executor 节点上继续运行

主要负责以下任务：
1. 负责运行组成 Spark 应用的任务，并将结果返回给驱动器进程
2. 通过自身的块管理器为用户程序中要求缓存的 RDD 提供内存式存储。RDD 是直接缓存在 Executor 进程内的，因此任务可以在运行时充分利用缓存数据加速运算


## 任务
Application、Job、Stage、Task 每一层都是 1 对多的关系

Application: 初始化一个 SparkContext 即生成一个 Application。包含一个 driver 程序和多个 executor
Job: 一个 Action 算子就会生成一个 Job
Stage: 根据 RDD 之间的依赖关系的不同将 Job 划分成不同的 Stage。对于窄依赖，partition 的转换处理在 Stage 中完成计算。对于宽依赖，由于有 Shuffle 的存在，只能在 parent RDD 处理完成后，才能开始接下来的计算，因此宽依赖是划分 Stage 的依据
Task: Stage 是一个 TaskSet，将 Stage 划分的结果发送到不同的 Executor 执行即为一个 Task


## RDD 特性
A list of partitions
```scala
protected def getPartitions: Array[Partition]
```

A function for computing each split/partition
```scala
def compute(split: Partition, context: TaskContext): Iterator[T]
```

A list of dependencies on other RDDs
```scala
protected def getDependencies: Seq[Dependency[_]] = deps
```

Optionally, a Partitioner for key-value RDDs
```scala
@transient val partitioner: Option[Partitioner] = None
```

Optionally, a list of perferred location to compute each split on
```scala
protected def getPreferredLocations(split: Partition): Seq[String] = Nil
```

不可变

One task per partition


## RDD 操作
1. transformation are lazy, nothing actually happens until an action is called
2. action triggers the computation
3. action returns values to driver or writes data to external storage

RDD 通过 persist 方法或 cache 方法可以将前面的计算结果缓存，默认情况下 persist 会把数据以序列化的形式缓存在 JVM 的堆空间中。这两个方法被调用时不会立即缓存，而是触发后面的 action 时，该 RDD 将会被缓存在计算节点的内存中，并供后面重用

RDD 缓存有可能丢失，或者存储存储于内存的数据由于内存不足而被删除。RDD 缓存容错机制保证了即使缓存丢失也能保证计算的正确执行。通过基于 RDD 的一系列转换，丢失的数据会被重算。由于 RDD 的各个 Partition 是相对独立的，因此只需要计算丢失的部分即可，并不需要重算全部 Partition

RDD 调用 unpersist 移除缓存，立即执行


## RDD lineage
窄依赖：一个父 RDD 的 partition 最多被子 RDD 的某个 partition 使用一次 
宽依赖：一个父 RDD 的 partition 会被子 RDD 的 partition 使用多次

lineage 过长会造成容错成本过高，如果之后有节点出现问题而丢失分区，从做检查点的 RDD 开始重做 lineage，就会减少开销。检查点通过将数据写入到 HDFS 文件系统实现

在 checkpoint 的过程中，该 RDD 的所有依赖于父 RDD 中的信息将全部被移除。对 RDD 进行 checkpoint 操作并不会马上被执行，必须执行 Action 操作才能触发


## 序列化


## 广播变量


## 数据本地性


当 Spark 读取 HDFS 文件作为输入时，会根据具体数据格式对应的 InputFormat 进行解析，形成分片 InputSplit

为输入分片生成具体的 Task，InputSplit 与 Task 是一一对应的

将 Task 分配到集群上的某个节点的某个 Executor 去执行，每个节点可以起一个或多个 Executor，每个 Executor 由若干 core 组成，每个 Executor 的每个 core 一次只能执行一个 Task

每个 Task 执行的结果就是生成了目标 RDD 的一个 partiton

Task 执行并发度 = Executor 数目 * Executor 核数


./spark-submit --master local[2] --name spark0301 /home/hadoop/script/spark0301.py


## 调度
```
time=`date --date='1 day ago' +%Y%m%d`
echo $time
```

```
time=20181007
${SPARK_HOME}/bin/spark-submit \
--class com.pain.sea.log.LogApp \
--master local \
--jars /home/hadoop/lib/kudu-client-1.7.0.jar,/home/hadoop/lib/kudu-spark2_2.11-1.7.0.jar \
--conf spark.time=$time \
--conf spark.raw.path="hdfs://cdh:8020/pk/access/$time" \
--conf spark.ip.path="hdfs://cdh:8020/pk/access/ip.txt" \
/home/hadoop/lib/sparksql-train-1.0.jar
```

crontab -e 编辑
crontab -l 查看
crontab -r 删除


## 调优
--executor-memory MEM    1G

--executor-cores NUM     1

--num-executors          2

--queue                  root.用户名


map端缓冲区大小: spark.shuffle.file.buffer
reduce端拉取数据缓冲区大小: spark.reducer.maxSizeInFlight
reduce端拉取数据重试次数：spark.shuffle.io.maxRetries
reduce端拉取数据等待间隔：spark.shuffle.io.retryWait

