package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object DistinctRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("distinct rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(List(90, 20, 50, 20, 30, 90, 10))
        val distinctRdd: RDD[Int] = rdd.distinct(2)

        println(distinctRdd.collect().mkString(","))
    }

}
