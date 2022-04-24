package com.yakovenko.lab2;

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
}
