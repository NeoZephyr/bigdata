package com.pain.sea.core.base

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SortByApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("sortByApp").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[Int] = sparkContext.makeRDD(List(10, 20, 100, 200, 300, 400, 350, 250, 150, 50), 2)
        val sortByRdd: RDD[Int] = rdd.sortBy(x => x, false)

        println(sortByRdd.collect().mkString(", "))
    }
}
