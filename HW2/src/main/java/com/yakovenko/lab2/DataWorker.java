package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DataWorker extends ProcessWorker {

    public DataWorker(Dataset<Row> inputDataset) {
        dataset = inputDataset;
        prepare();
    }

    protected void prepare() {
        dataset = dataset.withColumnRenamed("_c0", "key_id");
        dataset = dataset.withColumnRenamed("_c1", "value");
        dataset = dataset.withColumn("value", dataset.col("value").cast("Long"));
    }

    public void process() {
        result = dataset.groupBy(dataset.col("key_id")).sum();
    }

    public void save(String path) {
        result.toDF(result.columns())
        .write()
        .option("header", false)
        .csv(path);
    }

    public void show() {
        System.out.println("Data intensive:");
        super.show();
    }
}
