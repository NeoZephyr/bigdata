package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object ReduceByKeyApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("reduceByKey app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val lines = Array("spark streaming", "spark hive", "hadoop hive", "hive hbase", "hive impala", "spark kafka")
        val wordRdd: RDD[(String, Int)] = sparkContext.parallelize(lines).flatMap(item => item.split(" ")).map((_, 1))
        val reduceByKeyRdd: RDD[(String, Int)] = wordRdd.reduceByKey(_ + _)
        reduceByKeyRdd.collect().foreach(println)
    }
}
