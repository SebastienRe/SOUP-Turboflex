package com.example.soupturboflex;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class VoiceCommandService {
    private static VoiceCommandService INSTANCE;
    private final TranscriptionService transcriptionService = TranscriptionService.getInstance();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> transcription = new MutableLiveData<>();

    private VoiceCommandService() {
        observeTranscription();
        isLoading.postValue(false);
    }

    public static VoiceCommandService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new VoiceCommandService();
        }
        return INSTANCE;
    }

    public void executeCommand(String audioFileName) {
        isLoading.postValue(true);
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                transcription.postValue(s);
                isLoading.postValue(false);
                transcriptionService.getTranscriptionMutableLiveData().removeObserver(this);
            }
        };

        transcriptionService.getTranscriptionMutableLiveData().observeForever(observer);
        transcriptionService.transcribe(audioFileName);
    }

    public MutableLiveData<Boolean> getIsLoadingMutableLiveData() {
        return isLoading;
    }

    private void observeTranscription() {
        transcription.observeForever(newTranscription -> {
            System.out.println("transcription : " + newTranscription);
            // Send request to TAL
        });
    }
}
