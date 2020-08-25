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

