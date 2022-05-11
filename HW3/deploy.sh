#!/bin/bash

set -e

cd producer
mvn package -f pom.xml
cd ..

cd consumer
mvn package -f pom.xml
cd ..

cd docker
sudo docker-compose up -d --build
cd ..

cat << EOF
Run:
- VK client with command:
        cd producer && ./mvnw mn:run

- Kafka client with command:
        java -cp consumer/target/lab3-1.0-SNAPSHOT-jar-with-dependencies.jar \\
        com.yakovenko.lab3.ConsumerLogger streaming-analytics

- Send token with:
        curl --location --request POST 'http://localhost:8080/start' \\
        --header 'Content-Type: application/x-www-form-urlencoded' \\
        --data-urlencode "appId=$APPID" \\
        --data-urlencode "token=$TOKEN"

- Open browser on http://localhost:3000
EOF

