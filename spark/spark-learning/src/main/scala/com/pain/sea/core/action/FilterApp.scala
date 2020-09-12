package com.pain.sea.core.action

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object FilterApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("filter app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val filterRdd: RDD[Int] = rdd.filter((value: Int) => {
            var flag = false
            if (value % 3 == 0) {
                flag = true
            }
            flag
        })
        println(filterRdd.collect().mkString(", "))
    }
}
