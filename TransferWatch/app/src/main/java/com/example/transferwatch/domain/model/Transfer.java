package com.example.transferwatch.domain.model;
public record Transfer(
        String playerName,
        String fromClub,
        String toClub,
        String transferType,
        String date
) {
}
