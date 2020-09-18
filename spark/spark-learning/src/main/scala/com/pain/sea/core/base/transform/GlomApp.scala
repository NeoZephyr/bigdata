package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GlomApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("glom").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 32, 4)

        // 将每一个分区形成一个数组
        val glomRdd: RDD[Array[Int]] = rdd.glom()

        glomRdd.collect().foreach(it => {
            println(it.mkString(", "))
        })
    }
}
