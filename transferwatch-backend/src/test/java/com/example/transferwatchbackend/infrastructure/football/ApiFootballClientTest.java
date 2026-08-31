package com.example.transferwatchbackend.infrastructure.football;

import com.example.transferwatchbackend.infrastructure.football.api.ApiFootballResponse;
import com.example.transferwatchbackend.team.Team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ApiFootballClientTest {

    private MockRestServiceServer server;
    private ApiFootballClient client;

    @BeforeEach
    void setUp() {

        RestClient.Builder builder = RestClient.builder().baseUrl("https://football.test").defaultHeader("x-apisports-key","test-key");

        server = MockRestServiceServer
                        .bindTo(builder)
                        .build();

        client = new ApiFootballClient(builder.build());
    }

    @Test
    void ignoresIncompleteTeamEntries() {

        String responseBody = """
            {
              "response": [
                null,
                {
                  "team": null
                },
                {
                  "team": {
                    "id": null,
                    "name": "Unknown",
                    "logo": null
                  }
                },
                {
                  "team": {
                    "id": 42,
                    "name": "Arsenal",
                    "logo": "arsenal-logo"
                  }
                }
              ]
            }
            """;

        server.expect(
                        requestTo(
                                "https://football.test/teams"
                                        + "?search=arsenal"
                        )
                )
                .andRespond(
                        withSuccess(
                                responseBody,
                                MediaType.APPLICATION_JSON
                        )
                );

        List<Team> result =
                client.searchTeams("arsenal");

        assertThat(result)
                .containsExactly(
                        new Team(
                                42,
                                "Arsenal",
                                "arsenal-logo"
                        )
                );

        server.verify();
    }

    @Test
    void returnsEmptyListWhenTeamResponseIsNull() {

        String responseBody = """
            {
              "response": null
            }
            """;

        server.expect(
                        requestTo(
                                "https://football.test/teams"
                                        + "?search=unknown"
                        )
                )
                .andRespond(
                        withSuccess(
                                responseBody,
                                MediaType.APPLICATION_JSON
                        )
                );

        assertThat(
                client.searchTeams("unknown")
        ).isEmpty();

        server.verify();
    }

    @Test
    void requestsTransfersForTeam() {

        String responseBody = """
            {
              "response": []
            }
            """;

        server.expect(
                        requestTo(
                                "https://football.test/transfers"
                                        + "?team=42"
                        )
                )
                .andExpect(method(GET))
                .andExpect(
                        header(
                                "x-apisports-key",
                                "test-key"
                        )
                )
                .andRespond(
                        withSuccess(
                                responseBody,
                                MediaType.APPLICATION_JSON
                        )
                );

        ApiFootballResponse result =
                client.getTransfers(42);

        assertThat(result).isNotNull();
        assertThat(result.response()).isEmpty();

        server.verify();
    }

    @Test
    void searchesAndMapsTeams() {

        String responseBody = """
            {
              "response": [
                {
                  "team": {
                    "id": 42,
                    "name": "Arsenal",
                    "logo": "arsenal-logo"
                  }
                }
              ]
            }
            """;

        server.expect(
                        requestTo(
                                "https://football.test/teams"
                                        + "?search=arsenal"
                        )
                )
                .andExpect(method(GET))
                .andExpect(
                        header(
                                "x-apisports-key",
                                "test-key"
                        )
                )
                .andRespond(
                        withSuccess(
                                responseBody,
                                MediaType.APPLICATION_JSON
                        )
                );

        List<Team> result =
                client.searchTeams("arsenal");

        assertThat(result)
                .containsExactly(
                        new Team(
                                42,
                                "Arsenal",
                                "arsenal-logo"
                        )
                );

        server.verify();
    }

}
