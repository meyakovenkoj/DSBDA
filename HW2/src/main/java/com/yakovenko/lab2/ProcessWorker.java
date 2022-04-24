package com.yakovenko.lab2;

import java.util.Arrays;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public abstract class ProcessWorker {
    protected Dataset<Row> dataset;
    protected Dataset<Row> result;

    abstract protected void prepare();
    abstract public void process();
    abstract public void save(String path);
    abstract public Dataset<Row> get();
    public void show() {
        if (dataset != null){
            dataset.schema();
            dataset.show();
        }
        if (result != null) {
            result.show();
        }
    }
    public boolean checkColumn(String column) {
        if (Arrays.asList(dataset.columns()).contains(column)) {
            return true;
        }
        System.err.println(String.format("ERROR: dataset no %s column", column));
        return false;
    }
}
