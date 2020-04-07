package com.pain.sea

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GroupByRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("groupBy rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val groupByRdd: RDD[(Int, Iterable[Int])] = rdd.groupBy(_ % 3)

        groupByRdd.collect().foreach(println)
    }
}
