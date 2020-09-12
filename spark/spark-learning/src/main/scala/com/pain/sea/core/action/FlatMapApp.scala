package com.pain.sea.core.action

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object FlatMapApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("flatMap app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val lines = Array("spark streaming", "spark hive", "hadoop hive", "hive hbase", "hive impala", "spark kafka")
        val linesRdd: RDD[String] = sparkContext.parallelize(lines)
        val wordsRdd: RDD[String] = linesRdd.flatMap(_.split(" "))
        println(wordsRdd.collect().mkString(", "))
    }
}
