package com.example.soupturboflex;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ActionService {
    private static ActionService INSTANCE;
    private final TalApiService api;
    private final MutableLiveData<ActionCouple> actionCouple = new MutableLiveData<>();

    private ActionService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(TalApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(TalApiService.class);
    }

    public static ActionService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ActionService();
        }
        return INSTANCE;
    }

    public void getAction(String transcription) {
        Call<ArrayList<String>> call = api.getAction(transcription);
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                ArrayList<String> actionArray = response.body();
                String action = actionArray.get(0);
                String music = actionArray.size() > 1 ? actionArray.get(1) : null;
                actionCouple.postValue(new ActionCouple(action, music));
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                System.err.println("error : "+t.getMessage());
            }
        });
    }

    public MutableLiveData<ActionCouple> getActionCoupleMutableLiveData() {
        return this.actionCouple;
    }
}
