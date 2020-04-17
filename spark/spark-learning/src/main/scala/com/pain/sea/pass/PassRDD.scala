package com.pain.sea.pass

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object PassRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("pass").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[String] = sparkContext.makeRDD(List("spark stream", "kafka stream", "hive"))

        val search = new Search("stream")
        // val matchItems: RDD[String] = search.getMatch1(rdd)
        val matchItems: RDD[String] = search.getMatch2(rdd)
        matchItems.collect().foreach(println)
    }
}

class Search(query: String) {
    def isMatch(content: String): Boolean = {
        content.contains(query)
    }

    def getMatch1(rdd: RDD[String]): RDD[String] = {
        rdd.filter(isMatch)
    }

    def getMatch2(rdd: RDD[String]): RDD[String] = {
        // 将类变量赋值给局部变量
        val tmpQuery = query
        // rdd.filter(content => content.contains(query))
        rdd.filter(content => content.contains(tmpQuery))
    }
}
