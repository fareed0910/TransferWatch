package com.example.transferwatch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TransferApi {

    @GET("api/transfers")
    Call<List<Transfer>> getTransfers();
}