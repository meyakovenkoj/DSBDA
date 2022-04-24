package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Data intensive worker. Sum all elements by key_id
 */
public class DataWorker extends ProcessWorker {

    /**
     * Constructor, calls prepare for inputDataset
     * @param inputDataset dataset for work
     */
    public DataWorker(Dataset<Row> inputDataset) {
        dataset = inputDataset;
        prepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepare() {
        if (dataset == null) {
            System.err.println("ERROR: dataset is null");
            return;
        }
        if (!checkColumn("_c0") || !checkColumn("_c1")) {
            return;
        }
        dataset = dataset.withColumnRenamed("_c0", "key_id");
        dataset = dataset.withColumnRenamed("_c1", "value");
        dataset = dataset.withColumn("value", dataset.col("value").cast("Long"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process() {
        if (dataset == null) {
            System.err.println("ERROR: dataset is null");
            return;
        }
        if(!checkColumn("key_id")){
            return;
        }
        result = dataset.groupBy(dataset.col("key_id")).sum();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        System.out.println("Data intensive:");
        super.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dataset<Row> get() {
        return result;
    }
}
