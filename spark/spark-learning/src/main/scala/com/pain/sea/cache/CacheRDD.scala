package com.pain.sea.cache

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CacheRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("cache").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[String] = sparkContext.makeRDD(List("spark", "hbase"))
        val cacheRdd: RDD[String] = rdd.map(_ + System.currentTimeMillis())

        cacheRdd.cache()
        cacheRdd.collect().foreach(println)
        cacheRdd.collect().foreach(println)
    }
}
