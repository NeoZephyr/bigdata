package com.pain.sea.sql

import org.apache.spark.sql.{DataFrame, SparkSession}

object DataFrameApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        import spark.implicits._

        val people: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/people.json")
        people.printSchema()
        people.show()
        people.show(3, 10, vertical = true)
        people.select("name").show()
        people.select($"name").show()
        people.filter("age > 21").show()
        people.filter($"age" > 21).show()
        people.groupBy("age").count().show()
        people.select($"name", ($"age" + 10).as("+10")).show()

        people.createOrReplaceTempView("people")
        spark.sql("select * from people where age > 21").show()

        people.createOrReplaceGlobalTempView("people")
        spark.sql("select * from global_temp.people where age > 21").show()

        val zip: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/zips.json")
        zip.printSchema()
        zip.show(false)
        zip.head(3).foreach(println)
        zip.take(3).foreach(println)

        import org.apache.spark.sql.functions._

        zip.filter(zip.col("pop") > 50000).withColumnRenamed("_id", "id").show(false)
        zip.select($"_id".as("id"), $"city", $"pop", $"state").filter(zip.col("state") === "CA").orderBy(desc("pop")).show(false)

        zip.createOrReplaceTempView("zip")
        spark.sql("select _id as id, city, pop, state from zip where state = 'CA' order by pop desc limit 10").show(false)

        println(s"zip count: ${zip.count()}")

        spark.stop()
    }
}
