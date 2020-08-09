package com.pain.sea.log.utils

import org.apache.spark.sql.SparkSession

object TableUtils {
    def getTableName(tableName:String, spark:SparkSession) = {
        val time = spark.sparkContext.getConf.get("spark.time")

        if (!time.isEmpty) {
            tableName + "_" + time
        } else {
            tableName
        }
    }
}
