package com.example.transferwatchbackend.transfer;

import com.example.transferwatchbackend.infrastructure.football.api.ApiFootballResponse;
import com.example.transferwatchbackend.infrastructure.football.api.ApiPlayer;
import com.example.transferwatchbackend.infrastructure.football.api.ApiPlayerTransfer;
import com.example.transferwatchbackend.infrastructure.football.api.ApiTeam;
import com.example.transferwatchbackend.infrastructure.football.api.ApiTransfer;
import com.example.transferwatchbackend.infrastructure.football.api.ApiTransferTeams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private TransferProvider transferProvider;
    private TransferService transferService;

    @BeforeEach
    void setUp() {

        transferProvider =
                mock(TransferProvider.class);

        transferService =
                new TransferService(transferProvider);
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
              transferProvider.getTransfers(33)
        ).thenReturn(apiResponse);


        List<Transfer> result =
                transferService.getTransfers(33);


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
                transferProvider.getTransfers(33)
        ).thenReturn(apiResponse);


        List<Transfer> result =
                transferService.getTransfers(33);


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
                transferProvider.getTransfers(33)
        ).thenReturn(
                new ApiFootballResponse(
                        List.of(playerTransfer)
                )
        );


        List<Transfer> result =
                transferService.getTransfers(33);


        assertThat(result)
                .hasSize(2);
    }

    @Test
    void returnsEmptyListWhenApiContainsNoPlayers() {

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(List.of())
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyListWhenPlayerResponseIsNull() {

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(null)
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).isEmpty();
    }

    @Test
    void ignoresPlayerWithoutTransferList() {

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                1,
                                "Test Player"
                        ),
                        null,
                        null
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).isEmpty();
    }

    @Test
    void ignoresTransferWithoutTeams() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "Loan",
                        null
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                1,
                                "Test Player"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).isEmpty();
    }

    @Test
    void ignoresTransferWithoutDestinationClub() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "Loan",
                        new ApiTransferTeams(
                                null,
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                1,
                                "Test Player"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        assertThat(
                transferService.getTransfers(33)
        ).isEmpty();
    }

    @Test
    void ignoresTransferWithoutSourceClub() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "Loan",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                ),
                                null
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                1,
                                "Test Player"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        assertThat(
                transferService.getTransfers(33)
        ).isEmpty();
    }

    @Test
    void includesIncomingTransferForSelectedTeam() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "€50M",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                ),
                                new ApiTeam(
                                        55,
                                        "Other Club",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                123,
                                "Player A"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().toClub())
                .isEqualTo("Manchester United");
    }

    @Test
    void includesOutgoingTransferForSelectedTeam() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "Free",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        77,
                                        "Other Club",
                                        null
                                ),
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(123, "Player A"),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result).hasSize(1);

        assertThat(result.getFirst().fromClub())
                .isEqualTo("Manchester United");
    }

    @Test
    void ignoresTransferUnrelatedToSelectedTeam() {

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "Loan",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        60,
                                        "Club B",
                                        null
                                ),
                                new ApiTeam(
                                        50,
                                        "Club A",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(123, "Player A"),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        assertThat(
                transferService.getTransfers(33)
        ).isEmpty();
    }

    @Test
    void sortsTransfersNewestFirst() {

        ApiTransfer older =
                new ApiTransfer(
                        "2025-01-01",
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

        ApiTransfer newer =
                new ApiTransfer(
                        "2026-08-20",
                        "€40M",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        33,
                                        "Manchester United",
                                        null
                                ),
                                new ApiTeam(
                                        60,
                                        "Club B",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer player =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                123,
                                "Test Player"
                        ),
                        null,
                        List.of(
                                older,
                                newer
                        )
                );

        when(transferProvider.getTransfers(33))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(player)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(33);

        assertThat(result)
                .extracting(Transfer::date)
                .containsExactly(
                        "2026-08-20",
                        "2025-01-01"
                );
    }

    @Test
    void loadsTransfersForRequestedTeam() {

        int arsenalId = 42;

        ApiTransfer apiTransfer =
                new ApiTransfer(
                        "2026-08-20",
                        "€30M",
                        new ApiTransferTeams(
                                new ApiTeam(
                                        arsenalId,
                                        "Arsenal",
                                        null
                                ),
                                new ApiTeam(
                                        50,
                                        "Other Club",
                                        null
                                )
                        )
                );

        ApiPlayerTransfer playerTransfer =
                new ApiPlayerTransfer(
                        new ApiPlayer(
                                123,
                                "Test Player"
                        ),
                        null,
                        List.of(apiTransfer)
                );

        when(transferProvider.getTransfers(arsenalId))
                .thenReturn(
                        new ApiFootballResponse(
                                List.of(playerTransfer)
                        )
                );

        List<Transfer> result =
                transferService.getTransfers(arsenalId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().toClub())
                .isEqualTo("Arsenal");

        verify(transferProvider)
                .getTransfers(arsenalId);
    }


}