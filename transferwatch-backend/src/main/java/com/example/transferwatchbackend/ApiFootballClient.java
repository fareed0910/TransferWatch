package com.example.transferwatchbackend;

import com.example.transferwatchbackend.api.ApiFootballResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApiFootballClient implements TransferProvider{

    private final RestClient restClient;

    public ApiFootballClient(
            @Value("${api.football.base-url}") String baseUrl,
            @Value("${api.football.key}") String apiKey
    ) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(
                        "x-apisports-key",
                        apiKey
                )
                .build();
    }

    @Override
    public ApiFootballResponse getTransfers(int teamId) {

        return restClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/transfers")
                                .queryParam("team", teamId)
                                .build()
                )
                .retrieve()
                .body(ApiFootballResponse.class);
    }
}