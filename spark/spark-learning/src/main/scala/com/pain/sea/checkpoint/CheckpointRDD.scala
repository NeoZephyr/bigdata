package com.pain.sea.checkpoint

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CheckpointRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("check point").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        sparkContext.setCheckpointDir("checkpoint")

        val rdd: RDD[String] = sparkContext.makeRDD(List("spark", "hbase"))
        val timeRdd: RDD[String] = rdd.map(_ + System.currentTimeMillis())
        timeRdd.collect().foreach(println)
        timeRdd.collect().foreach(println)
    }
}
