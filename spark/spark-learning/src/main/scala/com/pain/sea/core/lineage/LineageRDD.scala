package com.pain.sea.core.lineage

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object LineageRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("lineage").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[String] = sparkContext.textFile("input/words.txt")
        val wordsRdd: RDD[String] = rdd.flatMap(_.split(" "))
        val wordToCountRdd: RDD[(String, Int)] = wordsRdd.map((_, 1))
        val wordToSumRdd: RDD[(String, Int)] = wordToCountRdd.reduceByKey(_ + _)
        println("wordsRdd debugString")
        println(wordsRdd.toDebugString)
        println(wordsRdd.dependencies)

        println("wordToCountRdd debugString")
        println(wordToCountRdd.toDebugString)
        println(wordToCountRdd.dependencies)

        println("wordToSumRdd debugString")
        println(wordToSumRdd.toDebugString)
        println(wordToSumRdd.dependencies)

        wordToSumRdd.collect().foreach(println)
    }
}
