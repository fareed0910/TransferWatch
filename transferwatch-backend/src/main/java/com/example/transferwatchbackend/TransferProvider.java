package com.example.transferwatchbackend;

import com.example.transferwatchbackend.api.ApiFootballResponse;

public interface TransferProvider {

    ApiFootballResponse getTransfers(int teamId);
}