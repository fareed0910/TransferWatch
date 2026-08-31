package com.example.transferwatch.ui.search;

import com.example.transferwatch.domain.model.Team;

import java.util.List;

public record TeamSearchState(
        Status status,
        List<Team> teams,
        String errorMessage
) {

    public enum Status {
        IDLE,
        LOADING,
        RESULTS,
        EMPTY,
        ERROR
    }

    public TeamSearchState {
        teams = List.copyOf(teams);
    }

    public static TeamSearchState idle() {
        return new TeamSearchState(
                Status.IDLE,
                List.of(),
                null
        );
    }
}