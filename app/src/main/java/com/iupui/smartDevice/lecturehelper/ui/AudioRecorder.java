package com.iupui.smartDevice.lecturehelper.ui;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iupui.smartDevice.lecturehelper.R;
import com.iupui.smartDevice.lecturehelper.utils.LectureHelperUtils;
import java.io.File;
import java.io.IOException;

/**
 * Class to perform the following functionalities on MediaPlayer
 * 1. Record Audio
 * 2. Play Audio
 * 3. Stop Audio
 * 4. Save Audio to SD card
 *
 Made by: Divya Maridi
 */

public class AudioRecorder extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private Button recordButton, stopButton, playButton, stopPlayButton, saveButton;
    private static final String THREE_GPP = ".3gp";
    private static final String TEMP_FILE_NAME = "audio";
    private LectureApplication mLectureApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        initializeObjects();
        initializeId();

        recordButton.setEnabled(true);
        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        stopPlayButton.setEnabled(false);
        saveButton.setEnabled(false);

        outputFile = getCachePath();
    }

    private void initializeObjects() {
        mLectureApplication = (LectureApplication) getApplication();
    }

    private void initializeId() {
        recordButton = (Button) findViewById(R.id.audio_button1);
        stopButton = (Button) findViewById(R.id.audio_button2);
        playButton = (Button) findViewById(R.id.audio_button3);
        stopPlayButton = (Button) findViewById(R.id.audio_button4);
        saveButton = (Button) findViewById(R.id.audio_button5);
    }

    private String getCachePath() {
        String cachePath = getCacheDir().getAbsolutePath();

        try {
            //Creating a temporary file in cache, where recorded audio will be stored while recording
            File.createTempFile(TEMP_FILE_NAME, THREE_GPP, new File(cachePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cachePath + "/" + TEMP_FILE_NAME + THREE_GPP;
    }

    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.audio_button1:
                try {
                    startRecording();

                    recordButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    playButton.setEnabled(false);
                    stopPlayButton.setEnabled(false);
                    saveButton.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.audio_button2:
                try {
                    stopRecording();

                    recordButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    playButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                    saveButton.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.audio_button3:
                try {
                    startPlaying();

                    recordButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    playButton.setEnabled(false);
                    stopPlayButton.setEnabled(true);
                    saveButton.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.audio_button4:
                try {
                    stopPlaying();
                    recordButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    playButton.setEnabled(true);
                    stopPlayButton.setEnabled(false);
                    saveButton.setEnabled(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.audio_button5:

                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                stopPlayButton.setEnabled(false);
                saveButton.setEnabled(false);

                if (LectureHelperUtils.copyToStorage(new File(outputFile),
                    mLectureApplication.getCurrentCourseName(), THREE_GPP)) {
                    Toast.makeText(getApplicationContext(), "Audio has been saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Audio has not been saved", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void startRecording() {
        if (recorder != null)
            recorder.release();
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(outputFile);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
        }
    }

    private void startPlaying() throws Exception {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(outputFile);
        mediaPlayer.prepare();
        mediaPlayer.start();

        //Listener which calls onCompletion() to perform UI actions, once the audio file has been completed playing
        mediaPlayer.setOnCompletionListener(this);
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlayButton.setEnabled(false);
        recordButton.setEnabled(true);
        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        saveButton.setEnabled(true);
    }
}
