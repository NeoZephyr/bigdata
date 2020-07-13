package com.pain.red.app.index;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class CountMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

    private String filename;
    private Text k = new Text();
    private LongWritable v = new LongWritable(1);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        filename = fileSplit.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] items = line.split("\\s+");

        for (String item : items) {
            k.set(String.format("%s-%s", filename, item));
            context.write(k, v);
        }
    }
}
