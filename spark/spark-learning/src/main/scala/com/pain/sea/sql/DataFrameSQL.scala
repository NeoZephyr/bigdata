package com.pain.sea.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object DataFrameSQL {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("pass").setMaster("local[*]")
        val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    }

    def udfTest(spark: SparkSession): Unit = {
        spark.udf.register("upper", (text: String) => text.toUpperCase)
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.createOrReplaceTempView("student")
        val studentDataFrame: DataFrame = spark.sql("select id, upper(name) as name, score from student")
        studentDataFrame.show()
    }

    def aggFuncUdfTest(spark: SparkSession): Unit = {
        spark.udf.register("avgFunc", AverageFunction)
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.createOrReplaceTempView("student")
        val studentDataFrame: DataFrame = spark.sql("select avgFunc(score) from student")
        studentDataFrame.show()
    }

    def aggUdfTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataSet: Dataset[Student] = spark.read.json("io/json/student.json").as[Student]
        val avgScore = AverageAgg.toColumn.name("avg_score")
        val scoreDataSet: Dataset[Double] = dataSet.select(avgScore)
        scoreDataSet.show()
    }
}

case class Student(id: Long, name: String, score: Long)
case class Average(var sum: Long, var count: Long)