package com.pain.sea.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object KeyValueRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("key value rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // partitionByTest(sparkContext)
        // groupByKeyTest(sparkContext)
        // reduceByKeyTest(sparkContext)
        // sortByKeyTest(sparkContext)
        // joinTest(sparkContext)
        // cogroupTest(sparkContext)
        // mapValuesTest(sparkContext)
        // aggregateByKeyTest(sparkContext)
        // combineByKeyTest(sparkContext)
        foldByKeyTest(sparkContext)
        sparkContext.stop()
    }

    //noinspection DuplicatedCode
    def partitionByTest(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[(Int, String)] = sparkContext.makeRDD(Array((1, "jack"), (2, "ray"), (3, "geo"), (4, "paul"), (5, "slack")), 4)
        val rdd1WithPartition: RDD[(Int, (Int, String))] = rdd1.mapPartitionsWithIndex((idx, item) => {
            item.map((idx, _))
        })
        rdd1WithPartition.collect().foreach(println)
        println(s"rdd1 partition size: ${rdd1.partitions.length}")

        val rdd2: RDD[(Int, String)] = rdd1.partitionBy(new HashPartitioner(2))
        val rdd2WithPartition: RDD[(Int, (Int, String))] = rdd2.mapPartitionsWithIndex((idx, item) => {
            item.map((idx, _))
        })
        rdd2WithPartition.collect().foreach(println)
        println(s"rdd2 partition size: ${rdd2.partitions.length}")
    }

    //noinspection DuplicatedCode
    def groupByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.makeRDD(Array("spark", "hbase", "spark", "hive", "spark", "hive"))
        val wordToOneRdd: RDD[(String, Int)] = rdd.map((_, 1))
        val groupByKeyRdd: RDD[(String, Iterable[Int])] = wordToOneRdd.groupByKey()

        groupByKeyRdd.collect().foreach(item => {
            val value = item._2.mkString(", ")
            println(s"(${item._1}, (${value}))")
        })

        val sumRdd: RDD[(String, Int)] = groupByKeyRdd.map(x => (x._1, x._2.sum))
        sumRdd.collect().foreach(println)
    }

    //noinspection DuplicatedCode
    def reduceByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.makeRDD(Array("spark", "hbase", "spark", "hive", "spark", "hive"))
        val wordToOneRdd: RDD[(String, Int)] = rdd.map((_, 1))

        val reduceByKeyRdd: RDD[(String, Int)] = wordToOneRdd.reduceByKey(_ + _)
        reduceByKeyRdd.collect().foreach(println)
    }

    def sortByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("durant", 98), ("curry", 97), ("harden", 97)))

        // key 必须实现 Ordered 接口
        val ascRdd: RDD[(String, Int)] = rdd.sortByKey(true)
        val descRdd: RDD[(String, Int)] = rdd.sortByKey(false)

        ascRdd.collect().foreach(println)
        descRdd.collect().foreach(println)
    }

    //noinspection DuplicatedCode
    def joinTest(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 99), ("harden", 98)))
        val rdd2: RDD[(String, Int)] = sparkContext.makeRDD(List(("durant", 99), ("leBron", 99), ("curry", 98)))

        // 将 key 相同的数据聚合到一个元组
        val joinRdd: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
        joinRdd.collect().foreach(println)
    }

    //noinspection DuplicatedCode
    def cogroupTest(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 99), ("harden", 98), ("leBron", 100)))
        val rdd2: RDD[(String, Int)] = sparkContext.makeRDD(List(("durant", 99), ("leBron", 99), ("curry", 98)))

        // 将 key 相同的数据聚合到一个迭代器
        val cogroupRdd: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd1.cogroup(rdd2)
        cogroupRdd.collect().foreach(item => {
            val value1 = item._2._1.mkString(", ")
            val value2 = item._2._2.mkString(", ")
            println(s"(${item._1}, (${value1}), (${value2}))")
        })
    }

    //noinspection DuplicatedCode
    def mapValuesTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 99), ("harden", 98), ("leBron", 100)))
        val mapValuesRdd: RDD[(String, Double)] = rdd.mapValues(_ / 10.toDouble)
        mapValuesRdd.collect().foreach(println)
    }

    //noinspection DuplicatedCode
    def aggregateByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 98), ("curry", 99), ("harden", 98), ("leBron", 100), ("harden", 97)), 2)
        rdd.foreachPartition(part => {
            println(part.mkString(", "))
        })

        // 取出每个分区相同 key 对应值的最大值，然后相加
        val aggRdd: RDD[(String, Int)] = rdd.aggregateByKey(0)(math.max(_, _), _ + _)
        aggRdd.collect().foreach(println)
    }

    def combineByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 98), ("curry", 99), ("harden", 98), ("leBron", 100), ("harden", 97)), 2)

        // 将相同 key 对应的值相加，同时记录该 key 出现的次数
        val combineRdd: RDD[(String, (Int, Int))] = rdd.combineByKey(
            (_, 1),
            (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
            (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
        combineRdd.collect().foreach(println)

        val avgRdd: RDD[(String, Double)] = combineRdd.map {
            case (key, value) => (key, value._1 / value._2.toDouble)
        }
        avgRdd.collect().foreach(println)
    }

    def foldByKeyTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 99), ("curry", 98), ("curry", 99), ("harden", 98), ("leBron", 100), ("harden", 97)), 2)

        // 计算相同 key 对应值的相加结果
        val foldRdd: RDD[(String, Int)] = rdd.foldByKey(0)(_ + _)
        foldRdd.collect().foreach(println)
    }
}
