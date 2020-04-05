create 'user', 'base_info', 'extra_info'

desc 'user'
scan 'hbase:meta'

put 'user', '00001', 'base_info:name', 'tony'
flush user

rowkey
热点问题
数据查询