package com.pain.red.app.join.reduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class OrderJoinDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide two args");
        }

        Configuration configuration = new Configuration();
//        configuration.set("fs.defaultFs", "hdfs://cdh:8020");
        Job job = Job.getInstance(configuration);

        job.setJarByClass(OrderJoinDriver.class);

        job.setMapperClass(OrderMapper.class);
        job.setReducerClass(OrderReducer.class);

        job.setMapOutputKeyClass(Order.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(Order.class);
        job.setOutputValueClass(NullWritable.class);

        job.setGroupingComparatorClass(OrderGroupComparator.class);

        Path outputPath = new Path(args[1]);
        outputPath.getFileSystem(configuration).delete(outputPath, true);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }
}