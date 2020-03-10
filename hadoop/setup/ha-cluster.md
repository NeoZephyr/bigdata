QJM

## 高可用与联邦
集群元数据保存在 namenode 内存中，内存称为系统扩展瓶颈

HA
NameNode1
NameNode2
DataNode
ZK
ZKFC1
ZKFC2
JNN

1. nameservices 与物理节点的映射
2. JournalNode 位置信息配置
3. 故障切换，NameNode1 与 NameNode2 之间免密钥

zookeeper 集群搭建
```sh
export ZOOKEEPER_HOME=
export PATH=$PATH:$ZOOKEEPER_HOME/bin
```
zoo.cfg
```sh
dataDir=
clientPort=

server.1=192.168.9.12:2888:3888
server.2=192.168.9.13:2888:3888
server.3=192.168.9.14:2888:3888
```
```sh
echo id > dataDir/myid
```
```sh
zkServer.sh start
zkServer.sh status
```

hdfs-site.xml
```xml
<configuration>
    <property>
        <name>dfs.nameservices</name>
        <value>mycluster</value>
    </property>
    <property>
        <name>dfs.ha.namenodes.mycluster</name>
        <value>nn1,nn2</value>
    </property>
    <property>
        <name>dfs.namenode.rpc-address.mycluster.nn1</name>
        <value>node01:8020</value>
    </property>
    <property>
        <name>dfs.namenode.rpc-address.mycluster.nn2</name>
        <value>node02:8020</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.mycluster.nn1</name>
        <value>node01:50070</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.mycluster.nn2</name>
        <value>node02:50070</value>
    </property>

    <!-- journal node -->
    <property>
        <name>dfs.namenode.shared.edits.dir</name>
        <value>qjournal://node01:8485;node02:8485;node03:8485/mycluster</value>
    </property>
    <property>
        <name>dfs.journalnode.edits.dir</name>
        <value>/</value>
    </property>

    <!-- 故障转移 -->
    <property>
        <name>dfs.client.failover.proxy.provider.mycluster</name>
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
    </property>
    <property>
        <name>dfs.ha.fencing.methods</name>
        <value>sshfence</value>
    </property>
    <property>
        <name>dfs.ha.fencing.ssh.private-key-files</name>
        <value>....ssh/id_rsa</value>
    </property>

    <property>
        <name>dfs.ha.automatic-failover.enabled</name>
        <value>true</value>
    </property>
</configuration>
```

core-site.xml
```xml
<configuration>
    <!-- 指定 namenode 的 hostname -->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://mycluster</value>
    </property>
    <!--hadoop集群临时数据存储目录-->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/opt/tmp/ha</value>
    </property>

    <property>
        <name>ha.zookeeper.quorum</name>
        <value>node02:2181;node03:2181;node04:2181</value>
    </property>
</configuration>
```

NameNode1 与 NameNode2 之间免密钥

启动 JournalNode
```sh
hadoop-daemon.sh start journalnode
```
格式化
```sh
hdfs namenode -format
```
NameNode1
```sh
hdfs-daemon.sh start namenode
```
NameNode2
```sh
hdfs namenode -bootstrapStandby
```
格式化 zookeeper
```sh
hdfs zkfc -formatZK
```
```sh
start-dfs.sh
```