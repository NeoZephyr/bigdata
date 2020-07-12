package com.pain.red.bill;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class BillPartitioner extends Partitioner<Text, Bill> {
    @Override
    public int getPartition(Text text, Bill bill, int numPartitions) {
        return (text.hashCode() & Integer.MAX_VALUE) % numPartitions;
    }
}
