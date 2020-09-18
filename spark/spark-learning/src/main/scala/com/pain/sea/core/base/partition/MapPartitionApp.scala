package com.pain.sea.core.base.partition

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object MapPartitionApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("mapPartition app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd1: RDD[(Int, String)] = sparkContext.makeRDD(Array((1, "jack"), (2, "ray"), (3, "geo"), (4, "paul"), (5, "slack")), 4)
        mapPartitionWithIndex(rdd1)

        val rdd2: RDD[(Int, String)] = rdd1.partitionBy(new HashPartitioner(2))
        mapPartitionWithIndex(rdd2)

        val rdd3: RDD[(Int, String)] = rdd1.repartition(2)
        mapPartitionWithIndex(rdd3)

        val rdd4: RDD[(Int, String)] = rdd1.coalesce(2)
        mapPartitionWithIndex(rdd4)

        mapPartition(rdd1)

        sparkContext.stop()
    }

    def mapPartitionWithIndex(rdd: RDD[(Int, String)]): Unit = {
        val rddWithPartition: RDD[(Int, (Int, String))] = rdd.mapPartitionsWithIndex((idx, item) => {
            item.map((idx, _))
        })
        rddWithPartition.collect().foreach(println)
        println(s"rdd partition size: ${rdd.partitions.length}")
    }

    def mapPartition(rdd: RDD[(Int, String)]): Unit = {
        // 每次处理一个分区的数据
        // 这个分区的数据处理完后，原 RDD 中分区的数据才能释放，可能导致 OOM
        val rddWithPartition: RDD[(Int, String)] = rdd.mapPartitions(item =>
            item.map(subItem =>
                (subItem._1 * 1000, subItem._2)
            )
        )
        rddWithPartition.collect().foreach(println)
    }
}
