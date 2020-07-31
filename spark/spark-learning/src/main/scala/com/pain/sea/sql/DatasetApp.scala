package com.pain.sea.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object DatasetApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        import spark.implicits._
        val dataset: Dataset[Person] = Seq(Person("pain", 28)).toDS()
        dataset.show()

        Seq(1, 100, 100).toDS().show()

        val peopleDF: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/people.json")
        val peopleDS: Dataset[Person] = peopleDF.as[Person]

        peopleDS.map(people => people.name).show()

        spark.stop()
    }

    case class Person(name: String, age: Long)
}
