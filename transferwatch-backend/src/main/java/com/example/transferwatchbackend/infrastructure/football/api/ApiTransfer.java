package com.example.transferwatchbackend.infrastructure.football.api;

public record ApiTransfer(
        String date,
        String type,
        ApiTransferTeams teams
) {
}