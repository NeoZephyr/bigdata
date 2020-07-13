package com.pain.red.app.group;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Order implements WritableComparable<Order> {
    private String orderNo;
    private String productNo;
    private double price;

    Order() {}

    public Order(String orderNo, String productNo, double price) {
        this.orderNo = orderNo;
        this.productNo = productNo;
        this.price = price;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNo='" + orderNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public int compareTo(Order o) {
        int ret = orderNo.compareTo(o.orderNo);

        if (ret == 0) {
            ret = Double.compare(o.price, price);
        }

        return ret;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(orderNo);
        out.writeUTF(productNo);
        out.writeDouble(price);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.orderNo = in.readUTF();
        this.productNo = in.readUTF();
        this.price = in.readDouble();
    }
}
