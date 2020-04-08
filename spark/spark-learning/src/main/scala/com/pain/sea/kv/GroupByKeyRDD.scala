package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GroupByKeyRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("group by key").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[String] = sparkContext.makeRDD(Array("spark", "hbase", "spark", "hive", "spark", "hive"))
        val wordToOneRdd: RDD[(String, Int)] = rdd.map((_, 1))
        val groupByKeyRdd: RDD[(String, Iterable[Int])] = wordToOneRdd.groupByKey()

        groupByKeyRdd.collect().foreach(println)

        val sumRdd: RDD[(String, Int)] = groupByKeyRdd.map(x => (x._1, x._2.sum))
        sumRdd.collect().foreach(println)

    }
}
