package com.yakovenko.lab3.clients;


import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.streaming.responses.GetServerUrlResponse;
import com.vk.api.sdk.streaming.clients.StreamingEventHandler;
import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import com.vk.api.sdk.streaming.exceptions.StreamingApiException;
import com.vk.api.sdk.streaming.exceptions.StreamingClientException;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import com.vk.api.sdk.streaming.objects.responses.StreamingResponse;
import com.yakovenko.lab3.clients.dto.AggregateResponseDto;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Client for connecting to weather open api
 */
@Singleton
public class FutureClient {
    private static int i = 0;
    private VkApiClient vkClient;
    private VkStreamingApiClient streamingClient;
    private List<StreamingCallbackMessage> results = new ArrayList<>();
    private StreamingActor streamingActor;
    private final Gson gParser = new Gson();

    public FutureClient() {
        TransportClient transportClient = new HttpTransportClient();

        vkClient = new VkApiClient(transportClient);
        streamingClient = new VkStreamingApiClient(transportClient);
    }

    /**
     * @param accessToken Access token from api.openweathermap.org
     *              Method to initialize which cities to get data about
     */
    public void createConnection(String appId, String accessToken) throws ClientException, ApiException {
        ServiceActor actor = new ServiceActor(Integer.getInteger(appId), accessToken);
        GetServerUrlResponse getServerUrlResponse = vkClient.streaming().getServerUrl(actor).execute();
        streamingActor = new StreamingActor(getServerUrlResponse.getEndpoint(), getServerUrlResponse.getKey());
    }
    
    public void addRule(String value) {
        i += 1;
        String tag = Integer.toString(i);
        try {
            StreamingResponse response = streamingClient.rules().add(streamingActor, tag, value).execute();
        } catch ( StreamingClientException | StreamingApiException ignored) {
        }
    }
    /**
     * @return Method to get information about the cities initialized in createUris(String token) method
     */
    public List<StreamingCallbackMessage> getStatistics() throws ExecutionException, InterruptedException {
        streamingClient.stream().get(streamingActor, new StreamingEventHandler() {
                @Override
                public void handle(StreamingCallbackMessage message) {
//                    ResponseDto object = gParser.fromJson(message.toString(), ResponseDto.class);
                    results.add(message);
                }
            }).execute();
        return results;
    }

    public String getJSON(AggregateResponseDto data) {
        return gParser.toJson(data, AggregateResponseDto.class);
    }

    public void reset(){
        results = new ArrayList<>();
    }
}
