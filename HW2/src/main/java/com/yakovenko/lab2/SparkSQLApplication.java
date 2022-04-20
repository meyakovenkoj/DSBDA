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
        if (args.length < 2) {
            throw new RuntimeException("Usage: java -jar SparkSQLApplication.jar input.file outputDirectory");
        }
        String inputDir = args[0];
        String outputDir = args[1];
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
        Dataset<Row> df = sc.read().csv(hdfsURL + inputDir);
        log.info("===============COUNTING...================");
        final long startTime = System.currentTimeMillis();
        log.info("========== Print Schema ============");
        df.printSchema();
        for (String col : df.columns()) {
            df = df.withColumn(
                col,
                df.col(col).cast("Long")
            );
        }

        log.info("========== Print Data ==============");
        df.printSchema();
        df.show();
        Dataset<Row> result = ProcessCounter.process(df);
        result.show();
        final long endTime = System.currentTimeMillis();
        log.info("============SAVING FILE TO " + hdfsURL + outputDir + " directory============");
        log.info("Total execution time: " + (endTime - startTime));
        result.toDF(result.columns())
                .write()
                .option("header", true)
                .csv(hdfsURL + outputDir);
    }
}
