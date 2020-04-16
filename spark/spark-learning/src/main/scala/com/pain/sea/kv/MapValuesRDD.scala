package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object MapValuesRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("map values").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[(Int, String)] = sparkContext.makeRDD(List((1, "a"), (1, "d"), (2, "b"), (3, "c")))
        val mapValuesRdd: RDD[(Int, String)] = rdd.mapValues(_.toUpperCase)
        mapValuesRdd.collect().foreach(println)
    }
}