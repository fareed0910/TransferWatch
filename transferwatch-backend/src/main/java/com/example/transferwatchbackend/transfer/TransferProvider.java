package com.example.transferwatchbackend.transfer;

import com.example.transferwatchbackend.infrastructure.football.api.ApiFootballResponse;

public interface TransferProvider {

    ApiFootballResponse getTransfers(int teamId);
}