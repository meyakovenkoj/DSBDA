package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class ComputeWorker extends ProcessWorker {

    public ComputeWorker(Dataset<Row> inputDataset) {
        dataset = inputDataset;
        prepare();
    }
   
    @Override
    protected void prepare() {
        if (dataset == null) {
            System.err.println("ERROR: dataset is null");
            return;
        }
        dataset = dataset.withColumnRenamed("_c0", "value");
        dataset = dataset.withColumn("value", dataset.col("value").cast("Long"));
    }

    @Override
    public void process() {
        if (dataset == null) {
            System.err.println("ERROR: dataset is null");
            return;
        }
        result = dataset.withColumn(
                        "value",
                        factorial(dataset.col("value")));
    }

    @Override
    public void save(String path) {
        if (result == null) {
            System.err.println("ERROR: result is null");
            return;
        }
        result.toDF(result.columns())
        .write()
        .option("header", false)
        .csv(path);
    }

    @Override
    public void show() {
        System.out.println("Compute intensive:");
        super.show();
    }

    @Override
    public Dataset<Row> get() {
        return result;
    }
}