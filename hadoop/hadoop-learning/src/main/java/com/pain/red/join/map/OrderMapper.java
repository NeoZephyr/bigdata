package com.pain.red.join.map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OrderMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

    private Map<String, String> productIdToName = new HashMap<>();
    private Text k = new Text();

    @Override
    protected void setup(Context context) throws IOException {
        URI[] cacheFiles = context.getCacheFiles();
        String path = cacheFiles[0].getPath();

        // 指定 utf-8 编码
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));

        String line;

        while (StringUtils.isNoneBlank(line = bufferedReader.readLine())) {
            String[] items = line.split("\\s+");
            productIdToName.put(items[0], items[1]);
        }

        IOUtils.closeStream(bufferedReader);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] items = line.split("\\s+");
        String orderId = items[0];
        String productId = items[1];
        int count = Integer.parseInt(items[2]);

        String productName = "NULL";

        if (productIdToName.get(productId) != null) {
            productName = productIdToName.get(productId);
        }

        String orderLine = String.format("%s\t%s\t%s", orderId, productName, count);
        k.set(orderLine);

        if (count >= 5) {
            context.getCounter("frequency", "true").increment(1);
        } else {
            context.getCounter("frequency", "false").increment(1);
        }

        context.write(k, NullWritable.get());
    }
}
