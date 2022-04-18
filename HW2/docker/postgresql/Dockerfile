# syntax=docker/dockerfile:1
FROM postgres:10.5

ARG POSTGRES_DB=postgres_db

ADD postgresql/scripts/generator.py /root/generator.py

RUN apt update && apt install python3
RUN env
RUN cd /root/ && python3 /root/generator.py -c 10 -max 10 -len 10 -db $POSTGRES_DB
RUN cp /root/dump.sql /docker-entrypoint-initdb.d/dump.sql