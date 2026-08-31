package com.example.transferwatch.domain.repository;

import com.example.transferwatch.domain.model.Team;
import com.example.transferwatch.domain.model.Transfer;

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