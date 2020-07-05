## zookeeper


## hadoop


## HBase
hbase-env.sh
```
export JAVA_HOME=
export HBASE_MANAGES_ZK=false
```

hbase-site.xml
```xml
<configuration>
    <property>
        <name>hbase.rootdir</name> 
        <value>hdfs://hadoop102:9000/hbase</value>
    </property>
    <property>
        <name>hbase.cluster.distributed</name> 
        <value>true</value>
    </property>
    <property>
        <name>hbase.master.port</name> 
        <value>16000</value>
    </property>
    <property>
        <name>hbase.zookeeper.quorum</name> 
        <value>hadoop102:2181,hadoop103:2181,hadoop104:2181</value>
    </property>
    <property>
        <name>hbase.zookeeper.property.dataDir</name> 
        <value>/opt/module/zookeeper-3.4.10/zkData</value>
    </property>
</configuration>
```

regionservers
```
hadoop102
hadoop103
hadoop104
```

软连接 hadoop 配置文件到 hbase
```sh
ln -s hadoop/core-site.xml hbase/conf/core-site.xml
ln -s hadoop/hdfs-site.xml hbase/conf/hdfs-site.xml
```

```sh
hbase-daemon.sh start master
hbase-daemon.sh start regionserver
```

时间同步
```xml
<property>
    <name>hbase.master.maxclockskew</name> 
    <value>180000</value>
</property>
```

```sh
start-hbase.sh
stop-hbase.sh
```

```
http://hadoop102:16010
```
