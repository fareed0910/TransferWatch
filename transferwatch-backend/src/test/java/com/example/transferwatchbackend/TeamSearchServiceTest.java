package com.example.transferwatchbackend;

import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TeamSearchServiceTest {
    @Test
    void searchesForNormalizedQuery() {

        TeamProvider provider = mock(TeamProvider.class);
        TeamSearchService service = new TeamSearchService(provider);

        List<Team> expected =
                List.of(new Team(42, "Arsenal", "logo"));

        when(provider.searchTeams("arsenal"))
                .thenReturn(expected);

        assertThat(service.search("  arsenal  "))
                .isEqualTo(expected);

        verify(provider).searchTeams("arsenal");
    }

    @Test
    void doesNotSearchForQueryShorterThanThreeCharacters() {

        TeamProvider provider = mock(TeamProvider.class);
        TeamSearchService service = new TeamSearchService(provider);

        assertThat(service.search("ar")).isEmpty();

        verifyNoInteractions(provider);
    }

    @Test
    void doesNotSearchForNullQuery() {

        TeamProvider provider = mock(TeamProvider.class);
        TeamSearchService service = new TeamSearchService(provider);

        assertThat(service.search(null)).isEmpty();

        verifyNoInteractions(provider);
    }
}
