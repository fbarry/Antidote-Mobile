package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        public Button joinButton;
        public TextView gameName;
        public Game game;

        public GameViewHolder(View itemView) {
            super(itemView);

            gameName = itemView.findViewById(R.id.gameName);
            joinButton = itemView.findViewById(R.id.joinButton);
        }
    }

    private final ArrayList<Game> games;
    private final Activity activity;
    private final Dialog dialog;

    public GameAdapter(Activity activity, Dialog dialog) {
        this.activity = activity;
        games = new ArrayList<>();
        this.dialog = dialog;
    }

    public void addGame(Game game) {
        games.add(game);
        notifyItemInserted(games.size() - 1);
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View gameItemView = inflater.inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(gameItemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game currGame = games.get(position);

        holder.game = currGame;
        holder.gameName.setText(String.format("%s(%d)", currGame.roomCode(), currGame.numPlayers()));

        holder.joinButton.setOnClickListener(v -> {
            ((MainActivity) activity).joinGame(currGame.roomCode());
            dialog.dismiss();
        });

    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
