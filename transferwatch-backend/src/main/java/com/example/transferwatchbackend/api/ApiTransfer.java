package com.example.transferwatchbackend.api;

public record ApiTransfer(
        String date,
        String type,
        ApiTransferTeams teams
) {
}