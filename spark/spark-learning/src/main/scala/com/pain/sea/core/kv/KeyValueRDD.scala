package com.pain.sea.core.kv

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object KeyValueRDD {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("key value rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // cogroupTest(sparkContext)
        // mapValuesTest(sparkContext)
        // aggregateByKeyTest(sparkContext)
        // combineByKeyTest(sparkContext)
        foldByKeyTest(sparkContext)
        sparkContext.stop()
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
