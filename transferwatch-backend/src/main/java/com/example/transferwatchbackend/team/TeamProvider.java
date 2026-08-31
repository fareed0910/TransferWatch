package com.example.transferwatchbackend.team;

import java.util.List;

public interface TeamProvider {
    List<Team> searchTeams(String query);

}
