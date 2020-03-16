## 高可用
1. namenode 内存中各自保存一份元数据
2. 只有 Active 状态的 namenode 节点可以做写操作 edits 日志
3. 两个 namenode 节点都可以读 edits 日志
4. 共享的 edits 日志由共享存储统一管理
5. 每个运行 namenode 的主机也运行了一个 ZKFC 进程
6. ZKFC 定期检查对应主机上的 namenode 的健康状态
7. 如果本地 namenode 是健康的，且 ZKFC 发现没有其它的节点当前持有 znode 锁，就会获取该锁


### 故障转移
1. zkfc 检测到假死
2. zkfc 通知另一台 namenode 的 zkfc
3. 另一台 namenode 的 zkfc 杀死假死的 namenode，防止脑裂
4. 如果杀死假死的 namenode 失败，则调用用户自定义的脚本程序
5. 根据命令结果，激活 standby 的 namenode 为 active

### 节点安排
node01: zk, datanode, journalnode, nodemanager, namenode
node02: zk, datandoe, journalnode, nodemanager, namenode, resourcemanager
node03: zk, datanode, journalnode, nodemanager

### zookeeper
```sh
# mv zoo_sample.cfg zoo.cfg
dataDir=/app/zookeeper-3.4.10/zkData

server.1=node01:2888:3888
server.2=node02:2888:3888
server.3=node03:2888:3888
```
2888 是该服务器与集群中的 leader 服务器交换信息的端口
3888 是用来执行选举时服务器相互通信的端口
1, 2, 3 是对应服务器的 dataDir 目录下 myid 文件中的值

```sh
zkServer.sh start
```
```sh
zkServer.sh status
```

### hdfs
core-site.xml
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://mycluster</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/app/ha/hadoop-3.1.2/data/tmp</value>
    </property>
</configuration>
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
        <value>node01:9000</value>
    </property>
    <property>
        <name>dfs.namenode.rpc-address.mycluster.nn2</name>
        <value>node02:9000</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.mycluster.nn1</name>
        <value>node01:9870</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.mycluster.nn2</name>
        <value>node02:9870</value>
    </property>
    <property>
        <name>dfs.namenode.shared.edits.dir</name>
        <value>qjournal://node01:8485;node02:8485;node02:8485/mycluster</value>
    </property>

    <!-- 配置隔离机制，即同一时刻只能有一台服务器对外响应 -->
    <property>
        <name>dfs.ha.fencing.methods</name>
        <value>sshfence</value>
    </property>

    <!-- 使用隔离机制时需要 ssh 无秘钥登录-->
    <property>
        <name>dfs.ha.fencing.ssh.private-key-files</name>
        <value>/home/vagrant/.ssh/id_rsa</value>
    </property>

    <!-- 声明 journalnode 服务器存储目录-->
    <property>
        <name>dfs.journalnode.edits.dir</name>
        <value>/app/ha/hadoop-3.1.2/data/jn</value>
    </property>

    <!-- 关闭权限检查-->
    <property>
        <name>dfs.permissions.enable</name>
        <value>false</value>
    </property>

    <!-- 访问代理类：client，mycluster，active 配置失败自动切换实现方式-->
    <property>
        <name>dfs.client.failover.proxy.provider.mycluster</name>
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
    </property>
</configuration>
```

启动 journalnode 服务
```sh
hadoop-daemon.sh start journalnode
```

nn1 上
```sh
hdfs namenode -format
hadoop-daemon.sh start namenode
```

nn2 上
```sh
# 同步 nn1 的元数据信息
hdfs namenode -bootstrapStandby
hadoop-daemon.sh start namenode
```

在 nn1上，启动所有 datanode
```sh
hadoop-daemons.sh start datanode
```

将 nn1 切换为 Active
```sh
hdfs haadmin -transitionToActive nn1

hdfs haadmin -getServiceState nn1
```

自动故障转移
hdfs-site.xml
```xml
<property>
    <name>dfs.ha.automatic-failover.enabled</name>
    <value>true</value>
</property>
```
core-site.xml
```xml
<property>
    <name>ha.zookeeper.quorum</name>
    <value>node01:2181,node02:2181,node03:2181</value>
</property>
```

```sh
stop-dfs.sh
zkServer.sh start

# 初始化在 Zookeeper 中状态
hdfs zkfc -formatZK
start-dfs.sh
```

### yarn
yarn-site.xml
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
    </property>
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>cluster-yarn</value>
    </property>
    <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>node01</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>node02</value>
    </property>
    <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>node01:2181,node02:2181,node03:2181</value>
    </property>
    <property>
        <name>yarn.resourcemanager.recovery.enabled</name>
        <value>true</value>
    </property>
    <property>
        <name>yarn.resourcemanager.store.class</name>
        <value>org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore
        </value>
    </property>
</configuration>
```

```sh
hadoop-daemon.sh start journalnode

# nn1
hdfs namenode -format
hadoop-daemon.sh start namenode

# nn2，同步 nn1 的元数据信息
hdfs namenode -bootstrapStandby

# 启动 nn2
hadoop-daemon.sh start namenode

# 启动所有 DataNode
hadoop-daemons.sh start datanode

# 将 nn1 切换为Active
hdfs haadmin -transitionToActive nn1

# node01
start-yarn.sh

# node02
yarn-daemon.sh start resourcemanager

# 查看服务状态
yarn rmadmin -getServiceState rm1
```


## Federation
集群元数据保存在 namenode 内存中，内存称为系统扩展瓶颈

