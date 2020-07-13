package com.pain.red.app.top;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class TopReducer extends Reducer<LongWritable, NullWritable, LongWritable, NullWritable> {

    @Override
    protected void reduce(LongWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

        Iterator<NullWritable> iterator = values.iterator();

        for (int i = 0; i < 10; ++i) {
            if (iterator.hasNext()) {
                iterator.next();
                context.write(key, NullWritable.get());
            }
        }
    }
}
