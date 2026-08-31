package com.example.transferwatchbackend.team;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamSearchService {

    private static final int MINIMUM_QUERY_LENGTH = 3;

    private final TeamProvider teamProvider;

    public TeamSearchService(TeamProvider teamProvider) {
        this.teamProvider = teamProvider;
    }

    public List<Team> search(String query) {

        if (query == null) {
            return List.of();
        }

        String normalizedQuery = query.trim();

        if (normalizedQuery.length() < MINIMUM_QUERY_LENGTH) {
            return List.of();
        }

        return teamProvider.searchTeams(normalizedQuery);
    }
}