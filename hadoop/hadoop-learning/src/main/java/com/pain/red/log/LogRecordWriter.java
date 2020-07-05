package com.pain.red.log;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class LogRecordWriter extends RecordWriter<LongWritable, Text> {

    FSDataOutputStream hadoopStream;
    FSDataOutputStream sparkStream;

    public void init(TaskAttemptContext job) throws IOException {
        String outputDir = job.getConfiguration().get(FileOutputFormat.OUTDIR);
        FileSystem fileSystem = FileSystem.get(job.getConfiguration());
        hadoopStream = fileSystem.create(new Path(String.format("%s/hadoop", outputDir)));
        sparkStream = fileSystem.create(new Path(String.format("%s/spark", outputDir)));
    }

    @Override
    public void write(LongWritable key, Text value) throws IOException, InterruptedException {
        String line = value.toString() + "\n";

        if (line.contains("hadoop")) {
            hadoopStream.write(line.getBytes());
        } else {
            sparkStream.write(line.getBytes());
        }
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        IOUtils.closeStream(hadoopStream);
        IOUtils.closeStream(sparkStream);
    }
}
