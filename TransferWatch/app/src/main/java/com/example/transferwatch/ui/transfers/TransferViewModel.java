package com.example.transferwatch.ui.transfers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.transferwatch.domain.model.Team;
import com.example.transferwatch.domain.model.Transfer;
import com.example.transferwatch.domain.repository.FootballRepository;
import com.example.transferwatch.domain.repository.RepositoryCallback;

import java.util.List;

public class TransferViewModel extends ViewModel {

    private final FootballRepository repository;

    private final MutableLiveData<TransferScreenState> state =
            new MutableLiveData<>(
                    TransferScreenState.idle()
            );

    private final MutableLiveData<Team> selectedTeam =
            new MutableLiveData<>();

    public TransferViewModel(
            FootballRepository repository
    ) {
        this.repository = repository;
    }

    public LiveData<TransferScreenState> state() {
        return state;
    }

    public LiveData<Team> selectedTeam() {
        return selectedTeam;
    }


    public void selectTeam(Team team) {

        if (team == null || team.id() == null) {
            return;
        }

        selectedTeam.setValue(team);
        loadTransfers(team.id());
    }

    public void refresh() {

        Team team = selectedTeam.getValue();

        if (team != null && team.id() != null) {
            loadTransfers(team.id());
        }
    }

    private void loadTransfers(
            int teamId
    ) {
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
                teamId,
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