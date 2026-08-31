package com.example.transferwatch.data.repository;

import com.example.transferwatch.domain.repository.FootballRepository;
import com.example.transferwatch.domain.repository.RepositoryCallback;
import com.example.transferwatch.domain.model.Team;
import com.example.transferwatch.domain.model.Transfer;
import com.example.transferwatch.data.remote.TransferApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class NetworkFootballRepository
        implements FootballRepository {

    private final TransferApi transferApi;

    public NetworkFootballRepository(
            TransferApi transferApi
    ) {
        this.transferApi = transferApi;
    }

    @Override
    public void searchTeams(
            String query,
            RepositoryCallback<List<Team>> callback
    ) {
        enqueue(
                transferApi.searchTeams(query),
                callback
        );
    }

    @Override
    public void getTransfers(
            int teamId,
            RepositoryCallback<List<Transfer>> callback
    ) {
        enqueue(
                transferApi.getTransfers(teamId),
                callback
        );
    }

    private <T> void enqueue(
            Call<T> call,
            RepositoryCallback<T> callback
    ) {
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(
                    Call<T> call,
                    Response<T> response
            ) {
                if (response.isSuccessful()
                        && response.body() != null) {

                    callback.onSuccess(response.body());

                } else {

                    callback.onError(
                            new HttpException(response)
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<T> call,
                    Throwable throwable
            ) {
                callback.onError(throwable);
            }
        });
    }
}