package com.example.transferwatch;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    private static final String LOCAL_NETWORK_PERMISSION =
            "android.permission.ACCESS_LOCAL_NETWORK";

    private final List<Transfer> transfers =
            new ArrayList<>();

    private TransferAdapter adapter;

    private RecyclerView recyclerView;

    private View loadingContainer;
    private View errorContainer;

    private TextView errorText;

    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        recyclerView =
                findViewById(
                        R.id.transferRecyclerView
                );

        loadingContainer =
                findViewById(
                        R.id.loadingContainer
                );

        errorContainer =
                findViewById(
                        R.id.errorContainer
                );

        errorText =
                findViewById(
                        R.id.errorText
                );

        retryButton =
                findViewById(
                        R.id.retryButton
                );


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new TransferAdapter(transfers);

        recyclerView.setAdapter(adapter);


        retryButton.setOnClickListener(
                view ->
                        loadTransfersWithPermissionCheck()
        );


        loadTransfersWithPermissionCheck();


        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat
                                            .Type
                                            .systemBars()
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

    private void loadTransfersWithPermissionCheck() {
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

            loadTransfers();
        }
    }

    private void showLoading() {

        loadingContainer.setVisibility(View.VISIBLE);

        errorContainer.setVisibility(View.GONE);

        recyclerView.setVisibility(View.GONE);
    }

    private void showContent() {

        loadingContainer.setVisibility(View.GONE);

        errorContainer.setVisibility(View.GONE);

        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {

        loadingContainer.setVisibility(View.GONE);

        recyclerView.setVisibility(View.GONE);

        errorContainer.setVisibility(View.VISIBLE);

        errorText.setText(message);
    }

    private void loadTransfers() {

        showLoading();

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

                    if (response.body().isEmpty()) {

                        showError(
                                "No transfers are currently available."
                        );

                        return;
                    }

                    transfers.clear();

                    transfers.addAll(
                            response.body()
                    );

                    adapter.notifyDataSetChanged();

                    showContent();

                } else {

                    showError(
                            "Server error: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<List<Transfer>> call,
                    Throwable throwable
            ) {

                showError(
                        "Could not load transfers.\n\n"
                                + throwable.getClass()
                                .getSimpleName()
                );

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

        if (requestCode
                == LOCAL_NETWORK_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                loadTransfers();

            } else {

                showError(
                        "Local network access is required "
                                + "to connect to the development server."
                );
            }
        }
    }
}