package com.pain.red.app.stat.v1;

import com.pain.red.common.LogParser;
import com.pain.red.common.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ETLApp {

    @SuppressWarnings("DuplicatedCode")
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException {
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide two args");
        }

        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFs", "hdfs://cdh:8020");
        Job job = Job.getInstance(configuration);

        job.setJarByClass(ETLApp.class);

        job.setMapperClass(ETLMapper.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

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

    static class ETLMapper extends Mapper<LongWritable, Text, NullWritable, Text> {

        private LogParser logParser;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            this.logParser = new LogParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String log = value.toString();
            Map<String, String> logInfo = logParser.parse(log);

            String ip = logInfo.get("ip");
            String country = logInfo.get("country") == null ? "-" : logInfo.get("country");
            String province = logInfo.get("province")== null ? "-" : logInfo.get("province");
            String city = logInfo.get("city")== null ? "-" : logInfo.get("city");
            String url = logInfo.get("url");
            String sessionId = logInfo.get("sessionId");
            String time = logInfo.get("time");


            String pageId = UrlUtils.getPageId(url) == "" ? "-" : UrlUtils.getPageId(url);

            StringBuilder builder = new StringBuilder();
            builder.append(ip).append("\t");
            builder.append(country).append("\t");
            builder.append(province).append("\t");
            builder.append(city).append("\t");
            builder.append(url).append("\t");
            builder.append(time).append("\t");
            builder.append(pageId).append("\t");
            builder.append(sessionId);

            if (StringUtils.isNotBlank(pageId) && !pageId.equals("-")) {
                System.out.println("------" + pageId);
            }

            context.write(NullWritable.get(), new Text(builder.toString()));
        }
    }
}
