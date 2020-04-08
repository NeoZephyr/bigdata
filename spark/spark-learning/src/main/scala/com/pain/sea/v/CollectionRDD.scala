package com.pain.sea.v

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CollectionRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("union rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd1: RDD[Int] = sparkContext.makeRDD(Range(1, 10, 2))
        val rdd2: RDD[Int] = sparkContext.makeRDD(Range(2, 10, 2))

        val rdd3: RDD[Int] = rdd1.union(rdd2)

        println("union")
        rdd3.collect().foreach(println)

        val rdd4: RDD[Int] = rdd3.intersection(rdd1)

        println("intersection")
        rdd4.collect().foreach(println)

        val rdd5: RDD[Int] = rdd3.subtract(rdd1)

        println("subtract")
        rdd5.collect().foreach(println)

        val rdd6: RDD[(Int, Int)] = rdd1.cartesian(rdd2)

        println("cartesian")
        rdd6.collect().foreach(println)

        val rdd7: RDD[(Int, Int)] = rdd1.zip(rdd2)

        println("zip")
        rdd7.collect().foreach(println)
    }
}
