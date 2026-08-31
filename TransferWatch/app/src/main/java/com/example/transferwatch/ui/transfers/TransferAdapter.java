package com.example.transferwatch.ui.transfers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transferwatch.R;
import com.example.transferwatch.domain.model.Transfer;

import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> {

    private final List<Transfer> transfers;

    public TransferAdapter(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    @NonNull
    @Override
    public TransferViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_transfer,
                        parent,
                        false
                );

        return new TransferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TransferViewHolder holder,
            int position
    ) {

        Transfer transfer = transfers.get(position);

        holder.playerNameText.setText(
                transfer.playerName()
        );

        holder.clubsText.setText(
                transfer.fromClub()
                        + "  →  "
                        + transfer.toClub()
        );

        holder.transferTypeText.setText(
                transfer.transferType()
        );

        holder.dateText.setText(
                transfer.date()
        );
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    static class TransferViewHolder
            extends RecyclerView.ViewHolder {

        TextView playerNameText;
        TextView clubsText;
        TextView transferTypeText;
        TextView dateText;

        public TransferViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            playerNameText =
                    itemView.findViewById(
                            R.id.itemPlayerNameText
                    );

            clubsText =
                    itemView.findViewById(
                            R.id.itemClubsText
                    );

            transferTypeText =
                    itemView.findViewById(
                            R.id.itemTransferTypeText
                    );

            dateText =
                    itemView.findViewById(
                            R.id.itemDateText
                    );
        }
    }
}

