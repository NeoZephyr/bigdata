package com.pain.red.app.join.reduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class OrderReducer extends Reducer<Order, NullWritable, Order, NullWritable> {

    /**
     * Order{id=0, productId=1001, productName='苹果手机', count=0}
     * Order{id=10006, productId=1001, productName='', count=800}
     * Order{id=10000, productId=1001, productName='', count=100}
     * Order{id=0, productId=1002, productName='小米手机', count=0}
     * Order{id=10005, productId=1002, productName='', count=2000}
     * Order{id=10001, productId=1002, productName='', count=1000}
     * Order{id=0, productId=1003, productName='美短猫咪', count=0}
     * Order{id=10002, productId=1003, productName='', count=20}
     * Order{id=0, productId=1004, productName='英短猫咪', count=0}
     * Order{id=10003, productId=1004, productName='', count=15}
     * Order{id=0, productId=1005, productName='可口可乐', count=0}
     * Order{id=10004, productId=1005, productName='', count=100000}
     *
     * @param key
     * @param values
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
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
