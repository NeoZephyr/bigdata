package com.pain.sea

import org.apache.spark.{SparkConf, SparkContext}

object CreateRDD {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("create rdd").setMaster("local[*]")
    val sparkContext = new SparkContext(sparkConf)
    val rdd1 = sparkContext.parallelize(Array(1, 2, 3, 4, 5), 2)
    val rdd2 = sparkContext.makeRDD(Array(1, 2, 3, 4, 5), 3)
    val rdd3 = sparkContext.textFile("input")

    rdd1.collect().foreach(println)
    rdd2.collect().foreach(println)
    rdd3.collect().foreach(println)

    sparkContext.stop()
  }
}
