package com.example.transferwatchbackend;

import com.example.transferwatchbackend.api.ApiFootballResponse;
import com.example.transferwatchbackend.api.ApiPlayerTransfer;
import com.example.transferwatchbackend.api.ApiTransfer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class TransferService {

    private final ApiFootballClient apiFootballClient;

    public TransferService(
            ApiFootballClient apiFootballClient
    ) {
        this.apiFootballClient =
                apiFootballClient;
    }

    public List<Transfer> getTransfers() {

        int teamId = 42;

        ApiFootballResponse response =
                apiFootballClient
                        .getTransfers(teamId);
        System.out.println(
                "Players returned by API: " + response.response().size()
        );


        List<Transfer> transfers =
                new ArrayList<>();

        for (ApiPlayerTransfer playerEntry
                : response.response()) {

            for (ApiTransfer apiTransfer
                    : playerEntry.transfers()) {

                if (apiTransfer.teams() == null
                        || apiTransfer.teams().in() == null
                        || apiTransfer.teams().out() == null) {
                    continue;
                }


                /*
                 * Defensive check:
                 * only keep transfers actually involving
                 * Manchester United.
                 */
                if (!Objects.equals(
                        apiTransfer.teams().in().id(),
                        teamId
                )
                        && !Objects.equals(
                        apiTransfer.teams().out().id(),
                        teamId
                )) {

                    continue;
                }
                Transfer transfer =
                        new Transfer(
                                playerEntry.player().name(),
                                apiTransfer.teams().out().name(),
                                apiTransfer.teams().in().name(),
                                apiTransfer.type(),
                                apiTransfer.date()
                        );

                transfers.add(transfer);
            }
        }

        transfers.sort(
                Comparator.comparing(
                        Transfer::date
                ).reversed()
        );
        System.out.println("Transfers after Mapping: " + transfers.size());
        return transfers;
    }
}