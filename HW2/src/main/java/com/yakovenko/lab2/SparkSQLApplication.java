package com.yakovenko.lab2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Main Spark application
 */
public class SparkSQLApplication {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SparkSQLApplication.class);

    /**
     * Entry point. Accepts 3 arguments
     * @param args inputCompute inputData outputDirectory
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            throw new RuntimeException("Usage: java -jar SparkSQLApplication.jar inputCompute inputData outputDirectory");
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

        ComputeWorker cWorker = new ComputeWorker(dfCompute);
        DataWorker dWorker = new DataWorker(dfData);

        log.info("===============COUNTING...================");
        final long startTime = System.currentTimeMillis();
        log.info("========== Print Schema and Data ============");
        cWorker.show();
        dWorker.show();
        log.info("========== Process Data ==============");
        cWorker.process();
        dWorker.process();
        final long endTime = System.currentTimeMillis();
        log.info("============SAVING FILE TO " + hdfsURL + outputDir + " directory============");
        log.info(String.format("===Total execution time: %d ms ===", (endTime - startTime)));
        cWorker.save(hdfsURL + outputDir + "/data");
        dWorker.save(hdfsURL + outputDir + "/compute");
    }
}
