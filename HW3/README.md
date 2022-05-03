# Lab 3

[![Aggregation Service](https://github.com/meyakovenkoj/DSBDA/actions/workflows/stream-api.yml/badge.svg)](https://github.com/meyakovenkoj/DSBDA/actions/workflows/stream-api.yml)

## Task

Для любого доступного публичного stream API (например, https://www.quora.com/Where-can-I-find-public-or-free-real-time-or-streaming-data-sources ) написать программу, которая вычисляет количество некоторых событий (метрик, сообщений и т.д) поминутно и отправляет агрегированные данные в Elasticsearch (count/timeline graph).

## Deploy

Ubuntu Dockerfile с bash-скриптами настройки окружения

## Delivery to Elasticsearch

log4j async or any http appender

## Visualization

Grafana

## Usage

Set app ID and Service token in env:

```
export APPID=xxx
export TOKEN=xxx
```

Start getting messages from VK Streaming API:

```bash
curl --location --request POST 'http://localhost:8080/start' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode "appId=$APPID" \
--data-urlencode "token=$TOKEN"
```
