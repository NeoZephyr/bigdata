package com.pain.sea.core.kv

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("word count").setMaster("local[*]")
    val sparkContext = new SparkContext(sparkConf)

    sparkContext.textFile(args(0))
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _, 1)
      .sortBy(_._2, false)
      .saveAsTextFile(args(1))

    sparkContext.stop()
  }
}
