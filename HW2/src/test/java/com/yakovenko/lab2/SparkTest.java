package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class SparkTest {
    private SparkSession sc = SparkSession
    .builder()
    .master("local")
    .appName("SparkSQLApplication")
    .getOrCreate();
    private final String dataRes = "test-data.csv";
    private final String computeRes = "test-compute.csv";

    private final String dataValid = "valid-data.csv";
    private final String computeValid = "valid-compute.csv";

    private Dataset<Row> testData;
    private Dataset<Row> testCompute;

    @Test
    public void testDataDataset() {
        testData = sc.read().csv(getClass().getResource(dataRes).toString());
        DataWorker dWorker = new DataWorker(testData);
        dWorker.process();

        Dataset<Row> result = dWorker.get();
        result.show();

        Dataset<Row> trueResult = sc.read()
                .format("csv")
                .option("header", true)
                .load(getClass().getResource(dataValid).toString());
        trueResult = trueResult.withColumn("sum(value)", trueResult.col("sum(value)").cast("Long"));
        compare(trueResult, result);
    }

    @Test
    public void testComputeDataset() {
        testCompute = sc.read().csv(getClass().getResource(computeRes).toString());
        ComputeWorker cWorker = new ComputeWorker(testCompute);
        cWorker.process();

        Dataset<Row> result = cWorker.get();
        result.show();

        Dataset<Row> trueResult = sc.read()
                .format("csv")
                .option("header", true)
                .load(getClass().getResource(computeValid).toString());

        compare(trueResult, result);
    }

    @Test
    public void testEmptyDataSet() {
        Dataset<Row> empty = sc.emptyDataFrame();
        ComputeWorker cWorker = new ComputeWorker(empty);
        cWorker.process();

        Dataset<Row> resultCompute = cWorker.get();
        assertNull(resultCompute);

        DataWorker dWorker = new DataWorker(empty);
        dWorker.process();

        Dataset<Row> resultData = cWorker.get();
        assertNull(resultData);

    }

    private void compare(Dataset<Row> trueDataset, Dataset<Row> resultDataset) {
        Dataset<Row> df = trueDataset.except(resultDataset);
        assertEquals(df.count(), 0);
    }
}
