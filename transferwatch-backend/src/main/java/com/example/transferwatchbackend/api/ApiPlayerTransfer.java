package com.example.transferwatchbackend.api;

import java.util.List;

public record ApiPlayerTransfer(
        ApiPlayer player,
        String update,
        List<ApiTransfer> transfers
) {
}