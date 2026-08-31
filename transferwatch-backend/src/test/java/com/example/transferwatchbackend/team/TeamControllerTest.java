package com.example.transferwatchbackend.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeamControllerTest {

    private TeamSearchService service;
    private TeamController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        service = mock(TeamSearchService.class);

        controller = new TeamController(service);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void returnsTeamsFromSearchService() {

        List<Team> expected =
                List.of(new Team(42, "Arsenal", "logo"));

        when(service.search("arsenal"))
                .thenReturn(expected);

        assertThat(controller.searchTeams("arsenal"))
                .isEqualTo(expected);

        verify(service).search("arsenal");
    }

    @Test
    void missingQueryReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/teams")
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    @Test
    void emptySearchResultReturnsEmptyJsonArray()
            throws Exception {

        when(service.search("unknown"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/teams")
                                .queryParam(
                                        "query",
                                        "unknown"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$")
                                .isEmpty()
                );

        verify(service)
                .search("unknown");
    }


    @Test
    void exposesTeamSearchEndpoint()
            throws Exception {

        when(service.search("arsenal"))
                .thenReturn(
                        List.of(
                                new Team(
                                        42,
                                        "Arsenal",
                                        "logo"
                                )
                        )
                );

        mockMvc.perform(
                        get("/api/teams")
                                .queryParam(
                                        "query",
                                        "arsenal"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(42)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Arsenal")
                )
                .andExpect(
                        jsonPath("$[0].logo")
                                .value("logo")
                );

        verify(service)
                .search("arsenal");
    }




}
