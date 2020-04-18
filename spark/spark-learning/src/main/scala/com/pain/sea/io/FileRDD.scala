package com.pain.sea.io

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.parsing.json.JSON

object FileRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("file").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // textTest(sparkContext)
        // jsonTest(sparkContext)
        // sequenceTest(sparkContext)
        objectTest(sparkContext)
    }

    def textTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.textFile("io/text/words.txt")
        rdd.collect().foreach(println)
        val wordCountRdd: RDD[(String, Int)] = rdd.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
        wordCountRdd.saveAsTextFile("io/text/output")
    }

    def jsonTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.textFile("io/json/student.json")
        val jsonRdd: RDD[Option[Any]] = rdd.map(JSON.parseFull)
        jsonRdd.collect().foreach(println)
    }

    def sequenceTest(sparkContext: SparkContext): Unit = {
//        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("jack", 100), ("pain", 99), ("john", 80)), 1)
//        rdd.saveAsSequenceFile("io/sequence/output")
        val readRdd: RDD[(String, Int)] = sparkContext.sequenceFile("io/sequence/output")
        readRdd.collect().foreach(println)
    }

    def objectTest(sparkContext: SparkContext): Unit = {
//        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("jack", 100), ("pain", 99), ("john", 80)), 1)
//        rdd.saveAsObjectFile("io/object/output")

        val readRdd: RDD[(String, Int)] = sparkContext.objectFile("io/object/output")
        readRdd.collect().foreach(println)
    }
}
