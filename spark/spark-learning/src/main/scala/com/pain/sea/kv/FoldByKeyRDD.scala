package com.pain.sea.kv

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object FoldByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("fold by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("a", 3), ("a", 2), ("c", 4), ("b", 3), ("c", 6), ("c", 8)), 2)

        // 计算相同 key 对应值的相加结果
        val foldRdd: RDD[(String, Int)] = rdd.foldByKey(0)(_ + _)
        foldRdd.collect().foreach(println)
    }
}
