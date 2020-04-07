package com.pain.sea

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object FilterRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("filter rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val filterRdd: RDD[Int] = rdd.filter(_ % 3 == 0)

        filterRdd.collect().foreach(println)
    }
}
