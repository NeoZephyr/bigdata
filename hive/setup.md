## hive 配置
/etc/profile
```
export HIVE_HOME=
export PATH=$HIVE_HOME/bin:$PATH
```

hive-env.sh
```
export HADOOP_HOME=/app/hadoop-3.1.2
export HIVE_CONF_DIR=/app/apache-hive-3.1.2/conf
```

hive-site.xml
```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>

    <!-- &useUnicode=true&characterEncoding=UTF-8 -->
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>
            jdbc:mysql://lab:3306/metastore?createDatabaseIfNotExist=true
        </value>
        <description>JDBC connect string for a JDBC metastore</description>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
        <description>Driver class name for a JDBC metastore</description>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
        <description>username to use against metastore database</description>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>123456</value>
        <description>password to use against metastore database</description>
    </property>

    <!-- 显示表的头信息 -->
    <property>
        <name>hive.cli.print.header</name>
        <value>true</value>
    </property>

    <!-- 显示当前数据库 -->
    <property>
        <name>hive.cli.print.current.db</name>
        <value>true</value>
    </property>

    <property>
        <name>hive.metastore.warehouse.dir</name>
        <value>/user/hive/warehouse</value>
        <description>location of default database for the warehouse</description>
    </property>
</configuration>
```

hive-log4j.properties
```
hive.log.dir=
```


## mysql
```
rpm -qa | grep -i mysql
rpm -e --nodeps <mysql>
```

```sh
wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm
sudo rpm -ivh mysql-community-release-el7-5.noarch.rpm
sudo yum install mysql-server

systemctl start mysqld.service
```

重置密码
```
mysql -u root
```

如果有权限错误
```sh
sudo chown -R vagrant:vagrant /var/lib/mysql

systemctl restart mysqld.service
```

```
use mysql;
update user set password=password('123456') where user='root';
exit;
```

```
mysql -h lab -uroot -p
use mysql;
select host, user, password from user;

grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
flush privileges;

select host, user, password from user;
```


## 启动 hive
拷贝 mysql 驱动 jar 包到 HIVE_HOME/lib 目录

```sh
schematool -dbType mysql -initSchema
```
```sh
hive
```

```sql
show databases;
create database if not exists test;

use test;
show tables;

create table player(id int, name string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';
show tables;
select * from player;

load data local inpath '/home/vagrant/player.txt' overwrite into table player;

select count(*) from player;
```

使用 hiveserver

```sh
nohup hiveserver2 &
```
```sh
beeline
!connect jdbc:hive2://lab:10000
```

```sh
!clear;
!ls /;

dfs ls /;
```