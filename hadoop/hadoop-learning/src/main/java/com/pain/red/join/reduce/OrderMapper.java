package com.pain.red.join.reduce;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class OrderMapper extends Mapper<LongWritable, Text, Order, NullWritable> {

    private Order order = new Order();
    private String filename;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        filename = fileSplit.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] items = line.split("\\s+");

        if (StringUtils.equals(filename, "order.md")) {
            order.setId(Integer.parseInt(items[0]));
            order.setProductId(Integer.parseInt(items[1]));
            order.setProductName("");
            order.setCount(Integer.parseInt(items[2]));
        } else {
            order.setId(0);
            order.setProductId(Integer.parseInt(items[0]));
            order.setProductName(items[1]);
            order.setCount(0);
        }

        context.write(order, NullWritable.get());
    }
}
