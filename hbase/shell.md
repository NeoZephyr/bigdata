```sh
hbase shell
```

查看当前数据库中的表
```sh
list
```

创建表
```sh
create 'student', 'info'
```
查看表结构
```sh
describe 'student'
```
变更表信息
```sh
alter 'student', {NAME=>'info',VERSIONS=>3}
```

插入数据
```sh
put 'student', '100001', 'info:gender', 'male'
put 'student', '100001', 'info:age', '20'
```
更新数据
```sh
put 'student', '100001', 'info:gender', 'female'
```

扫描表
```sh
scan 'student'
scan 'student', {STARTROW => '100001', STOPROW => '100001'}
```
查看数据
```sh
get 'student', '100001'
get 'student', '100001', 'info:gender'
```

统计表行数
```sh
count 'student'
```

删除数据
```sh
delete 'student', '100001', 'info:age'
deleteall 'student', '100001'
```

清空表数据
```sh
truncate 'student'
```

删除表
```sh
disable 'student'
drop 'student'
```

