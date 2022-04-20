#!/bin/bash

sqoop import \
    --connect "jdbc:postgresql://db:5432/$POSTGRES_DB?ssl=false" \
    --username "$POSTGRES_USER" --password "$POSTGRES_PASSWORD" \
    --target-dir 'inputs' \
    --query "select array_to_string(val,',','*') from inputs where \$CONDITIONS" -m 1


spark-submit --class $1 \
    --master local --deploy-mode client \
    --executor-memory 1g --name factorial \
    --conf "spark.app.id=$2" \
    /root/target/$3 \
    user/root/inputs/ user/root/out/

echo "DONE! RESULT IS: "
hdfs dfs -cat /user/root/out/*.csv

# hdfs dfs -get /user/root/out/*.csv /root/output/result.csv