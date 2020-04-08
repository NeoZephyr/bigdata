package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object ReduceByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("reduce by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[String] = sparkContext.makeRDD(Array("spark", "hbase", "spark", "hive", "spark", "hive"))
        val wordToOneRdd: RDD[(String, Int)] = rdd.map((_, 1))

        val reduceByKeyRdd: RDD[(String, Int)] = wordToOneRdd.reduceByKey(_ + _)
        reduceByKeyRdd.collect().foreach(println)
    }

}
