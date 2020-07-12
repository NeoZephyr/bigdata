package com.pain.red.bill;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;

public class BillMapper extends Mapper<LongWritable, Text, Text, Bill> {

    private Bill bill = new Bill();
    private Text name = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] items = line.split("\\s+");

        System.out.println(Arrays.toString(items));

        bill.setName(items[0]);
        bill.setIncome(Integer.parseInt(items[1]));
        bill.setExpenses(Integer.parseInt(items[2]));
        bill.updateSavings();
        name.set(items[0]);

        context.write(name, bill);
    }
}
