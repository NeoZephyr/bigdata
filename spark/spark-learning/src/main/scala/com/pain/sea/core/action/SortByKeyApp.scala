package com.pain.sea.core.action

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object SortByKeyApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("sortByKey app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val lines = Array("spark streaming", "spark hive", "hadoop hive", "hive hbase", "hive impala", "spark kafka")
        val rdd: RDD[(String, Int)] = sparkContext.parallelize(lines).flatMap(line => line.split(" ")).map((_, 1)).reduceByKey(_ + _)
        rdd.map(x => (x._2, x._1)).sortByKey(false).map(x => (x._2, x._1)).collect().foreach(println)
    }
}
