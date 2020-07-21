```sql
create external table track_info(
ip string,
country string,
province string,
city string,
url string,
time string,
page string
) partitioned by (day string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
```

```hiveql
LOAD DATA INPATH 'hdfs://cdh:8020/test/stat/etl/output' OVERWRITE INTO TABLE track_info partition(day='2013-07-21');
```

```sql
select count(*) from track_info where day='2013-07-21';
select province,count(*) as cnt from track_info where day='2013-07-21' group by province;
```

```sql
create table track_info_province_stat(
province string,
cnt bigint
) partitioned by (day string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t';
```

```sql
insert overwrite table track_info_province_stat partition(day='2013-07-21') 
select province,count(*) as cnt from track_info where day='2013-07-21' group by province;
```