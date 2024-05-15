package com.example.soupturboflex;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import com.example.soupturboflex.SOUP.PlayerPrx;

public class StreamingService {
    private static StreamingService INSTANCE;
    private final PlayerPrx player;
    private final MutableLiveData<String> musicName = new MutableLiveData<>();
    private Thread playerThread;
    private Context context;
    private String currentStreamingUrl;

    private StreamingService() {
        this.player = IceService.getPlayer();
    }

    public static StreamingService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new StreamingService();
        }
        return INSTANCE;
    }

    public void executeActionCouple(ActionCouple actionCouple, Context context) {
        this.context = context;
        System.out.println("Execution of following action couple : " + actionCouple.action + " sur " + actionCouple.music);

        switch (actionCouple.action) {
            case "jouer":
                if(actionCouple.music == null) {
                    System.err.println("No music name found.");
                    break;
                }
                player.stop();
                String currentMusicFileName = player.getMusicFileNameForMusicName(actionCouple.music);
                currentStreamingUrl = player.setMusic(currentMusicFileName);
                System.out.println("Streaming URL : " + currentStreamingUrl);
                musicName.postValue(actionCouple.music);
                player.play();
                play(currentStreamingUrl, context);
                break;
            case "pause":
                player.pause();
                playerThread.interrupt();
                break;
            case "relance":
                player.play();
                play(currentStreamingUrl, context);
                break;
            case "baisser":
                player.volumeDown();
                break;
            case "augmenter":
                player.volumeUp();
                break;
            default:
                System.err.println("Action '" + actionCouple.action + "' is not supported.");
        }
    }

    public MutableLiveData<String> getMusicNameMutableLiveDate() {
        return this.musicName;
    }

    private void play(String currentStreamingUrl, Context context) {
        if(playerThread != null) {
            playerThread.interrupt();
        }
        playerThread = new Thread(() -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            Uri uri = Uri.parse(currentStreamingUrl);
            try {
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                System.err.println("Error setting music : " + e.getMessage());
                mediaPlayer.release();
                return;
            }
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
        });
        playerThread.start();
    }
}
