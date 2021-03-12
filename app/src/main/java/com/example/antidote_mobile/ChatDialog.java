package com.example.antidote_mobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;

import java.util.ArrayList;

public class ChatDialog extends Dialog implements View.OnClickListener {

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    Activity activity;
    EditText etMessage;
    ImageButton btSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;

    boolean mFirstLoad;

    public ChatDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        btSend.setOnClickListener(this);

        rvChat = findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;

        final String userId = AntidoteMobile.currentUser.getObjectId();

        mAdapter = new ChatAdapter(activity, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        refreshMessages();
    }

    @Override
    public void onClick(View v) {
        String data = etMessage.getText().toString();
        Message message = new Message();
        message.setBody(data);
        message.setUserId(AntidoteMobile.currentUser.getObjectId());
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

        query.orderByDescending("createdAt");

        query.findInBackground((messages, e) -> {
            if (e == null) {
                mMessages.clear();
                mMessages.addAll(messages);
                mAdapter.notifyDataSetChanged();
                if (mFirstLoad) {
                    rvChat.scrollToPosition(0);
                    mFirstLoad = false;
                }
            } else {
                System.out.println("Failed to refresh");
            }
        });
    }
}
