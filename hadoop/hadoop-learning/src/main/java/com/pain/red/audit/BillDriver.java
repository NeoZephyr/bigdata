package com.pain.red.audit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;
import java.io.IOException;

public class BillDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        File file = new File(args[1]);

        if (file.exists()) {
            file.delete();
        }

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        job.setJarByClass(BillDriver.class);

        job.setMapperClass(BillMapper.class);
        job.setReducerClass(BillReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Bill.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Bill.class);

        job.setPartitionerClass(BillPartitioner.class);
        job.setNumReduceTasks(5);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }
}
