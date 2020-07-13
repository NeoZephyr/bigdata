package com.pain.red.app.sequence;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class FullFileRecordReader extends RecordReader<Text, BytesWritable> {

    boolean end = false;
    FileSplit fileSplit;
    FSDataInputStream fsDataInputStream;

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        fileSplit = (FileSplit) split;
        Path path = fileSplit.getPath();
        FileSystem fileSystem = path.getFileSystem(context.getConfiguration());
        fsDataInputStream = fileSystem.open(path);
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return !end;
    }

    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return new Text(fileSplit.getPath().toString());
    }

    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        long length = fileSplit.getLength();
        byte[] buf = new byte[(int) length];

        IOUtils.readFully(fsDataInputStream, buf, 0, (int) length);
        end = true;
        return new BytesWritable(buf);
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return (end ? 1 : 0);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeStream(fsDataInputStream);
    }
}
