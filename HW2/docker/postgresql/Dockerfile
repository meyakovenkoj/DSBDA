# syntax=docker/dockerfile:1
FROM postgres:10.5

ARG POSTGRES_DB=postgres_db

ADD ./scripts/generator.py /root/generator.py

RUN apt update && apt install python3
RUN env
RUN cd /root/ && python3 /root/generator.py -c 1000 -max 100 -db $POSTGRES_DB
RUN cp /root/dump.sql /docker-entrypoint-initdb.d/dump.sql

# CMD tail -f /dev/null