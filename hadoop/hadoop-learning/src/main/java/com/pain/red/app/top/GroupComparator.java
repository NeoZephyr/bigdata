package com.pain.red.app.top;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupComparator extends WritableComparator {

    GroupComparator() {
        super(LongWritable.class, true);
    }

    /**
     * 所有数据分到同一个组里面
     * @param a
     * @param b
     * @return
     */
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        return 0;
    }
}