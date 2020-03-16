```sh
#!/bin/bash
if (($#==0))
then
    exit 1;
fi
for i in node01 node02 node03
do
    echo Starting zk in $i
    ssh $i "source /etc/profile && /app/zookeeper-3.4.10/bin/zkServer.sh $1" > /dev/null
done
```

zoo.cfg
```sh
# 存放日志文件、快照文件
dataDir=/data/zookeeper
clientPort=2181
```
```sh
zkServer.sh start
```
