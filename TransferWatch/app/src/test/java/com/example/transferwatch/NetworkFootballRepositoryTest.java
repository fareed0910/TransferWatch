package com.example.transferwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.transferwatch.NetworkFootballRepository;
import com.example.transferwatch.TransferApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkFootballRepositoryTest {
    private MockWebServer mockWebServer;
    private NetworkFootballRepository repository;

    @Before
    public void setUp() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        TransferApi transferApi =
                new Retrofit.Builder()
                        .baseUrl(mockWebServer.url("/"))
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build()
                        .create(TransferApi.class);

        repository =
                new NetworkFootballRepository(
                        transferApi
                );
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void returnsTransfersFromApi()
            throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .setBody("""
                            [
                              {
                                "playerName": "Test Player",
                                "fromClub": "Club A",
                                "toClub": "Arsenal",
                                "transferType": "€20M",
                                "date": "2026-08-20"
                              }
                            ]
                            """)
        );

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<Transfer>> result =
                new AtomicReference<>();
        AtomicReference<Throwable> error =
                new AtomicReference<>();

        repository.getTransfers(
                42,
                new RepositoryCallback<>() {

                    @Override
                    public void onSuccess(
                            List<Transfer> transfers
                    ) {
                        result.set(transfers);
                        latch.countDown();
                    }

                    @Override
                    public void onError(
                            Throwable throwable
                    ) {
                        error.set(throwable);
                        latch.countDown();
                    }
                }
        );

        assertTrue(
                latch.await(2, TimeUnit.SECONDS)
        );

        assertNull(error.get());
        assertNotNull(result.get());
        assertEquals(1, result.get().size());
        assertEquals(
                "Arsenal",
                result.get().get(0).toClub()
        );
    }
    @Test
    public void reportsHttpError()
            throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
        );

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error =
                new AtomicReference<>();

        repository.searchTeams(
                "arsenal",
                new RepositoryCallback<>() {

                    @Override
                    public void onSuccess(
                            List<Team> teams
                    ) {
                        latch.countDown();
                    }

                    @Override
                    public void onError(
                            Throwable throwable
                    ) {
                        error.set(throwable);
                        latch.countDown();
                    }
                }
        );

        assertTrue(
                latch.await(2, TimeUnit.SECONDS)
        );

        assertTrue(
                error.get() instanceof HttpException
        );

        assertEquals(
                500,
                ((HttpException) error.get()).code()
        );
    }
}