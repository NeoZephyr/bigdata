package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SetApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("set app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd1: RDD[Int] = sparkContext.makeRDD(List(2, 3, 5))
        val rdd2: RDD[Int] = sparkContext.makeRDD(List(3, 5, 7))

        val unionRdd: RDD[Int] = rdd1.union(rdd2)
        println(unionRdd.collect().mkString(", "))

        val interRdd: RDD[Int] = rdd2.intersection(rdd1)
        println(interRdd.collect().mkString(", "))

        val subRdd: RDD[Int] = rdd2.subtract(rdd1)
        println(subRdd.collect().mkString(", "))

        val cartRdd: RDD[(Int, Int)] = rdd1.cartesian(rdd2)
        println(cartRdd.collect().mkString(", "))

        val zipRdd: RDD[(Int, Int)] = rdd1.zip(rdd2)
        println(zipRdd.collect().mkString(", "))

        val distinctRdd: RDD[Int] = unionRdd.distinct(2)
        println(distinctRdd.collect().mkString(", "))
    }
}
