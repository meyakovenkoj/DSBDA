package com.yakovenko.lab2;

import java.util.Arrays;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Abstract class for worker
 */
public abstract class ProcessWorker {
    protected Dataset<Row> dataset;
    protected Dataset<Row> result;

    /**
     * Prepare dataset. For example some casts
     */
    abstract protected void prepare();

    /**
     * Main process
     */
    abstract public void process();

    /**
     * Save result to csv
     * @param path path to save
     */
    abstract public void save(String path);

    /**
     * Get result dataset
     * @return result Dataset<Row>
     */
    abstract public Dataset<Row> get();

    /**
     * Show dataset and result
     */
    public void show() {
        if (dataset != null){
            dataset.schema();
            dataset.show();
        }
        if (result != null) {
            result.show();
        }
    }

    /**
     * Check if column exists in dataset
     * @param column name of column
     * @return true if column exists
     */
    public boolean checkColumn(String column) {
        if (Arrays.asList(dataset.columns()).contains(column)) {
            return true;
        }
        System.err.println(String.format("ERROR: dataset no %s column", column));
        return false;
    }
}
