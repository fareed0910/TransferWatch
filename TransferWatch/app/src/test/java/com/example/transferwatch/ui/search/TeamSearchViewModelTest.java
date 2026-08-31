package com.example.transferwatch.ui.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.transferwatch.domain.model.Team;
import com.example.transferwatch.domain.model.Transfer;
import com.example.transferwatch.domain.repository.FootballRepository;
import com.example.transferwatch.domain.repository.RepositoryCallback;

import org.junit.Rule;

public class TeamSearchViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    private static class FakeFootballRepository
            implements FootballRepository {
        private List<Team> teams = List.of();
        private String requestedQuery;
        private Throwable error;

        @Override
        public void searchTeams(
                String query,
                RepositoryCallback<List<Team>> callback
        ) {
            requestedQuery = query;

            if (error != null) {
                callback.onError(error);
            } else {
                callback.onSuccess(teams);
            }
        }

        @Override
        public void getTransfers(
                int teamId,
                RepositoryCallback<List<Transfer>> callback
        ) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shortQueryDoesNotCallRepository() {

        FakeFootballRepository repository =
                new FakeFootballRepository();

        TeamSearchViewModel viewModel =
                new TeamSearchViewModel(repository);

        viewModel.search("ar");

        assertEquals(
                TeamSearchState.Status.IDLE,
                viewModel.state().getValue().status()
        );

        assertNull(repository.requestedQuery);
    }

    @Test
    public void validQueryReturnsTeams() {

        Team arsenal =
                new Team(
                        42,
                        "Arsenal",
                        "logo"
                );

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.teams = List.of(arsenal);

        TeamSearchViewModel viewModel =
                new TeamSearchViewModel(repository);

        viewModel.search("  arsenal  ");

        TeamSearchState state =
                viewModel.state().getValue();

        assertNotNull(state);

        assertEquals(
                TeamSearchState.Status.RESULTS,
                state.status()
        );

        assertEquals(
                List.of(arsenal),
                state.teams()
        );

        assertEquals(
                "arsenal",
                repository.requestedQuery
        );
    }

    @Test
    public void emptyResultProducesEmptyState() {

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.teams = List.of();

        TeamSearchViewModel viewModel =
                new TeamSearchViewModel(repository);

        viewModel.search("unknown");

        assertEquals(
                TeamSearchState.Status.EMPTY,
                viewModel.state().getValue().status()
        );
    }

    @Test
    public void repositoryFailureProducesErrorState() {

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.error =
                new IOException("Network unavailable");

        TeamSearchViewModel viewModel =
                new TeamSearchViewModel(repository);

        viewModel.search("arsenal");

        TeamSearchState state =
                viewModel.state().getValue();

        assertEquals(
                TeamSearchState.Status.ERROR,
                state.status()
        );

        assertTrue(
                state.errorMessage()
                        .contains("IOException")
        );
    }
}
