package com.pain.sea.core.v

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object ValueRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("value rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // createRDDTest(sparkContext)
        // glomTest(sparkContext)
        // sortByTest(sparkContext)
        // sampleTest(sparkContext)
        coalesceTest(sparkContext)

        sparkContext.stop()
    }

    def createRDDTest(sparkContext: SparkContext): Unit = {
        val rdd1 = sparkContext.parallelize(Array(1, 2, 3, 4, 5), 2)
        val rdd2 = sparkContext.makeRDD(Array(1, 2, 3, 4, 5), 3)
        val rdd3 = sparkContext.textFile("input")

        println(rdd1.collect().mkString(", "))
        println(rdd2.collect().mkString(", "))
        println(rdd3.collect().mkString(", "))
    }

    def glomTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 32, 4)

        // 将每一个分区形成一个数组
        val glomRdd: RDD[Array[Int]] = rdd.glom()

        glomRdd.collect().foreach(it => {
            println(it.mkString(", "))
        })
    }

    def sortByTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(List(10, 20, 100, 200, 300, 400, 350, 250, 150, 50), 2)
        val sortByRdd: RDD[Int] = rdd.sortBy(x => x, false)

        println(sortByRdd.collect().mkString(", "))
    }

    def sampleTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val sampleRdd1: RDD[Int] = rdd.sample(true, 0.4, System.currentTimeMillis())
        println(sampleRdd1.collect().mkString(", "))

        val sampleRdd2: RDD[Int] = rdd.sample(false, 0.4, System.currentTimeMillis())
        println(sampleRdd2.collect().mkString(", "))
    }

    def coalesceTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16, 4)
        rdd.glom().collect().foreach(it => println(it.mkString(", ")))
        println(s"partition number: ${rdd.partitions.length}")

        val repartitionRdd: RDD[Int] = rdd.repartition(3)
        repartitionRdd.glom().collect().foreach(it => println(it.mkString(",")))
        println(s"repartition number: ${repartitionRdd.partitions.length}")

        val coalesceRdd: RDD[Int] = rdd.coalesce(3)
        coalesceRdd.glom().collect().foreach(it => println(it.mkString(",")))
        println(s"coalesce number: ${coalesceRdd.partitions.length}")
    }
}
