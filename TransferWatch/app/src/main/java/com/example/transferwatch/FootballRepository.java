package com.example.transferwatch;

import java.util.List;

public interface FootballRepository {

    void searchTeams(
            String query,
            RepositoryCallback<List<Team>> callback
    );

    void getTransfers(
            int teamId,
            RepositoryCallback<List<Transfer>> callback
    );
}