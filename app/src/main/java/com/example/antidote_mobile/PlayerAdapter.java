package com.example.antidote_mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public Button kickButton;
        public Player player;

        public PlayerViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.playerName);
            kickButton = itemView.findViewById(R.id.kickButton);
        }
    }

    private Game game;
    private final boolean isHost;
    private final ArrayList<Player> players;

    public PlayerAdapter(Game game, boolean isHost) {
        this.game = game;
        this.isHost = isHost;
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
        notifyItemInserted(players.size()-1);
    }

    public void removePlayer(Player player) {
        int index = players.indexOf(player);
        players.remove(player);
        notifyItemRemoved(index);
    }

    public ArrayList<Player> getPlayers() { return players; }

    public void setGame(Game g) { game = g; }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View playerItemView = inflater.inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(playerItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player currPlayer = players.get(position);

        holder.player = currPlayer;
        holder.username.setText(currPlayer.username());
        if (!isHost || currPlayer.isHost()) {
            holder.kickButton.setVisibility(View.GONE);
        } else {
            holder.kickButton.setOnClickListener(v -> game.removePlayer(currPlayer.getObjectId()));
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
