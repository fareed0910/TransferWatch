package com.example.transferwatchbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
                transferService.getTransfers()
        ).thenReturn(expected);


        List<Transfer> actual =
                controller.getTransfers();


        assertThat(actual)
                .isEqualTo(expected);

        verify(transferService)
                .getTransfers();
    }
}