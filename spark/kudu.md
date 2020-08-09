## 安装包
https://archive.cloudera.com/cdh5/redhat/7/x86_64/cdh/5.15.1/RPMS/x86_64/
```
kudu-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
kudu-client-devel-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
kudu-client0-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
kudu-debuginfo-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
kudu-master-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
kudu-tserver-1.7.0+cdh5.15.1+0-1.cdh5.15.1.p0.4.el7.x86_64.rpm
```

```sh
sudo rpm -ivh --nodeps *
```


## 数据目录
master 元数据目录
```sh
sudo mkdir -p /data/kudu/kudu_master_data
```

table 数据目录
```sh
sudo mkdir -p /data/kudu/kudu_tserver_data
```

log 目录
```sh
sudo mkdir -p /data/kudu/log
```

```sh
sudo chown -R kudu:kudu *
```


## 配置文件
/etc/kudu/conf/master.gflagfile
```
--fs_wal_dir=/data/kudu/kudu_master_data
--fs_data_dirs=/data/kudu/kudu_master_data
--log_dir=/data/kudu/log
```

/etc/kudu/conf/tserver.gflagfile
```
--fs_wal_dir=/data/kudu/kudu_tserver_data
--fs_data_dirs=/data/kudu/kudu_tserver_data
--tserver_master_addrs=cdh:7051
--log_dir=/data/kudu/log
```


## 启动
cd /etc/init.d

sudo ./kudu-master start
sudo ./kudu-tserver start


启动报错解决
```sh
yum install redhat-lsb -y
```

```sh
sudo yum install ntp -y
service ntpd start
systemctl enable ntpd
```

```sh
ntpq -p
```

/etc/ntp.conf
```
server cdh perfer
```

创建表错误解决
```
yum install gcc python-devel
yum install cyrus-sasl*
```

权限问题解决
```
--rpc_authentication=disabled
--rpc_encryption=disabled
--trusted_subnets=0.0.0.0/0
```

webUI 8050