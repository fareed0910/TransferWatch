package com.example.transferwatchbackend.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransferControllerTest {

    private TransferService transferService;
    private TransferController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        transferService = mock(TransferService.class);
        controller = new TransferController(transferService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    }

    @Test
    void returnsTransfersForRequestedTeam() {

        int arsenalId = 42;

        List<Transfer> expected =
                List.of(
                        new Transfer(
                                "Test Player",
                                "Other Club",
                                "Arsenal",
                                "€30M",
                                "2026-08-20"
                        )
                );

        when(transferService.getTransfers(arsenalId))
                .thenReturn(expected);

        List<Transfer> actual =
                controller.getTransfersForTeam(arsenalId);

        assertThat(actual).isEqualTo(expected);

        verify(transferService)
                .getTransfers(arsenalId);
    }

    @Test
    void rejectsNonPositiveTeamId() {

        assertThatThrownBy(
                () -> controller.getTransfersForTeam(0)
        )
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException =
                            (ResponseStatusException) exception;

                    assertThat(responseException.getStatusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST);
                });

        verifyNoInteractions(transferService);
    }

    @Test
    void exposesTeamTransfersEndpoint() throws Exception {

        when(transferService.getTransfers(42))
                .thenReturn(
                        List.of(
                                new Transfer(
                                        "Test Player",
                                        "Other Club",
                                        "Arsenal",
                                        "€30M",
                                        "2026-08-20"
                                )
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/teams/42/transfers"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].playerName")
                                .value("Test Player")
                )
                .andExpect(
                        jsonPath("$[0].fromClub")
                                .value("Other Club")
                )
                .andExpect(
                        jsonPath("$[0].toClub")
                                .value("Arsenal")
                )
                .andExpect(
                        jsonPath("$[0].transferType")
                                .value("€30M")
                )
                .andExpect(
                        jsonPath("$[0].date")
                                .value("2026-08-20")
                );

        verify(transferService)
                .getTransfers(42);
    }

    @Test
    void invalidTeamIdReturnsBadRequestThroughHttp()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/teams/0/transfers"
                        )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(transferService);
    }

    @Test
    void nonNumericTeamIdReturnsBadRequest()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/teams/arsenal/transfers"
                        )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(transferService);
    }

    @Test
    void emptyTransferResultReturnsEmptyJsonArray()
            throws Exception {

        when(transferService.getTransfers(42))
                .thenReturn(List.of());

        mockMvc.perform(
                        get(
                                "/api/teams/42/transfers"
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

        verify(transferService)
                .getTransfers(42);
    }

    @Test
    void legacyTransferEndpointDoesNotExist()
            throws Exception {

        mockMvc.perform(
                        get("/api/transfers")
                )
                .andExpect(
                        status().isNotFound()
                );

        verifyNoInteractions(transferService);
    }

}