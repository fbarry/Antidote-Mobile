package com.example.antidote_mobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public User user;
        public Button username;

        public FriendViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
        }
    }

    ArrayList<User> friends;
    Activity activity;

    public FriendAdapter(Activity activity, ArrayList<String> friends) {
        this.activity = activity;
        this.friends = new ArrayList<>();

        refresh(friends);
    }

    public void refresh(ArrayList<String> friends) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", friends);

        try {
            List<ParseUser> list = query.find();
            this.friends.clear();
            for (ParseUser u : list) this.friends.add((User) u);
            notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View friendItemView = inflater.inflate(R.layout.basic_button_item, parent, false);
        return new FriendViewHolder(friendItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User currFriend = friends.get(position);
        holder.username.setText(currFriend.getUsername());
        holder.user = currFriend;
        holder.username.setOnClickListener(v -> {
            if (activity instanceof RedirectToProfile) {
                ((RedirectToProfile) activity).goToProfile(currFriend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
