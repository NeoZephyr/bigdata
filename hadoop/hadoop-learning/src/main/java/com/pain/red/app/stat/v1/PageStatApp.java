package com.pain.red.app.stat.v1;

import com.pain.red.common.LogParser;
import com.pain.red.common.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

public class PageStatApp {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException {
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide two args");
        }

        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFs", "hdfs://cdh:8020");
        Job job = Job.getInstance(configuration);

        job.setJarByClass(PageStatApp.class);

        job.setMapperClass(PageStatMapper.class);
        job.setReducerClass(PageStatReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileSystem fileSystem = FileSystem.get(new URI("hdfs://cdh:8020"), configuration, "vagrant");
        Path outputPath = new Path(args[1]);

        if (fileSystem.exists(outputPath)) {
            fileSystem.delete(outputPath, true);
        }

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputPath);

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }

    static class PageStatMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        private LogParser logParser;
        private LongWritable outputValue = new LongWritable(1);
        private Text outputKey = new Text();

        @Override
        protected void setup(Context context) {
            logParser = new LogParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> logInfo = logParser.parse(value.toString());
            String pageId = UrlUtils.getPageId(logInfo.get("url"));

            if (StringUtils.isBlank(pageId)) {
                outputKey.set("-");
            } else {
                outputKey.set(pageId);
            }

            context.write(outputKey, outputValue);
        }
    }

    static class PageStatReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        private LongWritable outputValue = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            Iterator<LongWritable> iterator = values.iterator();
            long count = 0;

            while (iterator.hasNext()) {
                count += iterator.next().get();
            }

            outputValue.set(count);
            context.write(key, outputValue);
        }
    }
}
