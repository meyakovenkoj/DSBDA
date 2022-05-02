package com.yakovenko.lab3;

import com.google.gson.Gson;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;
import com.yakovenko.lab3.services.AggregationService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@MicronautTest
public class AggregationServiceImplTest {
    private final String message = "{\n" +
            "  \"code\": 100,\n" +
            "  \"event\": {\n" +
            "    \"event_type\": \"post\",\n" +
            "    \"event_id\": {\n" +
            "      \"post_owner_id\": 123456,\n" +
            "      \"post_id\": 54321\n" +
            "    },\n" +
            "    \"event_url\": \"https://vk.com/wall123456_54321\",\n" +
            "    \"text\": \"This is event text\",\n" +
            "    \"creation_time\": 1498569445,\n" +
            "    \"tags\": [\n" +
            "      \"2\"\n" +
            "    ],\n" +
            "    \"author\": {\n" +
            "      \"id\": 123456,\n" +
            "      \"platform\": 4\n" +
            "    }\n" +
            "  }\n" +
            "}";
    @Inject
    AggregationService aggregationService;

    @Test
    public void shouldAggregateData() throws IOException {
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
