package com.yakovenko.lab3.messaging;

import com.yakovenko.lab3.clients.dto.AggregateResponseDto;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

/**
 * Client for kafka
 */
@KafkaClient
public interface ResultProducer {

    @Topic("streaming-analytics")
    void sendResult(AggregateResponseDto result);

}
