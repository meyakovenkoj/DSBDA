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
    user/root/inputs/ out

echo "DONE! RESULT IS: "
hdfs fs -cat  /user/root/out/part-00000

hdfs fs -get /user/root/out/part-00000 /root/output/part-00000