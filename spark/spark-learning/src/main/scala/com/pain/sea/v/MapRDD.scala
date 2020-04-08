package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MapRDD {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("map rdd").setMaster("local[*]")
    val sparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 4)

    // 每次处理一条数据
    val mapRdd: RDD[Int] = rdd.map(_ * 10)
    mapRdd.collect().foreach(println)

    // 每次处理一个分区的数据
    // 这个分区的数据处理完后，原 RDD 中分区的数据才能释放，可能导致 OOM
    val mapPartitionRdd: RDD[Int] = rdd.mapPartitions(it => {
      it.map(_ * 100)
    })
    mapPartitionRdd.collect().foreach(println)

    val mapPartitionWithIndexRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((idx, it) => {
      it.map((_, idx))
    })
    mapPartitionWithIndexRdd.collect().foreach(println)

    val linesRdd: RDD[String] = sparkContext.textFile("input")
    val wordsRdd: RDD[String] = linesRdd.flatMap(_.split(" "))
    // val flatMapRdd: RDD[Int] = rdd.flatMap(1 until _)
    wordsRdd.collect().foreach(println)
  }

}
