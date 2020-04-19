package com.pain.sea.core.io

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}

object DBRdd {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("db").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        mysqlTestRead(sparkContext)
        // mysqlTestWrite(sparkContext)
    }

    def mysqlTestRead(sparkContext: SparkContext): Unit = {
        val driver = "com.mysql.jdbc.Driver"
        val url = "jdbc:mysql://localhost:3306/spark?useSSL=false"
        val username = "mysql"
        val password = "123456"

        val rdd = new JdbcRDD(sparkContext, () => {
            Class.forName(driver)
            DriverManager.getConnection(url, username, password)
        },
            "select * from `student` where `id` >= ? and `id` <= ?;",
            1,
            2,
            1,
            result => (result.getInt(1), result.getString(2))
        )

        println(rdd.count())
        rdd.collect().foreach(println)
    }

    def mysqlTestWrite(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("jack", 100), ("pain", 99)))
        rdd.foreachPartition(insertIntoMysql)
    }

    def insertIntoMysql(iterator: Iterator[(String, Int)]): Unit = {
        val driver = "com.mysql.jdbc.Driver"
        val url = "jdbc:mysql://localhost:3306/spark"
        val username = "mysql"
        val password = "123456"
        Class.forName(driver)
        val connection: Connection = DriverManager.getConnection(url, username, password)

        iterator.foreach(stu => {
            val statement: PreparedStatement = connection.prepareStatement("insert into student(name, score) values (?, ?)")
            statement.setString(1, stu._1)
            statement.setInt(2, stu._2)
            statement.execute()
        })
    }
}
