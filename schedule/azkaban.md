## 架构
Relational Database (MySQL)
AzkabanWebServer
AzkabanExecutorServer


## 运行模式
solo-server
the heavier weight two server mode
distributed multiple-executor mode


## 编译
1. 下载源码包
2. ./gradlew build installDist -x test
3. 手动或自动下载 gradle-4.1-all.zip，手动下载时存放到 gradle/wrapper 目录下，并修改 properties 文件：distributionUrl=gradle-4.1-all.zip
4. 编译成功之后，去对应的目录下找到对应模式的安装包即可

因为网络原因，jar 包下载失败。使用阿里云镜像
```
buildscript {
    repositories {
        maven {
            url 'http://maven.aliyun.com/nexus/content/groups/public/'
        }
    }
    dependencies {
        classpath 'com.cinnober.gradle:semver-git:2.2.3'
        classpath 'net.ltgt.gradle:gradle-errorprone-plugin:0.0.11'
    }
}

allprojects {
    repositories {
        maven {
            url 'http://maven.aliyun.com/nexus/content/groups/public/'
        }
        mavenCentral()
        mavenLocal()
    }
}


```


## solo 环境搭建
1. 解压编译后的安装包
2. 启动 azkaban

```
./bin/azkaban-solo-start.sh
```

```
vi azkaban.properties
```
```
vi azkaban-users.xml
```


## two server 模式
```sql
CREATE DATABASE azkaban;
CREATE USER 'azkaban'@'%' IDENTIFIED BY 'azkaban';
GRANT SELECT,INSERT,UPDATE,DELETE ON azkaban.* to 'azkaban'@'%' WITH GRANT OPTION;
```
```sql
flush privileges;
```
```sql
source create-all-sql-0.1.0-SNAPSHOT.sql;
```

```sh
tar -xzvf azkaban-web-server-0.1.0-SNAPSHOT.tar.gz -C ~/app/
```
```
keytool -keystore keystore -alias jetty -genkey -keyalg RSA
```


## flow
准备 job
```
zip -r foo.zip *
```
依赖 job
```
zip -r dependency.zip foo.job bar.job
```

hdfs job
```
type=command
command=/home/vagrant/app/hadoop-2.6.0-cdh5.15.1/bin/hdfs dfs -ls /
```

mapreduce job
```
type=command
command=/home/vagrant/app/hadoop-2.6.0-cdh5.15.1/bin/hadoop jar /home/vagrant/app/hadoop-2.6.0-cdh5.15.1/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0-cdh5.15.1.jar wordcount  /hdfs/words.txt /hdfs/az
```

hive job
```
type=command
command=/home/vagrant/app/hive-1.1.0-cdh5.15.1/bin/hive -f 'test.sql'
```


## ajax api


## plugin


## 短信告警改造


内存不足问题
plugins/jobtypes/commonprivate.properties
```
memCheck.enabled=false
```