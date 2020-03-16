## 搭建环境
### 停止防火墙
```sh
# 停止防火墙
systemctl stop firewalld

# 禁用防火墙
systemctl disable firewalld

# 查看防火墙状态
systemctl status firewalld
```

### 禁用 selinux
```sh
vi /etc/selinux/config

# 禁用
SELINUX=disabled
```

### ip 配置
```sh
vi /etc/sysconfig/network-scripts/ifcfg-eth1

# HOSTNAME=
vi /etc/sysconfig/network
```

### 用户加入到 sudoers
```
vi /etc/sudoers

vagrant ALL=(ALL) NOPASSWD: ALL
```

### 配置 hosts 文件
```sh
vi /etc/hosts

192.168.10.10 node01
192.168.10.20 node02
192.168.10.30 node03
```

### 软件安装
```sh
cd /opt
mkdir app package
chown vagrant:vagrant app
chown vagrant:vagrant package
```

### 免密钥登录
```sh
# 生成密钥对
ssh-keygen -t rsa

# 发送公钥到本机
ssh-copy-id node01
cat id_rsa.pub >> authorized_keys
```
```sh
# PasswordAuthentication yes
sudo vi /etc/ssh/sshd_config
sudo systemctl restart sshd
```

实现 node01 到 node01, node02, node03 的免密钥登录
```sh
# 拷贝公钥文件
ssh-copy-id -i node01
ssh-copy-id -i node02
ssh-copy-id -i node03
```

通过脚本分发配置
```sh
./sync .ssh
```
```sh
#!/bin/bash
pcount=$#
if ((pcount==0)); then
    echo no args;
    exit;
fi

p1=$1
filename=`basename $p1`
pdir=`cd -P $(dirname $p1); pwd`
user=`whoami`

for((number=1; number<=3; number++)); do
    rsync -av $pdir/$fname $user@node$number:$pdir
done
```
```sh
#!/bin/bash
for i in node01 node02 node03
do
    ssh $i "source /etc/profile && jps | grep -v Jps"
done
```

### 时间同步
```sh
rpm -qa | grep ntp
yum install -y ntpdate
```
/etc/ntp.conf
```
restrict 192.168.1.0 mask 255.255.255.0 nomodify notrap

# server 0.centos.pool.ntp.org iburst
# server 1.centos.pool.ntp.org iburst
# server 2.centos.pool.ntp.org iburst
# server 3.centos.pool.ntp.org iburst

server 127.127.1.0
fudge 127.127.1.0 stratum 10
```
/etc/sysconfig/ntpd
```
SYNC_HWCLOCK=yes
```
```sh
service ntpd status
service ntpd start
chkconfig ntpd on
# enable
```
```sh
crontab -e

# 每分钟同步一次时间
*/1 * * * * /usr/sbin/ntpdate node01
```
```
date -s "2017-9-11 11:11:11"
```

### 安装 jdk 与 hadoop
```sh
# 检查是否已经安装 java
rpm -qa | grep java

# 卸载
sudo rpm -e <package>
```

将配置远程拷贝到 node01
```sh
# scp -r
scp jdk-8u201-linux-x64.tar_64.gz vagrant@node01:/opt/package
scp hadoop-3.1.2.tar.gz vagrant@node01:/opt/package
```

解压 jdk 与 hadoop 到 /opt/app 目录下

同步安装目录到其它机器
```sh
./sync.sh /opt/app
```

设置环境变量
```sh
vi /etc/profile

export JAVA_HOME=
export HADOOP_HOME=
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

source /etc/profile
```
```sh
./sync.sh /etc/profile
```

## 集群搭建
### hadoop 相关配置
hadoop-env.sh
```sh
export JAVA_HOME=
```
yarn-env.sh
```sh
export JAVA_HOME=
```
mapred-env.sh
```sh
export JAVA_HOME=
```

core-site.xml
```xml
<configuration>
    <!-- 指定 namenode 的地址 -->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://node01:9000</value>
    </property>
    <!-- 集群临时数据存储目录 -->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/app/hadoop-3.1.2/data</value>
    </property>

    <!-- io操作流的配置 -->
    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>
</configuration>
```

hdfs-site.xml
```xml
<configuration>
    <!--block的副本数-->
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <!-- 辅助 namenode 地址 -->
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>node03:50090</value>
    </property>

    <!-- namenode 元数据存储目录-->
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/opt/hdfs/name/</value>
    </property>
    <!--block 块大小-->
    <property>
        <name>dfs.blocksize</name>
        <value>268435456</value>
    </property>
    <property>
        <name>dfs.namenode.handler.count</name>
        <value>100</value>
    </property>
    <!--工作节点的数据块存储目录 -->
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/opt/hdfs/data/</value>
    </property>
</configuration>
```

mapred-site.xml
```xml
<!--指定运行mapreduce的环境是yarn -->
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <!-- 启动历史服务器：mr-jobhistory-daemon.sh start historyserver -->
    <!-- 历史服务器 -->
    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>node03:10020</value>
    </property>
    <!-- 历史服务器 web 地址 -->
    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>node03:19888</value>
    </property>

    mapreduce.jobhistory.done-dir
    mapreduce.jobhistory.intermediate-done-dir

    <property>
        <name>yarn.app.mapreduce.am.env</name>
        <value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
    </property>
    <property>
        <name>mapreduce.map.env</name>
        <value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
    </property>
    <property>
        <name>mapreduce.reduce.env</name>
        <value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*,$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
    </property>
</configuration>
```

yarn-site.xml
```xml
<configuration>
    <!-- 指定 resourcemanager 的位置 -->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>node02</value>
    </property>
    <!-- reducer 获取数据的方式 -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <!-- 日志聚集功能 -->
    <property>
        <name>yarn.log-aggregation-enable</name>
        <value>true</value>
    </property>
    <!-- 日志保留时间，7 天 -->
    <property>
        <name>yarn.log-aggregation.retain-seconds</name>
        <value>604800</value>
    </property>

    <property>
        <name>yarn.resourcemanager.address</name>
        <value>hadoop1:18040</value>
    </property>
    <property>
        <name>yarn.resourcemanager.scheduler.address</name>
        <value>hadoop1:18030</value>
    </property>
    <property>
        <name>yarn.resourcemanager.resource-tracker.address</name>
        <value>hadoop1:18025</value>
    </property>
    <property>
        <name>yarn.resourcemanager.admin.address</name>
        <value>hadoop1:18141</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>hadoop1:18088</value>
    </property>
</configuration>
```

workers
```sh
node01
node02
node03
```

分发配置文件
```sh
./sync.sh /opt/app/hadoop-3.1.2/etc
```

### 启动集群
node01: datanode, nodemanager, namenode
node02: datanode, nodemanager, resourcemanager
node03: datanode, nodemanager, secondaryNameNode

```sh
# node01 节点格式化，会产生新的集群 id
# 当再次格式化时，需要删除 data 数据和 log 日志
hdfs namenode -format

# node1 节点启动 hdfs
start-dfs.sh

# 启动 namenode
# hadoop-daemon.sh start namenode
# 启动 datanode
# hadoop-daemon.sh start datanode
# 启动 resource manager
# yarn-daemon.sh start resourcemanager
# yarn-daemon.sh start nodemanager
# yarn-daemon.sh start resourcemanager
# mr-jobhistory-daemon.sh start historyserver

# node02 节点启动 yarn resource manager
start-yarn.sh
```

hdfs web 端查看
```
node01:50070
```
yarn web 端查看
```
node02:8088
```
查看历史运行
```
node03:19888
```
snn web 端查看
```
node03:50090
```

测试 word 计数
```sh
hdfs fs -put words /
hdfs jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.2.jar wordcount /words /wordcount
```

### 停止集群
```sh
stop-dfs.sh
stop-yarn.sh

# yarn-daemon.sh stop resourcemanager
# yarn-daemon.sh stop nodemanager
# mr-jobhistory-daemon.sh stop historyserver
```
