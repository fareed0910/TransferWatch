package com.example.transferwatch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TeamSearchViewModel extends ViewModel {

    private static final int MINIMUM_QUERY_LENGTH = 3;

    private final FootballRepository repository;

    private final MutableLiveData<TeamSearchState> state =
            new MutableLiveData<>(
                    TeamSearchState.idle()
            );

    public TeamSearchViewModel(
            FootballRepository repository
    ) {
        this.repository = repository;
    }

    public LiveData<TeamSearchState> state() {
        return state;
    }

    public void search(String query) {

        if (query == null
                || query.trim().length()
                < MINIMUM_QUERY_LENGTH) {

            state.setValue(
                    TeamSearchState.idle()
            );

            return;
        }

        String normalizedQuery = query.trim();

        state.setValue(
                new TeamSearchState(
                        TeamSearchState.Status.LOADING,
                        List.of(),
                        null
                )
        );

        repository.searchTeams(
                normalizedQuery,
                new RepositoryCallback<>() {

                    @Override
                    public void onSuccess(
                            List<Team> teams
                    ) {
                        if (teams.isEmpty()) {
                            state.setValue(
                                    new TeamSearchState(
                                            TeamSearchState.Status.EMPTY,
                                            List.of(),
                                            null
                                    )
                            );
                            return;
                        }

                        state.setValue(
                                new TeamSearchState(
                                        TeamSearchState.Status.RESULTS,
                                        teams,
                                        null
                                )
                        );
                    }

                    @Override
                    public void onError(
                            Throwable throwable
                    ) {
                        state.setValue(
                                new TeamSearchState(
                                        TeamSearchState.Status.ERROR,
                                        List.of(),
                                        "Could not search for teams.\n\n"
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