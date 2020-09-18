package com.pain.sea.core.base.partition

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MapApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("map app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 4)

        // 每次处理一条数据
        val mapRdd: RDD[Int] = rdd.map(_ * 10)
        println(mapRdd.collect().mkString(", "))
    }
}
