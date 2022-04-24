package com.yakovenko.lab2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkSQLApplication {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SparkSQLApplication.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new RuntimeException("Usage: java -jar SparkSQLApplication.jar input_compute input_data outputDirectory");
        }
        String inputDirCompute = args[0];
        String inputDirData = args[1];
        String outputDir = args[2];
        log.info("Appliction started!");
        log.debug("Application started");
        SparkSession sc = SparkSession
                .builder()
                .master("local")
                .appName("SparkSQLApplication")
                .getOrCreate();
        FileSystem fileSystem = null;
        String hdfsURL = "hdfs://127.0.0.1:9000/";
        try {
            fileSystem = FileSystem.get(new URI(hdfsURL), sc.sparkContext().hadoopConfiguration());

            Path outputDirectory = new Path(hdfsURL + outputDir);
            if (fileSystem.exists(outputDirectory)) {
                log.info("=== Deleting output directory ===");
                fileSystem.delete(outputDirectory, true);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        Dataset<Row> dfCompute = sc.read().csv(hdfsURL + inputDirCompute);
        Dataset<Row> dfData = sc.read().csv(hdfsURL + inputDirData);
        log.info("===============COUNTING...================");
        final long startTime = System.currentTimeMillis();
        log.info("========== Print Schema ============");
        dfCompute = dfCompute.withColumnRenamed("_c0", "value");
        dfCompute = dfCompute.withColumn("value", dfCompute.col("value").cast("Long"));
        dfCompute.printSchema();
        dfCompute.show();
        dfData = dfData.withColumnRenamed("_c0", "key_id");
        dfData = dfData.withColumnRenamed("_c1", "value");
        dfData = dfData.withColumn("value", dfData.col("value").cast("Long"));
        dfData.printSchema();
        dfData.show();
        log.info("========== Print Data ==============");
        Dataset<Row> resultData = ProcessCounter.process(dfData, ActionType.DATA_INTENSIVE);
        Dataset<Row> resultCompute = ProcessCounter.process(dfCompute, ActionType.COMPUTE_INTENSIVE);
        final long endTime = System.currentTimeMillis();
        log.info("============SAVING FILE TO " + hdfsURL + outputDir + " directory============");
        log.info("Total execution time: " + (endTime - startTime));
        resultData.toDF(resultData.columns())
                .write()
                .option("header", false)
                .csv(hdfsURL + outputDir + "/data");
        resultCompute.toDF(resultCompute.columns())
                .write()
                .option("header", false)
                .csv(hdfsURL + outputDir + "/compute");
    }
}
