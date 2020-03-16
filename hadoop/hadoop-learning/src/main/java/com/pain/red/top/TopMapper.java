package com.pain.red.top;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class TopMapper extends Mapper<LongWritable, Text, LongWritable, NullWritable> {

    private LongWritable k = new LongWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        long num = Long.parseLong(value.toString());
        k.set(num);
        context.write(k, NullWritable.get());
    }
}
