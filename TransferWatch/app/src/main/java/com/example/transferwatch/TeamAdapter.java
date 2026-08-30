package com.example.transferwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeamAdapter
        extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    private final List<Team> teams;
    private final OnTeamClickListener listener;

    public TeamAdapter(
            List<Team> teams,
            OnTeamClickListener listener
    ) {
        this.teams = teams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_team,
                                parent,
                                false
                        );

        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TeamViewHolder holder,
            int position
    ) {
        Team team = teams.get(position);

        holder.teamNameText.setText(
                team.name()
        );

        holder.itemView.setOnClickListener(
                view -> listener.onTeamClick(team)
        );
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class TeamViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView teamNameText;

        TeamViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            teamNameText =
                    itemView.findViewById(
                            R.id.teamNameText
                    );
        }
    }
}