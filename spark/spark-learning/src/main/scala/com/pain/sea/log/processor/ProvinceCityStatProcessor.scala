package com.pain.sea.log.processor

import com.pain.sea.log.`trait`.DataProcess
import com.pain.sea.log.utils.{KuduUtils, SQLUtils, SchemaUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}

object ProvinceCityStatProcessor extends DataProcess {
    override def process(spark: SparkSession): Unit = {
        val master = "cdh"
        val dataFrame: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
            .option("kudu.master", master)
            .option("kudu.table", "ods")
            .load()

        dataFrame.createOrReplaceTempView("ods")
        val provinceCityDataFrame: DataFrame = spark.sql(SQLUtils.PROVINCE_CITY_SQL)

        KuduUtils.sink(provinceCityDataFrame, "province_city_stat", master, SchemaUtils.ProvinceCitySchema, "provincename")
    }
}
