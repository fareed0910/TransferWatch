package com.example.transferwatchbackend.infrastructure.football.api;

import java.util.List;

public record ApiPlayerTransfer(
        ApiPlayer player,
        String update,
        List<ApiTransfer> transfers
) {
}