#!/bin/bash

sqoop import \
    --connect "jdbc:postgresql://db:5432/$POSTGRES_DB?ssl=false" \
    --username "$POSTGRES_USER" --password "$POSTGRES_PASSWORD" \
    --target-dir 'compute_intensive' \
    --query "select val from compute_intensive where \$CONDITIONS" -m 1

sqoop import \
    --connect "jdbc:postgresql://db:5432/$POSTGRES_DB?ssl=false" \
    --username "$POSTGRES_USER" --password "$POSTGRES_PASSWORD" \
    --target-dir 'data_intensive' \
    --query "select key_id, val from data_intensive where \$CONDITIONS" -m 1


spark-submit --class $1 \
    --master local --deploy-mode client \
    --executor-memory 1g --name factorial \
    --conf "spark.app.id=$2" \
    /root/target/$3 \
    user/root/compute_intensive/ user/root/data_intensive/ user/root/out/

echo "DONE! RESULT IS: "
# hdfs dfs -cat /user/root/out/*.csv

# hdfs dfs -get /user/root/out/*.csv /root/output/result.csv