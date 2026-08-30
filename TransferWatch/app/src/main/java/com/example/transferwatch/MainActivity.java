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
    private final List<Team> teams = new ArrayList<>();

    private TeamAdapter teamAdapter;
    private TeamSearchViewModel teamSearchViewModel;

    private View searchScreen;
    private View transferScreen;
    private View teamSearchProgress;

    private TextView teamSearchMessage;
    private TextView selectedTeamText;

    private android.widget.EditText teamSearchInput;
    private RecyclerView teamRecyclerView;
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

        searchScreen = findViewById(R.id.searchScreen);

        transferScreen = findViewById(R.id.transferScreen);

        teamSearchInput = findViewById(R.id.teamSearchInput);

        Button teamSearchButton = findViewById(R.id.teamSearchButton);

        teamSearchProgress = findViewById(R.id.teamSearchProgress);

        teamSearchMessage = findViewById(R.id.teamSearchMessage);

        teamRecyclerView = findViewById(R.id.teamRecyclerView);

        teamAdapter = new TeamAdapter(teams, this::selectTeam);

        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        teamRecyclerView.setAdapter(teamAdapter);

        selectedTeamText = findViewById(R.id.selectedTeamText);

        Button backToSearchButton = findViewById(R.id.backToSearchButton);

        RecyclerView recyclerView = findViewById(R.id.transferRecyclerView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        loadingContainer = findViewById(R.id.loadingContainer);

        errorContainer = findViewById(R.id.errorContainer);

        errorText = findViewById(R.id.errorText);

        retryButton = findViewById(R.id.retryButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TransferAdapter(transfers);

        recyclerView.setAdapter(adapter);

        FootballRepository repository = new NetworkFootballRepository(ApiClient.getTransferApi());

        teamSearchViewModel = new ViewModelProvider(this, new TeamSearchViewModelFactory(repository)).get(TeamSearchViewModel.class);

        viewModel = new ViewModelProvider(this, new TransferViewModelFactory(repository)).get(TransferViewModel.class);

        viewModel.state().observe(this, this::render);

        teamSearchViewModel.state().observe(this, this::renderTeamSearch);

        viewModel.selectedTeam().observe(
                this,
                team -> {
                    if (team != null) {
                        showTransferScreen(team);
                    }
                }
        );


        swipeRefreshLayout.setOnRefreshListener(() -> runWithPermission(viewModel::refresh));

        retryButton.setOnClickListener(view -> runWithPermission(viewModel::refresh));

        teamSearchButton.setOnClickListener(
                view -> searchForTeam()
        );

        teamSearchInput.setOnEditorActionListener(
                (view, actionId, event) -> {
                    searchForTeam();
                    return true;
                }
        );

        backToSearchButton.setOnClickListener(
                view -> {
                    transferScreen.setVisibility(View.GONE);
                    searchScreen.setVisibility(View.VISIBLE);
                }
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {

                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                    return insets;
                }
        );
    }


    private void runWithPermission(Runnable action) {
        if (requestLocalNetworkPermissionIfNeeded()) {
            pendingPermissionAction = action;
        } else {
            action.run();
        }
    }


    private boolean requestLocalNetworkPermissionIfNeeded() {

        if (Build.VERSION.SDK_INT >= 37 && ContextCompat.checkSelfPermission(this, LOCAL_NETWORK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{LOCAL_NETWORK_PERMISSION}, LOCAL_NETWORK_PERMISSION_REQUEST);
            return true;
        }
        return false;
    }

    private void render(TransferScreenState state) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCAL_NETWORK_PERMISSION_REQUEST) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (pendingPermissionAction != null) {
                pendingPermissionAction.run();
                pendingPermissionAction = null;
            }
        } else {
            showError("Local network access is required " + "to connect to the development server.");
        }
    }

    private void searchForTeam() {

        String query = teamSearchInput.getText().toString();

        runWithPermission(
                () -> teamSearchViewModel.search(query)
        );
    }

    private void selectTeam(Team team) {

        runWithPermission(
                () -> viewModel.selectTeam(team)
        );
    }

    private void showTransferScreen(Team team) {
        searchScreen.setVisibility(View.GONE);
        transferScreen.setVisibility(View.VISIBLE);

        selectedTeamText.setText(team.name() + " transfers");
    }

    private void renderTeamSearch(TeamSearchState state) {
        teamSearchProgress.setVisibility(View.GONE);
        teamRecyclerView.setVisibility(View.GONE);
        teamSearchMessage.setVisibility(View.GONE);

        switch (state.status()) {

            case IDLE -> {
                teamSearchMessage.setText(
                        "Enter at least 3 characters"
                );
                teamSearchMessage.setVisibility(View.VISIBLE);
            }

            case LOADING ->
                    teamSearchProgress.setVisibility(
                            View.VISIBLE
                    );

            case RESULTS -> {
                teams.clear();
                teams.addAll(state.teams());

                teamAdapter.notifyDataSetChanged();

                teamRecyclerView.setVisibility(
                        View.VISIBLE
                );
            }

            case EMPTY -> {
                teamSearchMessage.setText(
                        "No teams found."
                );
                teamSearchMessage.setVisibility(
                        View.VISIBLE
                );
            }

            case ERROR -> {
                teamSearchMessage.setText(
                        state.errorMessage()
                );
                teamSearchMessage.setVisibility(
                        View.VISIBLE
                );
            }
        }
    }
}