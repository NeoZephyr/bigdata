package com.pain.sea.core.base.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object ActionApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("action").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // testReduce(sparkContext)
        // testCollect(sparkContext)
        // testCount(sparkContext)
        // testFirst(sparkContext)
        // testTake(sparkContext)
        // testTakeOrdered(sparkContext)
        testAggregate(sparkContext)
        // testFold(sparkContext)
        // testCountByKey(sparkContext)
        // testSaveAsFile(sparkContext)
    }

    /**
     * 先聚合分区内数据，再聚合分区间数据
     * @param sparkContext
     */
    def testReduce(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("a", 1), ("a", 3), ("c", 3), ("d", 5)))
        val tuple: (String, Int) = rdd.reduce((x, y) => (x._1 + y._1, x._2 + y._2))
        println(tuple)
    }

    /**
     * 以数组的形式返回数据集的所有元素
     * @param sparkContext
     */
    def testCollect(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10)
        println(rdd.collect().mkString(", "))
    }

    /**
     * 返回 RDD 中元素的个数
     * @param sparkContext
     */
    def testCount(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10)
        println(rdd.count())
    }

    /**
     * 返回 RDD 中的第一个元素
     * @param sparkContext
     */
    def testFirst(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(List(10))
        println(rdd.first())
    }

    /**
     * 返回前 n 个元素组成的数组
     * @param sparkContext
     */
    def testTake(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10)
        println(rdd.take(3).mkString(", "))
    }

    /**
     * 返回排序后的前 n 个元素组成的数组
     * @param sparkContext
     */
    def testTakeOrdered(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(List(598, 618, 221, 1221, 184, 1949))
        rdd.takeOrdered(3).foreach(println)
    }

    /**
     * 通过 seqOp 和初始值进行聚合，通过 combine 将每个分区的结果和初始值进行聚合
     * @param sparkContext
     */
    def testAggregate(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 2)
        val aggValue: Int = rdd.aggregate(100)(_ + _, _ + _)
        println(aggValue)
    }

    /**
     * aggregate 的简化操作，seqop 和 combop 一样
     * @param sparkContext
     */
    def testFold(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 5)
        val foldValue: Int = rdd.fold(100)(_ + _)
        println(foldValue)
    }

    def testCountByKey(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(List((1, 3), (1, 2), (1, 4), (2, 3), (3, 6), (3, 8)), 2)
        val keyToCount: collection.Map[Int, Long] = rdd.countByKey()
        println(keyToCount)
    }

    def testSaveAsFile(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 2)
        rdd.saveAsTextFile("output/test")
        rdd.saveAsObjectFile("output/object")
    }
}
