package com.example.antidote_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public Button kickButton;

        public PlayerViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.playerName);
            kickButton = itemView.findViewById(R.id.kickButton);
        }
    }

    private boolean isHost;
    private ArrayList<Player> players;

    public PlayerAdapter(boolean isHost) {
        this.isHost = isHost;
        players = new ArrayList<>();
    }

    public void setPlayers(ArrayList<Player> playerList) {
        players = playerList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View playerItemView = inflater.inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(playerItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player currPlayer = players.get(position);

        holder.username.setText(currPlayer.username());
        if (!isHost || currPlayer.isHost()) {
            holder.kickButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
