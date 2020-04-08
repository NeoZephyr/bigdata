package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SortByRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("sortBy rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10)
        val sortByRdd: RDD[Int] = rdd.sortBy(x => x, false)

        sortByRdd.collect().foreach(println)
    }
}
