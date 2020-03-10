package com.pain.red.group;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class OrderMapper extends Mapper<LongWritable, Text, Order, NullWritable> {

    private Order order = new Order();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] items = line.split("\\s+");

        order.setOrderNo(items[0]);
        order.setProductNo(items[1]);
        order.setPrice(Double.parseDouble(items[2]));

        context.write(order, NullWritable.get());
    }
}
