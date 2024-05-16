package com.example.soupturboflex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private Button recordButton;
    private ProgressBar progressBar;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private VoiceCommandService voiceCommandService;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    /**
     * Reason for SuppressLint : I am cold-hearted and don't care about making the world a
     * better place for blind people.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        voiceCommandService = VoiceCommandService.getInstance(this);

        if (!checkPermissions()) throw new RuntimeException("Permissions not granted");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        outputFile = getExternalCacheDir().getAbsolutePath() + "/recording.mp4";

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recordButton = findViewById(R.id.record_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        recordButton.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                System.out.println("Recording...");
                startRecording();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                System.out.println("Recording stopped");
                recordButton.setEnabled(false);
                stopRecording();
                playRecording();
                voiceCommandService.executeCommand(outputFile);
                recordButton.setEnabled(true);
            }
            return true;
        });

        voiceCommandService.getIsLoadingMutableLiveData().observeForever(isLoading -> {
            System.out.println("isLoading : "+isLoading);
            if(isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkPermissions() {
        //cette fonction regarde si les permissions sont accordées et les demande si elles ne le sont pas, et retourne true si elles sont toutes accordées
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 1); // demande les permissions
            System.out.println(permissionsToRequest);
            return false; // au moins une permission n'est pas accordée
        }
        return true; // toutes les permissions sont accordées
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(outputFile); // Set the file to save the recording
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void playRecording() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}