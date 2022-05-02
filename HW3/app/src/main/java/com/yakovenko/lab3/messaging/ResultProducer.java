package com.yakovenko.lab3.messaging;

import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;
import io.micronaut.scheduling.annotation.Scheduled;
import com.yakovenko.lab3.clients.FutureClient;
import com.yakovenko.lab3.services.AggregationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Singleton
public class ResultProducer {
    private static final Logger log = LogManager.getLogger("HTTP_APPENDER");
    private static final Logger log2 = LogManager.getLogger("ASYNC_JSON_FILE_APPENDER");
    @Inject
    FutureClient futureClient;

    @Inject
    AggregationService aggregationService;

    /**
     * Method to prepare data
     */
    @Scheduled(fixedDelay = "60s")
    public void getData() {
        try {
//            futureClient.reset();
            List<StreamingCallbackMessage> mainWeatherDtoList = futureClient.getStatistics();
            AggregateResponseDto aggregateData = aggregationService.aggregate(mainWeatherDtoList);
            produceResult(aggregateData);
        } catch (ExecutionException | InterruptedException ex) {
            System.err.println("Execution failed");
        }

    }

    /**
     * @param aggregateData Data to be sent to Apache Kafka
     */
    public void produceResult(AggregateResponseDto aggregateData) {
        log.info(futureClient.getJSON(aggregateData));
        log2.info(futureClient.getJSON(aggregateData));
        futureClient.reset();
    }
}
