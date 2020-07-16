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

```sql
insert overwrite table user partition(month='202002')
select id, name, create_date from user where month = '202001';
```

```sql
truncate table user;
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

以 Pa 开头
```sql
select * from user where name like 'Pa%';
```
第 2、3 个字符为 Pa
```sql
select * from user where name like "_Pa%";
```
包含 Pa
```sql
select * from user where name rlike '[Pa]';
```

全局排序，只有一个 Reducer。如果数据量比较大的时候，效率会非常低
```sql
select * from user order by salary desc;
select * from user order by name, salary;
```

sort by 为每个 reducer 产生一个排序文件。每个 reducer 内部进行排序，对全局结果集来说不是排序
```
set mapreduce.job.reduces=3;
set mapreduce.job.reduces;
```
```sql
select * from user sort by salary desc;
```

将查询结果导入到文件中
```sql
# 按照部门编号降序排序
insert overwrite local directory '/home/vagrant/user'
select * from user sort by salary desc;
```

分区排序
```sh
set mapreduce.job.reduces=3;
```
```sql
# 先按照用户 id 分区，再按照 salary 降序排序
insert overwrite local directory '/home/vagrant/user'
select * from user distribute by id sort by salary desc;
```

当 distribute by 和 sort by 字段相同时，可以使用 cluster by 方式
```sql
select * from user cluster by id;
```

分区针对的是数据的存储路径；分桶针对的是数据文件
创建分桶表
```sql
create table user_buck(id int, name string)
clustered by(id) 
into 4 buckets
row format delimited fields terminated by '\t';
```
```sql
desc formatted user_buck;
```
```sql
load data local inpath '/home/vagrant/user.txt' into table user_buck;
```

发现没有分桶

```sql
create table user(id int, name string)
row format delimited fields terminated by '\t';
```
```sql
load data local inpath '/home/vagrant/user.txt' into table user;
```
```sql
truncate table user_buck;
```
```sh
set hive.enforce.bucketing=true;
set mapreduce.job.reduces=-1;
```
```sql
insert into table user_buck
select id, name from user;
```

分桶取样
```sql
select * from user_buck tablesample(bucket 1 out of 4 on id);
```
TABLESAMPLE(BUCKET x OUT OF y)
y 必须是 table 总 bucket 数的倍数或者因子。table 总共分了 4 份，当 y = 2 时，抽取 4 / 2 个 bucket 的数据，当 y = 8 时，抽取 4 / 8 个 bucket 的数据

x 表示从哪个 bucket 开始抽取，如果需要取多个分区，以后的分区号为当前分区号加上 y

tablesample(bucket 1 out of 2)，表示总共抽 4 / 2 个 bucket 的数据，抽取第 1 个和第 1 + 2 个 bucket 的数据

```sql
select u.name, d.name
from user u join dept d
on u.dept_code = d.code;
```

```sql
explain extended
select u.name, d.name
from user u join dept d
on u.dept_code = d.code;
```

窗口函数
```sql
# 4 月份的顾客及总人数
select name, count(*) over ()
from order 
where substring(create_date, 1, 7) = '2017-04' 
group by name;
```
```sql
# 购买明细及月购买总额
select name, create_date, cost, sum(cost)
over(partition by month(create_date))
from order;
```

```sql
# 购买明细及月购买总额, 将每个顾客的 cost 按照日期进行累加
select name, create_date, cost, 
sum(cost) over() as total1, -- 所有行相加 
sum(cost) over(partition by name) as total2, --按 name 分组，组内数据相加 
sum(cost) over(partition by name order by create_date) as total3,
sum(cost) over(partition by name order by create_date rows between UNBOUNDED PRECEDING and current row ) as total4, -- 由起点到当前行的聚合
sum(cost) over(partition by name order by create_date rows between 1 PRECEDING and current row) as total5, --当前行和前面一行做聚合
sum(cost) over(partition by name order by create_date rows between 1 PRECEDING AND 1 FOLLOWING) as total6, -- 当前行和前边一行及后面一行
sum(cost) over(partition by name order by create_date rows between current row and UNBOUNDED FOLLOWING) as total7 --当前行及后面所有行
from order;
```

```sql
# 查看顾客上次的购买时间
select name, create_date, cost,
lag(create_date, 1, '1900-01-01') over(partition by name order by create_date ) as time1,
lag(create_date, 2) over (partition by name order by create_date) as time2 
from order;
```

```sql
# 查询前 20% 时间的订单信息
select * from (
    select name, create_date, cost, ntile(5)
    over(order by create_date) sorted
    from order
) t
where sorted = 1;
```

rank
rank() 排序相同时会重复，总数不会变
dense_rank() 排序相同时会重复，总数会减少
row_number() 会根据顺序计算
```sql
select name, subject, score,
rank() over(partition by subject order by score desc) rp,
dense_rank() over(partition by subject order by score desc) drp,
row_number() over(partition by subject order by score desc) rmp
from score;
```


## 函数
查看系统自带的函数
```sql
show functions;
```
查看自带的函数的用法
```sql
desc function upper;
desc function extended upper;
```

```xml
<dependency>
    <groupId>org.apache.hive</groupId>
    <artifactId>hive-exec</artifactId>
</dependency>
```
```java
public class Lower extends UDF {
    public String evaluate (final String text) {
        if (text == null) {
            return null;
        }

        return text.toLowerCase();
    }
}
```
创建函数
```
add jar /app/jars/udf.jar;
```
```
create temporary function mylower as "com.pain.hive.Lower";
```
```sql
select name, mylower(name) lowername from stu;
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

```sql
select name, nvl(email, "a@qq.com") from user;
select name, nvl(email, mobile) from user;
```

```sql
select
    dept_code,
    sum(case gender when '男' then 1 else 0 end) male,
    sum(case gender when '女' then 1 else 0 end) famale
from user
group by dept_code;
```

行转列
```sql
select
    t.customer_id,
    concat_ws('|', collect_set(t.value)) identity
from
    (
        select
            value,
            concat(customer_id, ",", type) customer_id
        from customer
    ) t
group by customer_id;
```

列转行
```sql
select
    product_name,
    tag
from product lateral view explode(tags) t as tag;
```


## 数据导出
导出到本地
```sql
insert overwrite local directory '/home/vagrant/user'
select * from user;
```
格式化导出到本地
```sql
insert overwrite local directory '/home/vagrant/user'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
select * from user;
```
导出到 HDFS 上
```sql
insert overwrite directory '/home/vagrant/user'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' 
select * from user;
```
Hadoop 命令导出到本地
```sh
dfs -get /user/hive/warehouse/user/month=202001/000000_0 /home/vagrant/user
```
Hive Shell 命令导出
```sh
hive -e 'select * from test.user;' > /home/vagrant/user/user.txt;
```
Export 导出到 HDFS 上
```sh
export table test.user to '/user/hive/warehouse/export/user';
```
```sh
import table user partition(month='202001') from '/user/hive/warehouse/export/user';
```


## 压缩
开启 Map 输出阶段压缩
```sql
-- 传输数据压缩功能
set hive.exec.compress.intermediate=true;

-- map 输出压缩功能
set mapreduce.map.output.compress=true;

-- map 输出数据的压缩方式
set mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec;
```
```sql
select count(name) name from stu;
```

开启 Reduce 输出阶段压缩
```sql
-- hive 最终输出数据压缩功能
set hive.exec.compress.output=true;

-- mapreduce 最终输出数据压缩
set mapreduce.output.fileoutputformat.compress=true;

-- mapreduce 最终数据输出压缩方式
set mapreduce.output.fileoutputformat.compress.codec=org.apache.hadoop.io.compress.SnappyCodec;

-- mapreduce 最终数据输出压缩为块压缩
set mapreduce.output.fileoutputformat.compress.type=BLOCK;
```
```sql
insert overwrite local directory '/app/trash/result' select * from customer distribute by dept_code sort by id desc;
```

查看 hadoop 支持的压缩方式
```sh
hadoop checknative
```


## 文件存储格式
TEXTFILE 和 SEQUENCEFILE 的存储格式都是基于行存储的
ORC 和 PARQUET 是基于列式存储的

```sql
create table log_text (
)
row format delimited fields terminated by '\t'
stored as textfile ;
```
```sql
load data local inpath '/app/log.data' into table log_text;
```
```
dfs -du -h /user/hive/warehouse/log_text;
```

```sql
create table log_orc(
)
row format delimited fields terminated by '\t'
stored as orc ;
```
```sql
insert into table log_orc select * from log_text;
```
```
dfs -du -h /user/hive/warehouse/log_orc;
```

```sql
create table log_parquet(
)
row format delimited fields terminated by '\t'
stored as parquet ; 
```
```sql
insert into table log_parquet select * from log_text;
```
```
dfs -du -h /user/hive/warehouse/log_parquet;
```


## 优化
执行查询语句，都会执行 mapreduce 程序
```
set hive.fetch.task.conversion=none;
```

针对某些情况的查询可以不使用 mapreduce 计算
```
set hive.fetch.task.conversion=more;
```

开启本地模式
```
set hive.exec.mode.local.auto=true;
```
设置本地模式的最大输入数据量
```
set hive.exec.mode.local.auto.inputbytes.max=50000000;
```
设置本地模式的最大输入文件个数
```
set hive.exec.mode.local.auto.input.files.max=10;
```

设置自动选择 Mapjoin，将小表加载到内存，以便在 map 阶段进行 join，避免 reducer 处理
大表小表的阈值设置，默认 25M 以下是小表
```
set hive.auto.convert.join = true;
set hive.mapjoin.smalltable.filesize=25000000;
```


空 key 过滤
将空 key 赋值为一个随机的值，使得数据随机均匀地分不到不同的 reducer 上

