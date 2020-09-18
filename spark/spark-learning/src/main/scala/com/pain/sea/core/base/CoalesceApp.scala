package com.pain.sea.core.base

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CoalesceApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("coalesce").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16, 4)
        rdd.glom().collect().foreach(it => println(it.mkString(", ")))
        println(s"partition number: ${rdd.partitions.length}")

        val repartitionRdd: RDD[Int] = rdd.repartition(3)
        repartitionRdd.glom().collect().foreach(it => println(it.mkString(",")))
        println(s"repartition number: ${repartitionRdd.partitions.length}")

        val coalesceRdd: RDD[Int] = rdd.coalesce(3)
        coalesceRdd.glom().collect().foreach(it => println(it.mkString(",")))
        println(s"coalesce number: ${coalesceRdd.partitions.length}")
    }
}
