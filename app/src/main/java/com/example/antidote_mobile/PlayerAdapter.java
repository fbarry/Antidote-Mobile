package com.example.antidote_mobile;

import android.app.Activity;
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
        public Button difficultyButton;
        public Player player;
        public int difficulty;

        public PlayerViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.gameName);
            kickButton = itemView.findViewById(R.id.joinButton);
            difficultyButton = itemView.findViewById(R.id.difficultyButton);
        }
    }

    private Game game;
    private final boolean isHost;
    private final Activity activity;
    private ArrayList<Player> players;

    public PlayerAdapter(Activity activity, Game game, boolean isHost) {
        this.activity = activity;
        this.game = game;
        this.isHost = isHost;
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
        notifyItemInserted(players.size() - 1);
    }

    public void removePlayer(Player player) {
        int index = players.indexOf(player);
        players.remove(player);
        notifyItemRemoved(index);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setGame(Game g) {
        game = g;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = new ArrayList<>(players);
    }

    @Override
    public int getItemViewType(int position) {
        return players.get(position).isAI() ? 1 : 0;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View playerItemView = null;
        if (viewType == 0) {
            playerItemView = inflater.inflate(R.layout.player_item, parent, false);
        } else if (viewType == 1) {
            playerItemView = inflater.inflate(R.layout.player_ai_item, parent, false);
        }

        return new PlayerViewHolder(playerItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player currPlayer = players.get(position);

        holder.player = currPlayer;
        holder.username.setText(currPlayer.username());

        if (currPlayer.isAI()) {
            switch (currPlayer.getDifficulty()) {
                case 0:
                    holder.difficultyButton.setText(R.string.easy);
                    break;
                case 1:
                    holder.difficultyButton.setText(R.string.medium);
                    break;
                case 2:
                    holder.difficultyButton.setText(R.string.hard);
                    break;
                default:
            }
        }

        if (!isHost || currPlayer.isHost()) {
            holder.kickButton.setVisibility(View.GONE);
            if (holder.difficultyButton != null) {
                holder.difficultyButton.setClickable(false);
            }
        } else {
            holder.kickButton.setOnClickListener(v -> Utilities.showConfirmationAlert(activity,
                    "Kick " + currPlayer.username() + "?",
                    "The player will need to rejoin",
                    (dialog, which) -> game.removePlayer(currPlayer.getObjectId())));
            if (holder.difficultyButton != null) {
                holder.difficultyButton.setOnClickListener(v -> {
                    holder.difficulty = (holder.difficulty + 1) % PlayerAI.numDifficulties;

                    switch (holder.difficulty) {
                        case 0:
                            holder.difficultyButton.setText(R.string.easy);
                            break;
                        case 1:
                            holder.difficultyButton.setText(R.string.medium);
                            break;
                        case 2:
                            holder.difficultyButton.setText(R.string.hard);
                            break;
                        default:
                    }

                    holder.player.setDifficulty(holder.difficulty);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
