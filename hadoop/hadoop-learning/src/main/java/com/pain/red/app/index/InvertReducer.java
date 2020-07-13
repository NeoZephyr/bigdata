package com.pain.red.app.index;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class InvertReducer extends Reducer<Text, Text, Text, Text> {

    private Text v = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();

        for (Text value : values) {
            sb.append(value.toString()).append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());
        v.set(sb.toString());
        context.write(key, v);
    }
}
