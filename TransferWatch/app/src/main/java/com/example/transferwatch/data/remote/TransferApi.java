package com.example.transferwatch.data.remote;

import com.example.transferwatch.domain.model.Team;
import com.example.transferwatch.domain.model.Transfer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransferApi {

    @GET("api/teams")
    Call<List<Team>> searchTeams(@Query("query") String query);

    @GET("api/teams/{teamId}/transfers")
    Call<List<Transfer>> getTransfers(@Path("teamId") int teamId);
}