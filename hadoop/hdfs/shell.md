## 命令行
### 文件操作
查看文件列表，其中第二列表示文件的副本因子。需要注意的是，目录的副本因子显示的是空，因为目录作为元数据存储在 namenode 上
```sh
hdfs dfs -ls /
hdfs dfs -ls -R /
```

```sh
hdfs dfs -mkdir /test
```
```sh
hdfs dfs -put words.txt /test
hdfs dfs -get /test/words.txt ./tmp.txt

hdfs dfs -text /test/words.txt
hdfs dfs -cat /test/words.txt

# 追加文件内容
hdfs dfs -appendToFile tmp.txt /test/words.txt

hdfs dfs -mv /test/words.txt /test/word.txt
hdfs dfs -rm /test/words.txt
```

### 运行程序
```sh
hadoop jar hadoop-mapreduce-examples-3.1.2.jar wordcount /test/words.txt /test/output
```

### 配置操作
```sh
# 修改 block 大小
hdfs dfs -D dfs.blocksize=1048576 -put words.txt /test
```

```sh
# 获取 namenode 的节点名称
hdfs getconf -namenodes

# 获取 hdfs 最小块信息
hdfs getconf -confkey dfs.namenode.fs-limits.min-block-size

# 查找 hdfs 的 namenode 的 rpc 地址
hdfs getconf -nnRpcAddresses
```

```sh
# 查看当前模式
hdfs dfsadmin -safemode get

# 进入安全模式
hdfs dfsadmin -safemode enter
```

```sh
# 显示 hdfs 块信息
hdfs fsck / -files -blocks -locations
```

```sh
# 并行拷贝文件
hadoop distcp src dest

hadoop distcp -update dir1 dir2
hadoop distcp -overwrite dir1 dir2

# hdfs 集群之间传递
# -delete 表示删除目标目录存在，源目录不存在的文件
hadoop distcp -update -delete -p hdfs://namenode1/foo hdfs://namenode2/foo
```