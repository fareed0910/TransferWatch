package com.example.transferwatchbackend.team;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class TeamControllerTest {
    @Test
    void returnsTeamsFromSearchService() {

        TeamSearchService service =
                mock(TeamSearchService.class);

        TeamController controller =
                new TeamController(service);

        List<Team> expected =
                List.of(new Team(42, "Arsenal", "logo"));

        when(service.search("arsenal"))
                .thenReturn(expected);

        assertThat(controller.searchTeams("arsenal"))
                .isEqualTo(expected);

        verify(service).search("arsenal");
    }


}
