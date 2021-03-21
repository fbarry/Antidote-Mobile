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

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder> {

    public static class UserSearchViewHolder extends RecyclerView.ViewHolder {
        public User user;
        public Button username;

        public UserSearchViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
        }
    }

    Activity activity;
    ArrayList<User> users;

    public UserSearchAdapter(Activity activity) {
        this.activity = activity;
        users = new ArrayList<>();
        refresh("");
    }

    public void refresh(String text) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", text);

        try {
            List<ParseUser> list = query.find();
            this.users.clear();
            for (ParseUser u : list) this.users.add((User) u);
            notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public UserSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View friendItemView = inflater.inflate(R.layout.basic_button_item, parent, false);
        return new UserSearchViewHolder(friendItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchViewHolder holder, int position) {
        User currFriend = users.get(position);
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
        return users.size();
    }
}
