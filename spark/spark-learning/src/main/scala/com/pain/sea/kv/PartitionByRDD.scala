package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object PartitionByRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("partition by").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd1: RDD[(Int, String)] = sparkContext.makeRDD(Array((1, "jack"), (2, "ray"), (3, "geo"), (4, "paul"), (5, "slack")), 4)

        println(s"rdd1 partition size: ${rdd1.partitions.size}")

        val rdd2: RDD[(Int, String)] = rdd1.partitionBy(new HashPartitioner(2))

        println(s"rdd2 partition size: ${rdd2.partitions.size}")

    }
}
