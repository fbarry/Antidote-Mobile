package com.example.antidote_mobile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ChatDialog extends Dialog implements View.OnClickListener {

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    LobbyActivity activity;
    String gameId, username;

    EditText etMessage;
    ImageButton btSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;

    boolean mFirstLoad;

    public ChatDialog(LobbyActivity activity, String gameId, String currentUsername) {
        super(activity);
        this.activity = activity;
        this.gameId = gameId;
        this.username = currentUsername;
        mMessages = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.etMessage);
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onClick(etMessage);
                handled = true;
            }
            return handled;
        });

        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(this);

        rvChat = findViewById(R.id.rvChat);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(getContext(), username, mMessages);
        rvChat.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        refreshMessages();
    }

    @Override
    public void onClick(View v) {
        String data = etMessage.getText().toString();

        if (data.length() == 0) return;

        Message message = new Message();
        message.setBody(data);
        message.setUsername(username);
        message.setGame(gameId);

        message.saveInBackground(e -> {
            if(e == null) {
                System.out.println("Successfully posted chat.");
                refreshMessages();
            } else {
                System.out.println("MESSAGE FAILED TO SEND");
            }
        });
        etMessage.setText(null);
    }

    void refreshMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.whereEqualTo(Message.GAME_KEY, gameId);
        query.orderByDescending("createdAt");

        query.findInBackground((messages, e) -> {
            if (e == null) {
                if (newMessages(messages)) {
                    activity.showChatNotification();
                }
            } else {
                System.out.println("Failed to refresh");
            }
        });
    }

    public boolean newMessages(List<Message> messages) {

        if (mMessages.containsAll(messages)) return false;

        mMessages.clear();
        mMessages.addAll(messages);
        mAdapter.notifyDataSetChanged();
        if (mFirstLoad) {
            rvChat.scrollToPosition(0);
            mFirstLoad = false;
        }

        return true;
    }
}
