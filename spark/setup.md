## 运行模式
### Local 模式
local: 所有计算都运行在一个线程中
local[k]: 运行 k 个 worker 线程
local[*]: 按照 cpu 最多 cores 来设置线程数

```sh
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master local \
--executor-memory 1G \
--total-executor-cores 2 \
./examples/jars/spark-examples_2.11-2.1.1.jar \
100
```

--master: 指定 master 的地址，默认为 local
--class: 应用启动类
--deploy-mode: 默认为 client
--conf: 配置属性，格式 key=value
--executor-memory: 每个 executor 的可用内存
--total-executor-cores: 每个 executor 的可使用 cpu 核数

```sh
spark-shell
```
```scala
sc.textFile("input").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).collect
```
```
http://192.168.9.102:4040
```

### Yarn 模式
yarn-site.xml
```xml
<!-- 是否启动一个线程检查每个任务正使用的物理内存量，如果任务超出分配值，则直接将其杀掉，默认是 true -->
<property>
    <name>yarn.nodemanager.pmem-check-enabled</name>
    <value>false</value>
</property>

<!-- 是否启动一个线程检查每个任务正使用的虚拟内存量，如果任务超出分配值，则直接将其杀掉，默认是 true -->
<property>
    <name>yarn.nodemanager.vmem-check-enabled</name>
    <value>false</value>
</property>
```
spark-env.sh
```
YARN_CONF_DIR=/app/hadoop-3.1.2/etc/hadoop
```
spark-defaults.conf
```
spark.yarn.historyServer.address=hadoop002:18080
spark.history.ui.port=18080
```
```sh
sbin/start-history-server.sh
```
```sh
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master yarn \
--deploy-mode client \
./examples/jars/spark-examples_2.11-2.1.1.jar \
100
```

spark.yarn.jars


## JobHistoryServer
记录日志信息，需要提前创建目录
```sh
mv spark-defaults.conf.template spark-defaults.conf
```
spark-defaults.conf
```
spark.eventLog.enabled           true
spark.eventLog.dir               hdfs://cdh:8020/directory
```

spark-env.sh
```
export SPARK_HISTORY_OPTS="-Dspark.history.ui.port=18080
-Dspark.history.retainedApplications=30
-Dspark.history.fs.logDirectory=hdfs://cdh:8020/directory"
```

启动历史服务
```sh
sbin/start-history-server.sh
```

停止历史服务
```sh
sbin/stop-history-server.sh
```


## 访问 hive
拷贝 hive-site.xml 到 spark 配置目录下
spark jars 目录下添加 mysql 驱动

```sh
sbin/start-thriftserver.sh
```
```sh
bin/beeline -n vagrant -u jdbc:hive2://cdh:10000
```


## HA 配置
spark-env.sh
```sh
export SPARK_DAEMON_JAVA_OPTS="
-Dspark.deploy.recoveryMode=ZOOKEEPER
-Dspark.deploy.zookeeper.url=hadoop001,hadoop002,hadoop003
-Dspark.deploy.zookeeper.dir=/spark"
```
```sh
sbin/start-all.sh
```
```sh
sbin/start-master.sh
```
```sh
/opt/module/spark/bin/spark-shell \
--master spark://hadoop001:6080,hadoop001:6080 \
--executor-memory 2g \
--total-executor-cores 2
```