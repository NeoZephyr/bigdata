package com.pain.red.audit;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class BillReducer extends Reducer<Text, Bill, Text, Bill> {

    private Bill bill = new Bill();

    @Override
    protected void reduce(Text key, Iterable<Bill> values, Context context) throws IOException, InterruptedException {

        int incomeSum = 0;
        int expensesSum = 0;

        for (Bill bill : values) {
            incomeSum += bill.getIncome();
            expensesSum += bill.getExpenses();
        }

        bill.setIncome(incomeSum);
        bill.setExpenses(expensesSum);
        bill.updateSavings();

        context.write(key, bill);
    }
}
