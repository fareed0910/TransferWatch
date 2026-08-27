package com.example.transferwatchbackend;

import com.example.transferwatchbackend.api.ApiFootballResponse;
import com.example.transferwatchbackend.api.ApiPlayer;
import com.example.transferwatchbackend.api.ApiPlayerTransfer;
import com.example.transferwatchbackend.api.ApiTeam;
import com.example.transferwatchbackend.api.ApiTransfer;
import com.example.transferwatchbackend.api.ApiTransferTeams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransferServiceTest {

    private ApiFootballClient apiFootballClient;
    private TransferService transferService;

    @BeforeEach
    void setUp() {

        apiFootballClient =
                mock(ApiFootballClient.class);

        transferService =
                new TransferService(apiFootballClient);
    }

    @Test
    void mapsApiTransferToTransfer() {

        ApiTeam oldClub =
                new ApiTeam(
                        40,
                        "Old Club",
                        "logo-old"
                );

        ApiTeam newClub =
                new ApiTeam(
                        33,
                        "Manchester United",
                        "logo-new"
                );

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "€40M",
                        new ApiTransferTeams(
                                newClub,
                                oldClub
                        )
                );

        ApiPlayerTransfer playerTransfer =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                123,
                                "Test Player"
                        ),
                        "2026-08-20T12:00:00+00:00",
                        List.of(apiTransfer)
                );

        ApiFootballResponse apiResponse =
                new ApiFootballResponse(
                        List.of(playerTransfer)
                );

        when(
                apiFootballClient.getTransfers(33)
        ).thenReturn(apiResponse);


        List<Transfer> result =
                transferService.getTransfers();


        assertThat(result).hasSize(1);

        Transfer transfer = result.getFirst();

        assertThat(transfer.playerName())
                .isEqualTo("Test Player");

        assertThat(transfer.fromClub())
                .isEqualTo("Old Club");

        assertThat(transfer.toClub())
                .isEqualTo("Manchester United");

        assertThat(transfer.transferType())
                .isEqualTo("€40M");

        assertThat(transfer.date())
                .isEqualTo("2026-08-20");
    }

    @Test
    void handlesTeamWithNullId() {

        ApiTeam unknownClub =
                new ApiTeam(
                        null,
                        "Unknown Club",
                        null
                );

        ApiTeam manchesterUnited =
                new ApiTeam(
                        33,
                        "Manchester United",
                        null
                );

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-01",
                        "Free",
                        new ApiTransferTeams(
                                unknownClub,
                                manchesterUnited
                        )
                );

        ApiPlayerTransfer playerTransfer =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                999,
                                "Test Player"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        ApiFootballResponse apiResponse =
                new ApiFootballResponse(
                        List.of(playerTransfer)
                );

        when(
                apiFootballClient.getTransfers(33)
        ).thenReturn(apiResponse);


        List<Transfer> result =
                transferService.getTransfers();


        assertThat(result)
                .isNotNull();
    }

    @Test
    void mapsMultipleTransfersForPlayer() {

        ApiTransfer transfer1 =
                new ApiTransfer(
                        "2026-08-20",
                        "Loan",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                ),
                                new ApiTeam(
                                        50,
                                        "Club A",
                                        null
                                )
                        )
                );

        ApiTransfer transfer2 =
                new ApiTransfer(
                        "2025-07-01",
                        "Free",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        60,
                                        "Club B",
                                        null
                                ),
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer playerTransfer =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                100,
                                "Player One"
                        ),
                        null,
                        List.of(
                                transfer1,
                                transfer2
                        )
                );

        when(
                apiFootballClient.getTransfers(33)
        ).thenReturn(
                new ApiFootballResponse(
                        List.of(playerTransfer)
                )
        );


        List<Transfer> result =
                transferService.getTransfers();


        assertThat(result)
                .hasSize(2);
    }

}