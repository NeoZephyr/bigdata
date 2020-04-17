package com.pain.sea.partition

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}

object PartitionRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("partition").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[(String, String)] = sparkContext.makeRDD(List(("spark", "engine"), ("hbase", "database"), ("hadoop", "file"), ("mapreduce", "engine"), ("hive", "tools")))
        val partitionRdd: RDD[(String, String)] = rdd.partitionBy(new HashPartitioner(2))
        println(rdd.partitioner)
        println(partitionRdd.partitioner)

        partitionRdd.mapPartitions(iter => Iterator(iter.length)).collect().foreach(println)

        val myPartitionRdd: RDD[(String, String)] = rdd.partitionBy(new MyPartitioner(2))
        val myPartitionIndexRdd: RDD[(Int, (String, String))] = myPartitionRdd.mapPartitionsWithIndex((idx, items) => items.map((idx, _)))
        myPartitionIndexRdd.collect().foreach(println)
    }
}

class MyPartitioner(partitionNum: Int) extends Partitioner {
    override def numPartitions: Int = partitionNum

    override def getPartition(key: Any): Int = {
        val keyString: String = key.toString
        keyString.length % numPartitions
    }
}