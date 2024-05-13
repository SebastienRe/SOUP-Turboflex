package com.example.soupturboflex;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AsrApiService {
    public static final String BASE_URL = "http://127.0.0.1:5000";
    @Multipart
    @POST("transcribe")
    Call<String> transcribe(@Part MultipartBody.Part audio);
}
