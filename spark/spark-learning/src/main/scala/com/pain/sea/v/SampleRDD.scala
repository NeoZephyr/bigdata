package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SampleRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("sample rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val sampleRdd1: RDD[Int] = rdd.sample(true, 0.4, System.currentTimeMillis())
        println(sampleRdd1.collect().mkString(","))

        val sampleRdd2: RDD[Int] = rdd.sample(false, 0.4, System.currentTimeMillis())
        println(sampleRdd2.collect().mkString(","))
    }

}
