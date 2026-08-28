package com.example.transferwatchbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TransferControllerTest {

    private TransferService transferService;
    private TransferController controller;

    @BeforeEach
    void setUp() {

        transferService =
                mock(TransferService.class);

        controller =
                new TransferController(
                        transferService
                );
    }

    @Test
    void returnsTransfersFromService() {

        List<Transfer> expected =
                List.of(
                        new Transfer(
                                "Test Player",
                                "Club A",
                                "Club B",
                                "Loan",
                                "2026-08-20"
                        )
                );

        when(
                transferService.getTransfers(33)
        ).thenReturn(expected);


        List<Transfer> actual =
                controller.getTransfers();


        assertThat(actual)
                .isEqualTo(expected);

        verify(transferService)
                .getTransfers(33);
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
                .thenReturn(List.of());

        MockMvcBuilders
                .standaloneSetup(controller)
                .build()
                .perform(get("/api/teams/42/transfers"))
                .andExpect(status().isOk());

        verify(transferService).getTransfers(42);
    }

}