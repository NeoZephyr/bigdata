package com.pain.red.join.reduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class OrderReducer extends Reducer<Order, NullWritable, Order, NullWritable> {

    @Override
    protected void reduce(Order key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<NullWritable> iterator = values.iterator();
        iterator.next();
        String productName = key.getProductName();

        while (iterator.hasNext()) {
            iterator.next();
            key.setProductName(productName);
            context.write(key, NullWritable.get());
        }
    }
}
