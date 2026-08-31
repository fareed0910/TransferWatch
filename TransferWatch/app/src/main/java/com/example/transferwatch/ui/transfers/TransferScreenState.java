package com.example.transferwatch.ui.transfers;

import com.example.transferwatch.domain.model.Transfer;

import java.util.List;

public record TransferScreenState(Status status, List<Transfer> transfers, String errorMessage) {

    public enum Status {
        IDLE,
        LOADING,
        CONTENT,
        EMPTY,
        ERROR
    }

    public TransferScreenState {
        transfers = List.copyOf(transfers);
    }

    public static TransferScreenState idle() {
        return new TransferScreenState(
                Status.IDLE,
                List.of(),
                null
        );
    }
}