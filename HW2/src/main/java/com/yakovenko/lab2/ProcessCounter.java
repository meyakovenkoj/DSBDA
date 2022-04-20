package com.yakovenko.lab2;

import java.math.BigInteger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

public class ProcessCounter {
    public static Dataset<Row> process(Dataset<Row> inputDataset) {
        Dataset<Row> result = inputDataset;
        for (String col : result.columns()) {
            result = result.withColumn(
                col,
                factorial(result.col(col))
            );
        }

        result.show();
        
        result = result.groupBy().sum();
        return result;
    }
}
