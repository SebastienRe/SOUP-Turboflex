package com.example.soupturboflex;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import Soup.Song;

public class VoiceCommandService {
    private static VoiceCommandService INSTANCE;
    private final TranscriptionService transcriptionService = TranscriptionService.getInstance();
    private final ActionService actionService = ActionService.getInstance();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> transcription = new MutableLiveData<>();

    private MediaPlayer mediaPlayer;

    private Context context;

    private VoiceCommandService(Context contextp) {
        observeTranscription();
        isLoading.postValue(false);
        context = contextp;
    }

    public static VoiceCommandService getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new VoiceCommandService(context);
        }
        return INSTANCE;
    }

    public void executeCommand(String audioFileName) {
        isLoading.postValue(true);

        Observer<ActionCouple> actionCoupleObserver = new Observer<ActionCouple>() {
            @Override
            public void onChanged(ActionCouple actionCouple) {
                System.out.println(actionCouple.action + " on " + actionCouple.music);
                List<Song> songs = IceService.getSongs(); // Get songs from server
                Song song = null; // Find the song to play
                if (actionCouple.music != null) {
                    for (Song s : songs) {
                        if (s.title.equals(actionCouple.music)) {
                            song = s;
                            break;
                        }
                    }
                }
                System.out.println("Song : " + song + " " + actionCouple.music+ " " + actionCouple.action);
                switch (actionCouple.action) {
                    case "jouer":
                        int port = IceService.play(actionCouple, song);
                        System.out.println("Port : " + port);
                        if (port == -1) {
                            System.out.println("Song not found or action not found");
                            break;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer = new MediaPlayer();
                                try {
                                    System.out.println("setDataSource");
                                    Uri uri = Uri.parse("http://127.0.0.1:" + port);
                                    mediaPlayer.setDataSource(context, uri);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    mediaPlayer.release();
                                    throw new RuntimeException(e);
                                }
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mediaPlayer.release();
                                    }
                                });
                            }
                        }).start();
                        break;
                    case "pause":
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        break;
                    case "relance":
                        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                        break;
                    case "baisser":
                        if (mediaPlayer != null)
                            mediaPlayer.setVolume(0.5f, 0.5f);
                        break;
                    case "augmenter":
                        if (mediaPlayer != null)
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        break;
                    default:
                        System.out.println("Action not found");
                }
                isLoading.postValue(false);
                actionService.getActionCoupleMutableLiveData().removeObserver(this);
            }
        };

        Observer<String> transcriptionObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                transcription.postValue(s);
                actionService.getActionCoupleMutableLiveData().observeForever(actionCoupleObserver);
                actionService.getAction(s);
                transcriptionService.getTranscriptionMutableLiveData().removeObserver(this);
            }
        };

        transcriptionService.getTranscriptionMutableLiveData().observeForever(transcriptionObserver);
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
