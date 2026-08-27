package com.example.transferwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransferApiTest {

    private MockWebServer mockWebServer;
    private TransferApi transferApi;

    @Before
    public void setUp() throws IOException {

        mockWebServer =
                new MockWebServer();

        mockWebServer.start();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(
                                mockWebServer.url("/")
                        )
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        )
                        .build();

        transferApi =
                retrofit.create(
                        TransferApi.class
                );
    }

    @After
    public void tearDown() throws IOException {

        mockWebServer.shutdown();
    }

    @Test
    public void parsesValidTransferResponse()
            throws IOException {

        String json = """
            [
              {
                "playerName": "Test Player",
                "fromClub": "Club A",
                "toClub": "Club B",
                "transferType": "€25M",
                "date": "2026-08-20"
              }
            ]
            """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
        );


        Response<List<Transfer>> response =
                transferApi
                        .getTransfers()
                        .execute();


        assertTrue(response.isSuccessful());

        assertEquals(
                1,
                response.body().size()
        );

        Transfer transfer =
                response.body().get(0);

        assertEquals(
                "Test Player",
                transfer.playerName()
        );

        assertEquals(
                "Club A",
                transfer.fromClub()
        );

        assertEquals(
                "Club B",
                transfer.toClub()
        );
    }

    @Test
    public void handlesServerError()
            throws IOException {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
        );


        Response<List<Transfer>> response =
                transferApi
                        .getTransfers()
                        .execute();


        assertEquals(
                500,
                response.code()
        );

        assertTrue(
                !response.isSuccessful()
        );
    }

    @Test
    public void malformedJsonCausesParsingFailure() {

        String invalidJson =
                """
                [
                  {
                    "playerName":
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(invalidJson)
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
        );


        boolean exceptionThrown = false;

        try {

            transferApi
                    .getTransfers()
                    .execute();

        } catch (Exception exception) {

            exceptionThrown = true;
        }


        assertTrue(exceptionThrown);
    }

    @Test
    public void requestsCorrectEndpoint()
            throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("[]")
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
        );

        transferApi
                .getTransfers()
                .execute();


        String path =
                mockWebServer
                        .takeRequest()
                        .getPath();


        assertEquals(
                "/api/transfers",
                path
        );
    }

    @Test
    public void responseIsProcessedQuickly()
            throws IOException {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("[]")
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
        );

        long start =
                System.currentTimeMillis();

        transferApi
                .getTransfers()
                .execute();

        long duration =
                System.currentTimeMillis()
                        - start;

        assertTrue(
                duration < 1000
        );
    }

}