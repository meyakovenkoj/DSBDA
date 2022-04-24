package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

/**
 * Compute intensive worker. Computes factorial for each of dataset[0] elements
 */
public class ComputeWorker extends ProcessWorker {

    /**
     * Constructor, calls prepare for inputDataset
     * @param inputDataset dataset for work
     */
    public ComputeWorker(Dataset<Row> inputDataset) {
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
        if(!checkColumn("_c0")) {
            return;
        }
        dataset = dataset.withColumnRenamed("_c0", "value");
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
        if(!checkColumn("value")) {
            return;
        }
        result = dataset.withColumn(
                        "value",
                        factorial(dataset.col("value")));
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
        System.out.println("Compute intensive:");
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