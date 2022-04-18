FROM zoltannz/hadoop-ubuntu:2.8.1


ENV PATH="/usr/local/hadoop/bin:${PATH}"


RUN if [ ! -f sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz ]; then \
    wget http://archive.apache.org/dist/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz; \
    tar xvzf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz; \
    else \
    echo "Sqoop already exists, skipping..."; \
    fi

# Скачиваем драйвер PostgreSQL
RUN if [ ! -f postgresql-42.2.5.jar ]; then \
    wget --no-check-certificate https://jdbc.postgresql.org/download/postgresql-42.2.5.jar; \
    cp postgresql-42.2.5.jar sqoop-1.4.7.bin__hadoop-2.6.0/lib/; \
    else \
    echo "Postgresql driver already exists, skipping..."; \
    fi

ENV PATH="/sqoop-1.4.7.bin__hadoop-2.6.0/bin:${PATH}"

# Скачиваем Spark
RUN if [ ! -f spark-2.3.1-bin-hadoop2.7.tgz ]; then \
    wget https://archive.apache.org/dist/spark/spark-2.3.1/spark-2.3.1-bin-hadoop2.7.tgz; \
    tar xvzf spark-2.3.1-bin-hadoop2.7.tgz; \
    else \
    echo "Spark already exists, skipping..."; \
    fi


ENV SPARK_HOME=/spark-2.3.1-bin-hadoop2.7
ENV HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop

ENV PATH=$PATH:/spark-2.3.1-bin-hadoop2.7/bin

COPY ./start.sh /root/
RUN chmod +x /root/start.sh

# ENTRYPOINT [ "/root/start.sh", "com.yakovenko.lab2.SparkSQLApplication", "SparkSQLApplication", "lab2-1.0-SNAPSHOT-jar-with-dependencies.jar" ]