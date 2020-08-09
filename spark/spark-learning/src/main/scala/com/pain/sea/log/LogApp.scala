package com.pain.sea.log

import com.pain.sea.log.processor.{AppStatProcessor, AreaStatProcessor, LogETLProcessor, ProvinceCityStatProcessor}
import org.apache.spark.sql.SparkSession

object LogApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

//        LogETLProcessor.process(spark)
//        ProvinceCityStatProcessor.process(spark)
//        AreaStatProcessor.process(spark)
        AppStatProcessor.process(spark)

        spark.stop()
    }
}
