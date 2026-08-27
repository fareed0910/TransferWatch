package com.example.transferwatch;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCAL_NETWORK_PERMISSION_REQUEST = 100;

    /*
     * Using the String directly makes this work even if Android Studio
     * does not recognise Manifest.permission.ACCESS_LOCAL_NETWORK yet.
     */
    private static final String LOCAL_NETWORK_PERMISSION =
            "android.permission.ACCESS_LOCAL_NETWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        List<Transfer> transfers = new ArrayList<>();

        RecyclerView recyclerView =
                findViewById(R.id.transferRecyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        TransferAdapter adapter =
                new TransferAdapter(transfers);

        recyclerView.setAdapter(adapter);


        // Android 17 / API 37 local-network permission
        if (Build.VERSION.SDK_INT >= 37
                && ContextCompat.checkSelfPermission(
                this,
                LOCAL_NETWORK_PERMISSION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{LOCAL_NETWORK_PERMISSION},
                    LOCAL_NETWORK_PERMISSION_REQUEST
            );

        } else {

            loadTransfers(transfers, adapter);
        }


        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }


    private void loadTransfers(
            List<Transfer> transfers,
            TransferAdapter adapter
    ) {

        TransferApi transferApi =
                ApiClient.getTransferApi();

        Call<List<Transfer>> call =
                transferApi.getTransfers();

        call.enqueue(new Callback<List<Transfer>>() {

            @Override
            public void onResponse(
                    Call<List<Transfer>> call,
                    Response<List<Transfer>> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    Toast.makeText(
                            MainActivity.this,
                            "Received "
                                    + response.body().size()
                                    + " transfers",
                            Toast.LENGTH_LONG
                    ).show();

                    transfers.clear();
                    transfers.addAll(response.body());

                    adapter.notifyDataSetChanged();

                } else {

                    Toast.makeText(
                            MainActivity.this,
                            "HTTP error: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<List<Transfer>> call,
                    Throwable throwable
            ) {

                Toast.makeText(
                        MainActivity.this,
                        "Request failed: " + throwable,
                        Toast.LENGTH_LONG
                ).show();

                throwable.printStackTrace();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCAL_NETWORK_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {

            // Restart MainActivity.
            // On the next onCreate(), the permission check succeeds
            // and loadTransfers() runs.
            recreate();
        }
    }
}