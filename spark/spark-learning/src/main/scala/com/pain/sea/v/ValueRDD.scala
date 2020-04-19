package com.pain.sea.v

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object ValueRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("value rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // createRDDTest(sparkContext)
        // mapTest(sparkContext)
        // flatMapTest(sparkContext)
        // filterTest(sparkContext)
        // glomTest(sparkContext)
        // groupByTest(sparkContext)
        // distinctTest(sparkContext)
        // sortByTest(sparkContext)
        // collectionTest(sparkContext)
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

    def mapTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 4)

        // 每次处理一条数据
        val mapRdd: RDD[Int] = rdd.map(_ * 10)
        println(mapRdd.collect().mkString(", "))

        // 每次处理一个分区的数据
        // 这个分区的数据处理完后，原 RDD 中分区的数据才能释放，可能导致 OOM
        val mapPartitionRdd: RDD[Int] = rdd.mapPartitions(it => {
            it.map(_ * 100)
        })
        println(mapPartitionRdd.collect().mkString(", "))

        val mapPartitionWithIndexRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((idx, it) => {
            it.map((idx, _))
        })
        println(mapPartitionWithIndexRdd.collect().mkString(", "))
    }

    def flatMapTest(sparkContext: SparkContext): Unit = {
        val linesRdd: RDD[String] = sparkContext.textFile("input")
        val wordsRdd: RDD[String] = linesRdd.flatMap(_.split(" "))
        println(wordsRdd.collect().mkString(", "))
    }

    def filterTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val filterRdd: RDD[Int] = rdd.filter(value => {
            var flag = false
            if (value % 3 == 0) {
                flag = true
            }
            flag
        })
        println(filterRdd.collect().mkString(", "))
    }

    def glomTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 32, 4)

        // 将每一个分区形成一个数组
        val glomRdd: RDD[Array[Int]] = rdd.glom()

        glomRdd.collect().foreach(it => {
            println(it.mkString(", "))
        })
    }

    def groupByTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 16)
        val groupByRdd: RDD[(Int, Iterable[Int])] = rdd.groupBy(_ % 3)
        groupByRdd.collect().foreach(item => {
            val value = item._2.mkString(", ")
            println(s"${item._1}, (${value})")
        })
    }

    def distinctTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(List(90, 20, 50, 20, 30, 90, 10))
        val distinctRdd: RDD[Int] = rdd.distinct(2)

        println(distinctRdd.collect().mkString(", "))
    }

    def sortByTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(List(10, 20, 100, 200, 300, 400, 350, 250, 150, 50), 2)
        val sortByRdd: RDD[Int] = rdd.sortBy(x => x, false)

        println(sortByRdd.collect().mkString(", "))
    }

    def collectionTest(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[Int] = sparkContext.makeRDD(Range(1, 10, 2))
        val rdd2: RDD[Int] = sparkContext.makeRDD(Range(2, 10, 2))

        val rdd3: RDD[Int] = rdd1.union(rdd2)
        println(rdd3.collect().mkString(", "))

        val rdd4: RDD[Int] = rdd3.intersection(rdd1)
        println(rdd4.collect().mkString(", "))

        val rdd5: RDD[Int] = rdd3.subtract(rdd1)
        println(rdd5.collect().mkString(", "))

        val rdd6: RDD[(Int, Int)] = rdd1.cartesian(rdd2)
        println(rdd6.collect().mkString(", "))

        val rdd7: RDD[(Int, Int)] = rdd1.zip(rdd2)
        println(rdd7.collect().mkString(", "))
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
