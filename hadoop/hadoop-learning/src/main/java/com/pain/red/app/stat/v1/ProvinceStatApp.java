package com.pain.red.app.stat.v1;

import com.pain.red.common.IPParser;
import com.pain.red.common.LogParser;
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

public class ProvinceStatApp {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide two args");
        }

        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFs", "hdfs://cdh:8020");
        Job job = Job.getInstance(configuration);

        job.setJarByClass(ProvinceStatApp.class);

        job.setMapperClass(ProvinceStatMapper.class);
        job.setReducerClass(ProvinceStatReducer.class);

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

    static class ProvinceStatMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        private LogParser logParser;
        private Text outputKey = new Text();
        private LongWritable outputValue = new LongWritable(1);

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            logParser = new LogParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> logInfo = logParser.parse(value.toString());
            String ip = logInfo.get("ip");

            if (StringUtils.isBlank(ip)) {
                outputKey.set("-");
            } else {
                IPParser.RegionInfo regionInfo = IPParser.getInstance().analyseIp(ip);
                String province = regionInfo.getProvince();

                if (StringUtils.isBlank(province)) {
                    outputKey.set("-");
                } else {
                    outputKey.set(province);
                }
            }

            context.write(outputKey, outputValue);
        }
    }

    static class ProvinceStatReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

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
