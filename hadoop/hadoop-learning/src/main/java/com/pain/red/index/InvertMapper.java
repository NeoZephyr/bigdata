package com.pain.red.index;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class InvertMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Text k = new Text();
    private Text v = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
//        String[] items = line.replace("\t", " ").split("\\s+");
        String[] items = line.split("\t");
        String[] subItems = items[0].split("-");

        k.set(subItems[1]);
        v.set(String.format("%s: %s", subItems[0], items[1]));
        context.write(k, v);
    }
}
