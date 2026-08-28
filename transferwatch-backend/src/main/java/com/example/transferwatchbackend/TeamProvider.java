package com.example.transferwatchbackend;

import java.util.List;

public interface TeamProvider {
    List<Team> searchTeams(String query);

}
