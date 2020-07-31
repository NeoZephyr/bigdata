package com.pain.sea.sql

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkSessionApp {

    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        val dataFrame: DataFrame = spark.read.text("/Users/pain/Documents/bigdata/spark/spark-learning/input/words.txt")

        dataFrame.printSchema()
        dataFrame.show(false)

        spark.stop()
    }
}
