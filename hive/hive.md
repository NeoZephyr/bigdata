## hive 架构
### 用户接口
command line
jdbc
webui

### 元数据
元数据包括：表名、表所属的数据库、表的拥有者、列/分区字段表的类型、表的数据所在目录等

### hadoop
使用 HDFS 进行存储，使用 MapReduce 进行计算

### 驱动器
1. 解析器：将 SQL 字符串转换成抽象语法树 AST
2. 编译器：将 AST 编译生成逻辑执行计划
3. 优化器：对逻辑执行计划进行优化
4. 执行器：把逻辑执行计划转换成可以运行的物理计划

Hive 通过给用户提供的一系列交互接口，接收到用户的指令，使用自己的驱动器，结合元数据，将这些指令翻译成 MapReduce，提交到 Hadoop 中执行，最后，将执行返回的结果输出到用户交互接口


## DDL
```sql
show databases like 'test*';
```
```sql
# 指定数据库在 HDFS 上存放的位置
create database if not exists test LOCATION '/user/hive/test'
with DBPROPERTIES('creator'='pain');

desc database test;
desc database extended test;
```
```sql
ALTER DATABASE test set DBPROPERTIES('createtime'='20200321')
```
```sql
# 删除空数据库
drop database test;
```

```sql
create table user(
id int,
name string,
create_date string
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'

desc user;
desc extended user;
desc formatted user;
```
```sql
LOAD DATA LOCAL INPATH '/home/vagrant/user.txt' OVERWRITE INTO TABLE user;
```

```sql
ALTER TABLE user rename to account;
```

```sql
alter table customer add columns(desc, string);
alter table customer change column desc count int;
alter table customer replace columns(name string, mobile string);
```

```sql
drop table customer;
```


## DML
```sql
LOAD DATA LOCAL INPATH '/home/vagrant/user.txt' OVERWRITE INTO TABLE user;
```

LOCAL: 表示本地文件系统，如果没有则指的是 HDFS 的路径
OVERWRITE: 是否数据覆盖，如果没有则表示数据追加

根据查询结果创建表，查询的结果会添加到新创建的表中
```sql
create table if not exists user_bak as select * from user;
```
根据已经存在的表结构创建表
```sql
create table if not exists user_bak like user;
```

```sql
insert overwrite local directory '/tmp/'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
select id, name, create_date from user;
```


## QL
```sql
select max(id), min(id), avg(id), sum(id) from user;
```
```sql
select dept_code, avg(salary) from dept group by dept_code;
select dept_code, job, avg(salary) from dept group by dept_code, job;

select dept_code, avg(salary) avg_salary from dept group by dept_code having avg_salary > 2000;
```

```sql
select u.name, d.name
from user u join dept d
on u.dept_code = d.code;
```

```sql
explain
select u.name, d.name
from user u join dept d
on u.dept_code = d.code;
```


## 内部表 vs 外部表
默认创建的表都是管理表，也就是内部表。当删除一个内部表时，Hive 也会删除这个表中数据。因此，内部表不适合和其他工具共享数据

```sql
create table if not exists student(
id int, name string
)
row format delimited fields terminated by '\t'
stored as textfile
location '/user/hive/student';
```

删除外部表并不会删除掉对应的数据，但是描述表的元数据信息会被删除掉
```sql
create external table if not exists student(
id int, name string
)
row format delimited fields terminated by '\t'
stored as textfile
location '/user/hive/student';
```

内部表与外部表的相互转换
```sql
alter table student set tblproperties('EXTERNAL'='TRUE');
alter table student set tblproperties('EXTERNAL'='FALSE');
```


## 分区表
```sql
create table customer(
id int,
name string,
mobile string
)
partitioned by (month string)
row format delimited fields terminated by '\t';
```

分区表加载数据时，必须指定分区
```
load data local inpath '/home/vagrant/customer.txt' into table customer partition(month='202001');
load data local inpath '/home/vagrant/customer.txt' into table customer partition(month='202002');
```

单分区查询
```sql
select * from customer where month='202001';
```
多分区联合查询
```sql
select * from customer where month='202001'
union
select * from customer where month='202002'
```

创建单个分区
```sql
alter table customer add partition(month='202003');
```
同时创建多个分区
```sql
alter table customer add partition(month='202004') partition(month='202005');
```
删除单个分区
```sql
alter table customer drop partition(month='202005');
```
删除多个分区
```sql
alter table customer drop partition(month='202004'), partition(month='202003');
```
查看分区表的分区
```sql
show partitions customer;
```
查看分区表结构
```sql
desc formatted customer;
```

创建二级分区表
```sql
create table customer(
id int,
name string,
mobile string
)
partitioned by (month string, day string)
row format delimited fields terminated by '\t';
```
加载数据
```
load data local inpath '/home/vagrant/customer.txt' into table customer partition(month='202001', day='13');
```
查询分区数据
```
select * from customer where month='202001' and day='13';
```

把数据直接上传到分区目录上，让分区表和数据产生关联的三种方式
上传数据后修复
```
dfs -mkdir -p /user/hive/warehouse/month=202001/day=13;
dfs -put /user/vagrant/customer.txt /user/hive/warehouse/customer/month=202001/day=13;
```
```sql
select * from customer where month='202001' and day='13';

# 执行修复命令
msck repair table customer;
```

上传数据后添加分区
```
dfs -mkdir -p /user/hive/warehouse/month=202001/day=13;
dfs -put /user/vagrant/customer.txt /user/hive/warehouse/customer/month=202001/day=13;
```
```sql
alter table customer add partition(month='202001', day='13');

select * from customer where month='202001' and day='13';
```

创建文件夹后加载数据到分区
```
dfs -mkdir -p /user/hive/warehouse/month=202001/day=13;
```
```
load data local inpath '/home/vagrant/customer.txt' into table customer partition(month='202001', day='13');
```
查询数据
```sql
select * from customer where month='202001' and day='13';
```



## 数据类型
基本数据类型
TINYINT: byte
SMALINT: short
INT: int
BIGINT: long
BOOLEAN: boolean
FLOAT: float
DOUBLE: double
STRING: string
TIMESTAMP
BINARY

集合数据类型
STRUCT
MAP
ARRAY


songsong,bingbing_lili,xiao song:18_xiaoxiao song:19,hui long guan_beijing
yangyang,caicai_susu,xiao yang:18_xiaoxiao yang:19,chao yang_beijing

create table test(
name string,
friends array<string>,
children map<string, int>,
address struct<street:string, city:string>
)
row format delimited fields terminated by ','
collection items terminated by '_'
map keys terminated by ':'
lines terminated by '\n';

load data local inpath ‘/opt/module/datas/test.txt’into table test

select friends[1],children['xiao song'],address.city from test
where name="songsong";



