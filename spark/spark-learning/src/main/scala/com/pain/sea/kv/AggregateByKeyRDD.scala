package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object AggregateByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("aggregate by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("a", 3), ("a", 2), ("c", 4), ("b", 3), ("c", 6), ("c", 8)), 2)
        rdd.foreachPartition(part => {
            println(part.mkString(", "))
        })

        // 取出每个分区相同 key 对应值的最大值，然后相加
        val aggRdd: RDD[(String, Int)] = rdd.aggregateByKey(0)(math.max(_, _), _ + _)
        aggRdd.collect().foreach(println)
    }
}
