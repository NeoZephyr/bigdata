package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GroupByKeyApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("groupByKey app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val lines = Array("spark streaming", "spark hive", "hadoop hive", "hive hbase", "hive impala", "spark kafka")
        val rdd: RDD[String] = sparkContext.parallelize(lines)
        val wordRdd: RDD[String] = rdd.flatMap(item => {
            item.split(" ")
        })
        val wordToOneRdd: RDD[(String, Int)] = wordRdd.map((_, 1))
        val groupByKeyRdd: RDD[(String, Iterable[Int])] = wordToOneRdd.groupByKey()

        groupByKeyRdd.collect().foreach(item => {
            val value = item._2.mkString(", ")
            println(s"(${item._1}, (${value}))")
        })

        val sumRdd: RDD[(String, Int)] = groupByKeyRdd.map(x => (x._1, x._2.sum))
        sumRdd.collect().foreach(println)
    }
}
