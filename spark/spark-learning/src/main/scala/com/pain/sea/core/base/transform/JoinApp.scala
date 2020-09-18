package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object JoinApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("join app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 99), ("harden", 98)))
        val rdd2: RDD[(String, Int)] = sparkContext.makeRDD(List(("durant", 99), ("leBron", 99), ("curry", 98), ("leBron", 97)))

        // 将 key 相同的数据聚合到一个元组
        val joinRdd: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
        joinRdd.collect().foreach(println)

        val leftJoinRdd: RDD[(String, (Int, Option[Int]))] = rdd1.leftOuterJoin(rdd2)
        leftJoinRdd.collect().foreach(println)

        val fullJoinRdd: RDD[(String, (Option[Int], Option[Int]))] = rdd1.fullOuterJoin(rdd2)
        fullJoinRdd.collect().foreach(println)
    }
}
