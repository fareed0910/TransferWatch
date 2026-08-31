package com.example.transferwatchbackend.infrastructure.football.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiTransferTeams(


        ApiTeam in,

        ApiTeam out
) {
}