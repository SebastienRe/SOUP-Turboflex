package com.example.soupturboflex;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TalApiService {
    public static final String BASE_URL = "http://127.0.0.1:5001";

    @GET("{transcription}")
    Call<ArrayList<String>> getAction(@Path("transcription") String transcription);
}
