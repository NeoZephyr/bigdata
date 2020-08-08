package com.pain.sea.log.processor

import com.pain.sea.log.`trait`.DataProcess
import com.pain.sea.log.utils.{IPUtils, KuduUtils, SQLUtils, SchemaUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object LogETLProcessor extends DataProcess {
    override def process(spark: SparkSession): Unit = {
        import spark.implicits._

        val rdd: RDD[String] = spark.sparkContext.textFile("/Users/pain/Documents/bigdata/spark/spark-learning/input/ip.txt")
        val ipDF: DataFrame = rdd.map(line => {
            val items: Array[String] = line.split("\\|")
            val startIp: Long = items(2).toLong
            val endIp: Long = items(3).toLong
            val province: String = items(6).toString
            val city: String = items(7).toString
            val isp: String = items(9).toString
            (startIp, endIp, province, city, isp)
        }).toDF("start_ip", "end_ip", "province", "city", "isp")

        var logDF: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/log.json")

        import org.apache.spark.sql.functions._

        def getLongIp() = udf((ip: String) => {
            IPUtils.ipToLong(ip)
        })

        logDF = logDF.withColumn("ip_long", getLongIp()($"ip"))

        // logDF.join(ipDF, logDF("ip_long").between(ipDF("start_ip"), ipDF("end_ip"))).show(false)

        logDF.createOrReplaceTempView("logs")
        ipDF.createOrReplaceTempView("ips")

        val result: DataFrame = spark.sql(SQLUtils.SQL)

        KuduUtils.sink(result, "ods", "cdh", SchemaUtils.ODSSchema, "ip")
    }
}
