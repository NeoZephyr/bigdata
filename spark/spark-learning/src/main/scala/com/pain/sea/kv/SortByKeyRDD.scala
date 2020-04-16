package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SortByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("sort by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[(Int, String)] = sparkContext.makeRDD(List((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")))

        // key 必须实现 Ordered 接口
        val ascRdd: RDD[(Int, String)] = rdd.sortByKey(true)
        val descRdd: RDD[(Int, String)] = rdd.sortByKey(false)

        ascRdd.collect().foreach(println)
        descRdd.collect().foreach(println)
    }
}
