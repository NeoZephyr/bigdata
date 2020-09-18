package com.pain.sea.core.base.transform

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GroupByApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("groupBy app").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val groupByRdd: RDD[(Int, Iterable[Int])] = rdd.groupBy(_ % 3)
        groupByRdd.foreach((item: (Int, Iterable[Int])) => {
            val value: String = item._2.mkString(", ")
            println(s"${item._1}, (${value})")
        })
    }
}
