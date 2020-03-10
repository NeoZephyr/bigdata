package com.pain.red.audit;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Bill implements Writable {
    private int income;
    private int expenses;
    private int savings;

    Bill() {}

    Bill(int income, int expenses) {
        this.income = income;
        this.expenses = expenses;
        this.savings = income - expenses;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpenses() {
        return expenses;
    }

    public void setExpenses(int expenses) {
        this.expenses = expenses;
    }

    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
    }

    public void updateSavings() {
        this.savings = this.income - this.expenses;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(income);
        out.writeInt(expenses);
        out.writeInt(savings);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.income = in.readInt();
        this.expenses = in.readInt();
        this.savings = in.readInt();
    }

    @Override
    public String toString() {
        return "Bill{" +
                "income=" + income +
                ", expenses=" + expenses +
                ", savings=" + savings +
                '}';
    }
}
