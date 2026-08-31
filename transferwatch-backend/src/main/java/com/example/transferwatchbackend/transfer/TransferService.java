package com.example.transferwatchbackend.transfer;

import com.example.transferwatchbackend.infrastructure.football.api.ApiFootballResponse;
import com.example.transferwatchbackend.infrastructure.football.api.ApiPlayerTransfer;
import com.example.transferwatchbackend.infrastructure.football.api.ApiTransfer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class TransferService {

    private final TransferProvider transferProvider;

    public TransferService(TransferProvider transferProvider) {
        this.transferProvider = transferProvider;
    }

    public List<Transfer> getTransfers(int teamId) {
        {

            ApiFootballResponse response = transferProvider.getTransfers(teamId);

            if (response == null || response.response() == null) {
                return List.of();
            }

            List<Transfer> transfers = new ArrayList<>();

            for (ApiPlayerTransfer playerEntry : response.response()) {
                if (playerEntry == null || playerEntry.transfers() == null) {
                    continue;
                }
                for (ApiTransfer apiTransfer : playerEntry.transfers()) {
                    if (apiTransfer.teams() == null || apiTransfer.teams().in() == null || apiTransfer.teams().out() == null) {
                        continue;
                    }
                    /*
                     * Defensive check:
                     * only keep transfers actually involving selected Team
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
            return transfers;
        }
    }
}