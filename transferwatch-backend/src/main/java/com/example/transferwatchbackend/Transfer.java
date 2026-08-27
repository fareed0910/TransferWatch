package com.example.transferwatchbackend;

public record Transfer(
    String playerName,
    String fromClub,
    String toClub,
    String transferType,
    String date
){}
