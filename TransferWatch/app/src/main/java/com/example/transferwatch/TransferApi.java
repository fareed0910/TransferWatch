package com.example.transferwatch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransferApi {

    @GET("api/transfers")
    Call<List<Transfer>> getTransfers();

    @GET("api/teams")
    Call<List<Team>> searchTeams(
            @Query("query") String query
    );

    @GET("api/teams/{teamId}/transfers")
    Call<List<Transfer>> getTransfers(
            @Path("teamId") int teamId
    );
}