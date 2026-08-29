package com.example.transferwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TransferViewModelTest {

    private static class FakeFootballRepository
            implements FootballRepository {

        private List<Transfer> transfers;
        private Throwable error;
        private int requestedTeamId;

        @Override
        public void getTransfers(
                int teamId,
                RepositoryCallback<List<Transfer>> callback
        ) {
            requestedTeamId = teamId;

            if (error != null) {
                callback.onError(error);
            } else {
                callback.onSuccess(transfers);
            }
        }

        @Override
        public void searchTeams(
                String query,
                RepositoryCallback<List<Team>> callback
        ) {
            throw new UnsupportedOperationException();
        }
    }

    @Rule
    public InstantTaskExecutorRule rule =
            new InstantTaskExecutorRule();

    @Test
    public void successfulLoadProducesContent() {

        Transfer expected =
                new Transfer(
                        "Test Player",
                        "Club A",
                        "Manchester United",
                        "€20M",
                        "2026-08-20"
                );

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.transfers =
                List.of(expected);

        TransferViewModel viewModel =
                new TransferViewModel(repository);

        viewModel.loadTransfers();

        TransferScreenState state =
                viewModel.state().getValue();

        assertNotNull(state);
        assertEquals(
                TransferScreenState.Status.CONTENT,
                state.status()
        );
        assertEquals(
                List.of(expected),
                state.transfers()
        );
        assertEquals(33, repository.requestedTeamId);
    }

    @Test
    public void emptyResponseProducesEmptyState() {

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.transfers = List.of();

        TransferViewModel viewModel =
                new TransferViewModel(repository);

        viewModel.loadTransfers();

        assertEquals(
                TransferScreenState.Status.EMPTY,
                viewModel.state().getValue().status()
        );
    }

    @Test
    public void repositoryFailureProducesErrorState() {

        FakeFootballRepository repository =
                new FakeFootballRepository();

        repository.error =
                new IOException("Network unavailable");

        TransferViewModel viewModel =
                new TransferViewModel(repository);

        viewModel.loadTransfers();

        TransferScreenState state =
                viewModel.state().getValue();

        assertEquals(
                TransferScreenState.Status.ERROR,
                state.status()
        );

        assertTrue(
                state.errorMessage()
                        .contains("IOException")
        );
    }
}