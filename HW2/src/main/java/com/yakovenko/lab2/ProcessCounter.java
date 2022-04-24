package com.yakovenko.lab2;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class ProcessCounter {
    public static Dataset<Row> process(Dataset<Row> inputDataset, ActionType action) {
        Dataset<Row> result = inputDataset;
        switch (action) {
            case COMPUTE_INTENSIVE:
                result = result.withColumn(
                        "value",
                        factorial(result.col("value")));
                break;
            case DATA_INTENSIVE:
                result = result.groupBy(result.col("key_id")).sum();
                break;
            default:
                break;
        }

        result.show();

        return result;
    }
}
