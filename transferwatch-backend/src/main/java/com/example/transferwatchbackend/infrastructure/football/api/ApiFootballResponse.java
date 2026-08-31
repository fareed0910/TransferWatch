package com.example.transferwatchbackend.infrastructure.football.api;

import java.util.List;

public record ApiFootballResponse(
        List<ApiPlayerTransfer> response
) {
}