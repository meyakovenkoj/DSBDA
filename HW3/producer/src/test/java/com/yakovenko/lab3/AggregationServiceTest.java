package com.yakovenko.lab3;

import com.google.gson.Gson;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;
import com.yakovenko.lab3.services.AggregationService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple test for aggregation
 */
@MicronautTest
public class AggregationServiceTest {
    private final String message = """
            {
              "code": 100,
              "event": {
                "event_type": "post",
                "event_id": {
                  "post_owner_id": 123456,
                  "post_id": 54321
                },
                "event_url": "https://vk.com/wall123456_54321",
                "text": "This is event text",
                "creation_time": 1498569445,
                "tags": [
                  "2"
                ],
                "author": {
                  "id": 123456,
                  "platform": 4
                }
              }
            }""";

    @Inject
    AggregationService aggregationService;

    /**
     * test aggregation of 2 events
     */
    @Test
    public void shouldAggregateData() {
        List<StreamingCallbackMessage> responseDtoList = new ArrayList<>();
        Gson gParser = new Gson();
        StreamingCallbackMessage objectOne = gParser.fromJson(message, StreamingCallbackMessage.class);
        StreamingCallbackMessage objectTwo = gParser.fromJson(message, StreamingCallbackMessage.class);
        responseDtoList.add(objectOne);
        responseDtoList.add(objectTwo);
        AggregateResponseDto result = aggregationService.aggregate(responseDtoList);
        Assertions.assertEquals(result.getCount(), 2L);
    }
}
