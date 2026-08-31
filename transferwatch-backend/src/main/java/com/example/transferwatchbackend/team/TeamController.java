package com.example.transferwatchbackend.team;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamSearchService teamSearchService;

    public TeamController(TeamSearchService teamSearchService) {
        this.teamSearchService = teamSearchService;
    }

    @GetMapping
    public List<Team> searchTeams(
            @RequestParam String query
    ) {
        return teamSearchService.search(query);
    }
}