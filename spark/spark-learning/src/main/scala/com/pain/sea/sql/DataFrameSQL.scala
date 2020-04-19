package com.pain.sea.sql

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object DataFrameSQL {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("pass").setMaster("local[*]")
        val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

        // dataFrameViewTest(spark)
        // dataFrameDslTest(spark)
        // dataFrameToRdd(spark)
        // rddToDataFrameTest1(spark)
        // rddToDataFrameTest2(spark)
        // rddToDataFrameTest3(spark)
        // createDataSetTest(spark)
        // rddToDataSetTest(spark)
        // dataSetToRddTest(spark)
        dataSetToDataFrameTest(spark)
        // dataFrameToDataSetTest(spark)
    }

    def dataFrameViewTest(spark: SparkSession): Unit = {
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.createOrReplaceTempView("student")
        val studentDataFrame: DataFrame = spark.sql("select * from student")
        studentDataFrame.show()
        dataFrame.createGlobalTempView("stu")
        val globalStudentDataFrame: DataFrame = spark.sql("select * from global_temp.stu")
        globalStudentDataFrame.show()
    }

    def dataFrameDslTest(spark: SparkSession): Unit = {
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.select("name").show()
        dataFrame.groupBy("score").count().show()
        dataFrame.printSchema()
    }

    def dataFrameToRdd(spark: SparkSession): Unit = {
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        val rdd: RDD[Row] = dataFrame.rdd
        rdd.collect().foreach(println)
    }

    def rddToDataFrameTest1(spark: SparkSession): Unit = {
        import spark.implicits._
        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val studentRdd: RDD[(String, String, Int)] = rdd.map(line => {
            val items = line.split(",")
            (items(0), items(1), items(2).toInt)
        })
        val dataFrame: DataFrame = studentRdd.toDF("id", "name", "score")
        dataFrame.show()
    }

    def rddToDataFrameTest2(spark: SparkSession): Unit = {
        import spark.implicits._
        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val studentRdd: RDD[Student] = rdd.map(line => {
            val items = line.split(",")
            Student(items(0).toInt, items(1), items(2).toInt)
        })
        val dataFrame: DataFrame = studentRdd.toDF("id", "name", "score")
        dataFrame.show()
    }

    def rddToDataFrameTest3(spark: SparkSession): Unit = {
        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val studentRdd: RDD[Row] = rdd.map(line => {
            val items = line.split(",")
            Row(items(0).toInt, items(1), items(2).toInt)
        })
        val structType: StructType = StructType(StructField("id", IntegerType) :: StructField("name", StringType) :: StructField("score", IntegerType) :: Nil)
        val dataFrame: DataFrame = spark.createDataFrame(studentRdd, structType)
        dataFrame.show()
    }

    def createDataSetTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataSet: Dataset[Student] = Seq(Student(1, "john", 88)).toDS()
        dataSet.show()
    }

    def rddToDataSetTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val rdd: RDD[String] = spark.sparkContext.textFile("io/text/student.txt")
        val studentRdd: RDD[Student] = rdd.map(line => {
            val items = line.split(",")
            Student(items(0).toInt, items(1), items(2).toInt)
        })
        val dataSet: Dataset[Student] = studentRdd.toDS()
        dataSet.show()
    }

    def dataSetToRddTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataSet: Dataset[Student] = Seq(Student(1, "john", 88)).toDS()
        val rdd: RDD[Student] = dataSet.rdd
        rdd.collect().foreach(println)
    }

    def dataSetToDataFrameTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataSet: Dataset[Student] = Seq(Student(1, "john", 88)).toDS()
        val dataFrame: DataFrame = dataSet.toDF()
        dataFrame.show()
    }

    def dataFrameToDataSetTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        val dataSet: Dataset[Student] = dataFrame.as[Student]
        dataSet.show()
    }
}

case class Student(id: Long, name: String, score: Long)
