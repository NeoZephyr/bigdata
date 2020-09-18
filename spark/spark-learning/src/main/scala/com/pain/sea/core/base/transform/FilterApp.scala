package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

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
