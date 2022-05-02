package com.yakovenko.lab3.services;

import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Aggregation class
 */
@Singleton
public class AggregationServiceImpl implements AggregationService {
    /**
     * @param responseDtoList
     * @return
     */
    @Override
    public AggregateResponseDto aggregate(List<StreamingCallbackMessage> responseDtoList) {
        Long count = (long) responseDtoList.size();
        List<String> urls = new ArrayList<>();
        Stream<StreamingCallbackMessage> responses = responseDtoList.stream();
        responses.forEach((a) -> urls.add(a.getEvent().getEventUrl()));
        AggregateResponseDto result = new AggregateResponseDto();
        result.setCount(count);
        result.setEventUrls(urls);
        return result;
    }
}
