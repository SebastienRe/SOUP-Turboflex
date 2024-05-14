package com.example.soupturboflex;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TranscriptionService {
    private static TranscriptionService INSTANCE;
    private final AsrApiService api;
    private final MutableLiveData<String> transcription = new MutableLiveData<>();

    private TranscriptionService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(AsrApiService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(AsrApiService.class);
    }

    public static TranscriptionService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TranscriptionService();
        }
        return INSTANCE;
    }

    public void transcribe(String filename) {

        /*
        File file = new File(filename);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "audio",
                file.getName(),
                RequestBody.create(MediaType.parse("audio/*"), file)
        );

        Call<String> call = api.transcribe(filePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                transcription.postValue(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.err.println("error : "+t.getMessage());
                transcription.postValue(null);
            }
        });*/

        transcription.postValue("Joue la Vache");
    }

    public MutableLiveData<String> getTranscriptionMutableLiveData() {
        return this.transcription;
    }
}
