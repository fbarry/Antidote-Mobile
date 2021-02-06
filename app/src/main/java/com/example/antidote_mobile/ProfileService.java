package com.example.antidote_mobile;

import android.provider.ContactsContract;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProfileService {
    @GET("profiles")
    Call<List<Profile>> all();

    @GET("profiles/{username}")
    Call<Profile> get(@Path("username") String username);

    @POST("profiles/new")
    Call<Profile> create(@Body Profile profile);
}
