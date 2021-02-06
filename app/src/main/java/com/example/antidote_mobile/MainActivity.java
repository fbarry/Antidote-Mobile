package com.example.antidote_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    String BASE_URL = "https://antidote-mobile.herokuapp.com/";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.client(httpClient.build()).build();

    ProfileService profileService = retrofit.create(ProfileService.class);

    public void viewProfiles(View v) {
        TextView textView = (TextView) findViewById(R.id.textView);
        Call<List<Profile>> createCall = profileService.all();
        createCall.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                List<Profile> profiles = response.body();
                if (profiles == null) {
                    textView.setText(response.errorBody().toString());
                } else {
                    textView.setText("All Profiles:\n");
                    for (Profile p : profiles) {
                        textView.append(p.getUsername() + "\n");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                t.printStackTrace();
                textView.setText(t.getMessage());
            }
        });
    }

    public void addProfile(View v) {
        TextView textView = (TextView) findViewById(R.id.textView);
        Profile profile = new Profile("randomUser", "randomPassword");
        Call<Profile> createCall = profileService.create(profile);
        createCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile newProfile = response.body();
                textView.setText("Created");
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
                textView.setText(t.getMessage());
            }
        });
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }


}