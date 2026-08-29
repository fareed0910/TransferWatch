package com.example.transferwatch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TransferViewModel extends ViewModel {
    private static final int DEFAULT_TEAM_ID = 33;

    private final FootballRepository repository;

    private final MutableLiveData<TransferScreenState> state =
            new MutableLiveData<>(
                    TransferScreenState.idle()
            );

    public TransferViewModel(
            FootballRepository repository
    ) {
        this.repository = repository;
    }

    public LiveData<TransferScreenState> state() {
        return state;
    }

    public void loadInitialTransfers() {

        TransferScreenState current = state.getValue();

        if (current == null
                || current.status()
                == TransferScreenState.Status.IDLE) {
            loadTransfers();
        }
    }

    public void loadTransfers() {

        TransferScreenState current = state.getValue();

        List<Transfer> existingTransfers =
                current == null
                        ? List.of()
                        : current.transfers();

        state.setValue(
                new TransferScreenState(
                        TransferScreenState.Status.LOADING,
                        existingTransfers,
                        null
                )
        );

        repository.getTransfers(
                DEFAULT_TEAM_ID,
                new RepositoryCallback<>() {

                    @Override
                    public void onSuccess(
                            List<Transfer> transfers
                    ) {
                        if (transfers.isEmpty()) {
                            state.setValue(
                                    new TransferScreenState(
                                            TransferScreenState.Status.EMPTY,
                                            List.of(),
                                            null
                                    )
                            );
                            return;
                        }

                        state.setValue(
                                new TransferScreenState(
                                        TransferScreenState.Status.CONTENT,
                                        transfers,
                                        null
                                )
                        );
                    }

                    @Override
                    public void onError(
                            Throwable throwable
                    ) {
                        state.setValue(
                                new TransferScreenState(
                                        TransferScreenState.Status.ERROR,
                                        List.of(),
                                        "Could not load transfers.\n\n"
                                                + throwable
                                                .getClass()
                                                .getSimpleName()
                                )
                        );
                    }
                }
        );
    }
}
