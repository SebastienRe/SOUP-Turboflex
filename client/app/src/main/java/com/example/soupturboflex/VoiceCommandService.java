package com.example.soupturboflex;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoiceCommandService {
    private static VoiceCommandService INSTANCE;
    private final TranscriptionService transcriptionService = TranscriptionService.getInstance();
    private final ActionService actionService = ActionService.getInstance();
    private final StreamingService streamingService = StreamingService.getInstance();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private Context context;

    private VoiceCommandService(Context context) {
        isLoading.postValue(false);
        this.context = context;
    }

    public static VoiceCommandService getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new VoiceCommandService(context);
        }
        return INSTANCE;
    }

    public void executeCommand(String audioFileName) {
        isLoading.postValue(true);

        transcriptionService.transcribe(audioFileName, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String transcription = response.body();
                System.out.println("Transcription : " + transcription);
                actionService.getAction(transcription, new Callback<ArrayList<String>>() {
                    @Override
                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                        ArrayList<String> actionList = response.body();
                        String action = actionList.get(0);
                        String music = actionList.size() > 1 ? actionList.get(1) : null;
                        ActionCouple actionCouple = new ActionCouple(action, music);
                        System.out.println("Action : " + actionCouple.action + " sur " + actionCouple.music);
                        streamingService.executeActionCouple(actionCouple, context);
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                        System.err.println("Action retrieval error : " + t.getMessage());
                        isLoading.postValue(false);
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.err.println("Transcription error : " + t.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MutableLiveData<Boolean> getIsLoadingMutableLiveData() {
        return isLoading;
    }
}
