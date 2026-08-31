package com.example.transferwatchbackend.transfer;

public record Transfer(
    String playerName,
    String fromClub,
    String toClub,
    String transferType,
    String date
){}
