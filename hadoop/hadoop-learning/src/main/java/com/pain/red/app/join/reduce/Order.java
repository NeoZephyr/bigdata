package com.pain.red.app.join.reduce;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Order implements WritableComparable<Order> {
    private int id;
    private int productId;
    private String productName;
    private int count;

    public Order() {
    }

    public Order(int id, int productId, String productName, int count) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(id);
        out.writeInt(productId);
        out.writeUTF(productName);
        out.writeInt(count);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.productId = in.readInt();
        this.productName = in.readUTF();
        this.count = in.readInt();
    }

    /**
     * 先按照 productId 排序，productId 相同，则按照 productName 排序
     * @param o
     * @return
     */
    @Override
    public int compareTo(Order o) {
        int result = Integer.compare(productId, o.productId);

        if (result == 0) {
            result = o.productName.compareTo(productName);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", count=" + count +
                '}';
    }
}
