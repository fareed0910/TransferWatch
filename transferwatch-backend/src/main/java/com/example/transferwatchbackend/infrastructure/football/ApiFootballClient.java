package com.example.transferwatchbackend.infrastructure.football;

import com.example.transferwatchbackend.infrastructure.football.api.ApiFootballResponse;

import com.example.transferwatchbackend.infrastructure.football.api.ApiTeamSearchResponse;
import com.example.transferwatchbackend.team.Team;
import com.example.transferwatchbackend.team.TeamProvider;
import com.example.transferwatchbackend.transfer.TransferProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ApiFootballClient implements TransferProvider, TeamProvider {

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
    public List<Team> searchTeams(String query) {

        ApiTeamSearchResponse response =
                restClient
                        .get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/teams")
                                        .queryParam("search", query)
                                        .build()
                        )
                        .retrieve()
                        .body(ApiTeamSearchResponse.class);

        if (response == null || response.response() == null) {
            return List.of();
        }

        return response.response()
                .stream()
                .filter(entry ->
                        entry != null
                                && entry.team() != null
                                && entry.team().id() != null
                                && entry.team().name() != null
                )
                .map(entry ->
                        new Team(
                                entry.team().id(),
                                entry.team().name(),
                                entry.team().logo()
                        )
                )
                .toList();
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