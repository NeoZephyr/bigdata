package com.pain.sea.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object RDDApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        // arrayRdd(spark)
        // rowRdd(spark)
        classRdd(spark)
    }

    def arrayRdd(spark: SparkSession): Unit = {
        import spark.implicits._

        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val arrayRdd: RDD[(String, String, Int)] = rdd.map(line => {
            val items: Array[String] = line.split(",")
            (items(0), items(1), items(2).toInt)
        })
        val dataFrame: DataFrame = arrayRdd.toDF("id", "name", "score")
        dataFrame.show()
    }

    def rowRdd(spark: SparkSession): Unit = {
        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val rowRdd: RDD[Row] = rdd.map(line => {
            val items: Array[String] = line.split(",")
            Row(items(0).toInt, items(1), items(2).toInt)
        })
        val structType: StructType = StructType(StructField("id", IntegerType) ::
            StructField("name", StringType) ::
            StructField("score", IntegerType) :: Nil)
        val dataFrame: DataFrame = spark.createDataFrame(rowRdd, structType)
        dataFrame.show()
    }

    def classRdd(spark: SparkSession): Unit = {
        import spark.implicits._

        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val studentRdd: RDD[Student] = rdd.map(line => {
            val items: Array[String] = line.split(",")
            Student(items(0).toInt, items(1), items(2).toInt)
        })
        val dataFrame: DataFrame = studentRdd.toDF("id", "name", "score")
        val dataset: Dataset[Student] = studentRdd.toDS()
        dataFrame.show()
        dataset.show()
    }

    case class Student(id: Long, name: String, score: Long)
}
