package com.yakovenko.lab3.controllers;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.RequestAttribute;
import com.yakovenko.lab3.clients.FutureClient;
import com.yakovenko.lab3.messaging.ResultProducer;
import com.yakovenko.lab3.services.AggregationService;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Controller for initializing scheduled application
 */
@Controller
public class StartController {

    @Inject
    ResultProducer resultProducer;

    @Inject
    FutureClient futureClient;

    @Inject
    AggregationService aggregationService;

    @Post(value = "/start")
    @Consumes("application/x-www-form-urlencoded")
    public void send(@RequestAttribute("appId") String appId, @RequestAttribute("token") String accessToken) {
        try {
            futureClient.createConnection(appId, accessToken);
            Stream<String> stream = Files.lines(Paths.get("src/main/resources/vkTag.config"));
            stream.forEach(futureClient::addRule);
        } catch (ClientException | ApiException | IOException e) {
            System.err.println("Failed to initialize");
        }
    }

}
