## 任务
Application、Job、Stage、Task 每一层都是 1 对多的关系

### Application
初始化一个 SparkContext 即生成一个 Application

### Job
一个 Action 算子就会生成一个 Job

### Stage
根据 RDD 之间的依赖关系的不同将 Job 划分成不同的 Stage。对于窄依赖，partition 的转换处理在 Stage 中完成计算。对于宽依赖，由于有 Shuffle 的存在，只能在 parent RDD 处理完成后，才能开始接下来的计算，因此宽依赖是划分 Stage 的依据

### Task
Stage 是一个 TaskSet，将 Stage 划分的结果发送到不同的 Executor 执行即为一个 Task


## RDD 缓存
RDD 通过 persist 方法或 cache 方法可以将前面的计算结果缓存，默认情况下 persist 会把数据以序列化的形式缓存在 JVM 的堆空间中。这两个方法被调用时不会立即缓存，而是触发后面的 action 时，该 RDD 将会被缓存在计算节点的内存中，并供后面重用

RDD 缓存有可能丢失，或者存储存储于内存的数据由于内存不足而被删除。RDD 缓存容错机制保证了即使缓存丢失也能保证计算的正确执行。通过基于 RDD 的一系列转换，丢失的数据会被重算。由于 RDD 的各个 Partition 是相对独立的，因此只需要计算丢失的部分即可，并不需要重算全部 Partition


## RDD CheckPoint
lineage 过长会造成容错成本过高，如果之后有节点出现问题而丢失分区，从做检查点的 RDD 开始重做 lineage，就会减少开销。检查点通过将数据写入到 HDFS 文件系统实现。

在 checkpoint 的过程中，该 RDD 的所有依赖于父 RDD 中的信息将全部被移除。对 RDD 进行 checkpoint 操作并不会马上被执行，必须执行 Action 操作才能触发




当 Spark 读取 HDFS 文件作为输入时，会根据具体数据格式对应的 InputFormat 进行解析，形成分片 InputSplit

为输入分片生成具体的 Task，InputSplit 与 Task 是一一对应的

将 Task 分配到集群上的某个节点的某个 Executor 去执行，每个节点可以起一个或多个 Executor，每个 Executor 由若干 core 组成，每个 Executor 的每个 core 一次只能执行一个 Task

每个 Task 执行的结果就是生成了目标 RDD 的一个 partiton

Task 执行并发度 = Executor 数目 * Executor 核数