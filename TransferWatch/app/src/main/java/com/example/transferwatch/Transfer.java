package com.example.transferwatch;
public record Transfer(
        String playerName,
        String fromClub,
        String toClub,
        String transferType,
        String date
) {
}
