package com.example.transferwatch;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final Team DEFAULT_TEAM =
            new Team(
                    33,
                    "Manchester United",
                    null
            );
    private static final int LOCAL_NETWORK_PERMISSION_REQUEST = 100;

    private static final String LOCAL_NETWORK_PERMISSION =
            "android.permission.ACCESS_LOCAL_NETWORK";

    private final List<Transfer> transfers =
            new ArrayList<>();


    private TransferAdapter adapter;
    private TransferViewModel viewModel;

    private View loadingContainer;
    private View errorContainer;

    private TextView errorText;
    private Button retryButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Runnable pendingPermissionAction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView =
                findViewById(
                        R.id.transferRecyclerView
                );

        swipeRefreshLayout =
                findViewById(
                        R.id.swipeRefreshLayout
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

        FootballRepository repository =
                new NetworkFootballRepository(
                        ApiClient.getTransferApi()
                );

        viewModel =
                new ViewModelProvider(
                        this,
                        new TransferViewModelFactory(
                                repository
                        )
                ).get(TransferViewModel.class);

        viewModel.state().observe(
                this,
                this::render
        );

        swipeRefreshLayout.setOnRefreshListener(
                () -> runWithPermission(
                        viewModel::refresh
                )
        );

        retryButton.setOnClickListener(
                view -> runWithPermission(
                        viewModel::refresh
                )
        );;

        runWithPermission(
                () -> viewModel.loadInitialTransfers(
                        DEFAULT_TEAM
                )
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat
                                            .Type
                                            .systemBars()
                            );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }


    private void runWithPermission(
            Runnable action
    ) {
        if (requestLocalNetworkPermissionIfNeeded()) {
            pendingPermissionAction = action;
        } else {
            action.run();
        }
    }


    private boolean requestLocalNetworkPermissionIfNeeded() {

        if (Build.VERSION.SDK_INT >= 37
                && ContextCompat.checkSelfPermission(
                this,
                LOCAL_NETWORK_PERMISSION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            LOCAL_NETWORK_PERMISSION
                    },
                    LOCAL_NETWORK_PERMISSION_REQUEST
            );

            return true;
        }

        return false;
    }

    private void render(
            TransferScreenState state
    ) {
        swipeRefreshLayout.setRefreshing(false);

        switch (state.status()) {

            case IDLE -> {
                // Waiting for the initial load or permission.
            }

            case LOADING -> {
                if (state.transfers().isEmpty()) {
                    showLoading();
                } else {
                    showContent();
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            case CONTENT -> {
                transfers.clear();
                transfers.addAll(state.transfers());

                adapter.notifyDataSetChanged();

                showContent();
            }

            case EMPTY -> showError(
                    "No transfers are currently available."
            );

            case ERROR -> showError(
                    state.errorMessage()
            );
        }
    }

    private void showLoading() {

        loadingContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    private void showContent() {

        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {

        loadingContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);

        errorText.setText(message);
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
                != LOCAL_NETWORK_PERMISSION_REQUEST) {
            return;
        }

        if (grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {

            if (pendingPermissionAction != null) {
                pendingPermissionAction.run();
                pendingPermissionAction = null;
            }
        } else {

            showError(
                    "Local network access is required "
                            + "to connect to the development server."
            );
        }
    }
}