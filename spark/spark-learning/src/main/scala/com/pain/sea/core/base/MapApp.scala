package com.pain.sea.core.base

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object MapApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("map app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 4)

        // 每次处理一条数据
        val mapRdd: RDD[Int] = rdd.map(_ * 10)
        println(mapRdd.collect().mkString(", "))

        // 每次处理一个分区的数据
        // 这个分区的数据处理完后，原 RDD 中分区的数据才能释放，可能导致 OOM
        val mapPartitionRdd: RDD[Int] = rdd.mapPartitions(it => {
            it.map(_ * 100)
        })
        println(mapPartitionRdd.collect().mkString(", "))

        val mapPartitionWithIndexRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((idx, it) => {
            it.map((idx, _))
        })
        println(mapPartitionWithIndexRdd.collect().mkString(", "))
    }
}
