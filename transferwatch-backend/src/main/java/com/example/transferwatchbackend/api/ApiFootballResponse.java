package com.example.transferwatchbackend.api;

import java.util.List;

public record ApiFootballResponse(
        List<ApiPlayerTransfer> response
) {
}