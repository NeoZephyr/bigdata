package com.pain.red.group;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class OrderReducer extends Reducer<Order, NullWritable, Order, NullWritable> {

    @Override
    protected void reduce(Order key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        // 将订单 id 相同的 kv 聚合成组，然后取第一个即是该订单中最贵商品
//        context.write(key, NullWritable.get());

        Iterator<NullWritable> iterator = values.iterator();

        for (int i = 0; i < 2; ++i) {
            if (iterator.hasNext()) {
                context.write(key, iterator.next());
            }
        }
    }
}
