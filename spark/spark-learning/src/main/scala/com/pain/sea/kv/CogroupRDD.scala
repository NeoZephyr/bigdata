package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CogroupRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("cogroup").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd1: RDD[(Int, String)] = sparkContext.makeRDD(List((1, "a"), (2, "b"), (3, "c")))
        val rdd2: RDD[(Int, String)] = sparkContext.makeRDD(List((1, "A"), (2, "B"), (3, "C")))

        // 将 key 相同的数据聚合到一个迭代器
        val cogroupRdd: RDD[(Int, (Iterable[String], Iterable[String]))] = rdd1.cogroup(rdd2)

        cogroupRdd.collect().foreach(println)
    }
}