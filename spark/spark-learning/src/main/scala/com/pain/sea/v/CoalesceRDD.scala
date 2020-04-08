package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CoalesceRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("coalesce rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16, 4)

        println(s"partition number: ${rdd.partitions.size}")

        rdd.glom().collect().foreach(it => println(it.mkString(",")))

        val repartitionRdd: RDD[Int] = rdd.repartition(3)
        val coalesceRdd: RDD[Int] = rdd.coalesce(3)

        println(s"repartition number: ${repartitionRdd.partitions.size}")

        repartitionRdd.glom().collect().foreach(it => println(it.mkString(",")))

        println(s"coalesce number: ${coalesceRdd.partitions.size}")

        coalesceRdd.glom().collect().foreach(it => println(it.mkString(",")))
    }

}
