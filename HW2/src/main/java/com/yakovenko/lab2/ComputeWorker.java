package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class ComputeWorker extends ProcessWorker {

    public ComputeWorker(Dataset<Row> inputDataset) {
        dataset = inputDataset;
        prepare();
    }
    
    protected void prepare() {
        dataset = dataset.withColumnRenamed("_c0", "value");
        dataset = dataset.withColumn("value", dataset.col("value").cast("Long"));
    }

    public void process() {
        result = dataset.withColumn(
                        "value",
                        factorial(dataset.col("value")));
    }

    public void save(String path) {
        result.toDF(result.columns())
        .write()
        .option("header", false)
        .csv(path);
    }

    public void show() {
        System.out.println("Compute intensive:");
        super.show();
    }
}