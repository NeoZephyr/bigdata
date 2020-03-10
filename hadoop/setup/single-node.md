## 准备 java 环境


## 准备 hadoop 环境
设置环境变量
```sh
vi /etc/profile

export JAVA_HOME=
export HADOOP_HOME=
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

source /etc/profile
```

配置文件
hadoop-env.sh
```
export JAVA_HOME=
```

core-site.xml
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/app/hadoop-3.1.2/data</value>
    </property>
</configuration>
```

hdfs-site.xml
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```

slaves
```
localhost
```


## 免密钥登陆
```sh
ssh-keygen -t rsa
cat ~/.ssh/id_rsa.pub >> authorized_keys
```


## 启动集群
格式化
```sh
hdfs namenode -format
```
再次格式化 namenode，会产生新的集群 id，导致 namenode 和 datanode 的集群 id 不一致。所以，如果需要重复格式 namenode，需要先删除 data 数据和 log 日志，然后再格式化

```sh
start-dfs.sh
```

查看 namenode
```
http://lab:9870/
```

```sh
stop-dfs.sh
```


## yarn
### 配置
mapred-site.xml
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
    </property>

    <!-- 历史服务器地址 -->
    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>lab:10020</value>
    </property>
    <!-- 历史服务器 web 地址 -->
    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>lab:19888</value>
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
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>lab</value>
    </property>

    <!-- 日志聚集 -->
    <property>
        <name>yarn.log-aggregation-enable</name>
        <value>true</value>
    </property>
    <!-- 日志保留时间设置 7 天 -->
    <property>
        <name>yarn.log-aggregation.retain-seconds</name>
        <value>604800</value>
    </property>
</configuration>
```

### 启动
```
start-yarn.sh
```
```sh
# 启动历史服务器
mr-jobhistory-daemon.sh start historyserver
```


```
http://lab:8088
```

查看历史服务器
```
http://lab:19888/jobhistory
```

### 停止
```
stop-yarn.sh
```
```
mr-jobhistory-daemon.sh stop historyserver
```


## 客户端
配置 HADOOP_HOME
设置 PATH，加上 HADOOP_HOME/bin

