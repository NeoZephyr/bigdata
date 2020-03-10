package com.pain.red.group;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class OrderGroupComparator extends WritableComparator {

    OrderGroupComparator() {
        super(Order.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        Order orderA = (Order) a;
        Order orderB = (Order) b;

        return orderA.getOrderNo().compareTo(orderB.getOrderNo());
    }
}
