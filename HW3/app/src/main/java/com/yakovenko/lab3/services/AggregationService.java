package com.yakovenko.lab3.services;

import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;

import java.util.List;

public interface AggregationService {
    AggregateResponseDto aggregate(List<StreamingCallbackMessage> weatherDtoList);
}
