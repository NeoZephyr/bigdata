package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CombineByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("combine by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98)), 2)

        // 将相同 key 对应的值相加，同时记录该 key 出现的次数
        val combineRdd: RDD[(String, (Int, Int))] = rdd.combineByKey(
            (_, 1),
            (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
            (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
        combineRdd.collect().foreach(println)

        val avgRdd: RDD[(String, Double)] = combineRdd.map {
            case (key, value) => (key, value._1 / value._2.toDouble)
        }
        avgRdd.collect().foreach(println)
    }
}
