package com.orai.qduyhoang.sample;
import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static final int REPEAT_INTERVAL = 50;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 2;
    public static final String OUTPUT_DIRECTORY = "/Recordings";
    public String outputDirPath;
    private TextView recordStatus;

    CircleAudioSpectrum circleAudioWave;

    private MediaRecorder recorder = null;

    File outputDir;
    private boolean isRecording = false;


    private Handler handler; // Handler for updating the visualizer
    // private boolean recording; // are we currently recording?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleAudioWave = findViewById(R.id.record_button);
        circleAudioWave.setOnClickListener(recordClick);
        circleAudioWave.setAudioWavePadding(50, 40);


        recordStatus = findViewById(R.id.record_status);

        outputDir = new File(Environment.getExternalStorageDirectory(),
                OUTPUT_DIRECTORY);
        outputDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + OUTPUT_DIRECTORY;
        if (outputDir.exists()) {
            deleteFilesInDir(outputDir);
        } else {
            outputDir.mkdirs();
        }

        // create the Handler for record button update
        handler = new Handler();

        // Request permission to record audio
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        // Request permission to write files to external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }
    }

    OnClickListener recordClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!isRecording) {

                recordStatus.setText("Stop Recording");

                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(outputDirPath + "/audio_file"
                        + ".mp3");


                try {
                    recorder.prepare();
                    recorder.start();
                    isRecording = true; // we are currently recording
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(updateVisualizer);

            } else {
                recordStatus.setText("Start Recording");
                releaseRecorder();
            }

        }
    };

    private void releaseRecorder() {
        if (recorder != null) {
            isRecording = false; // stop recording
            handler.removeCallbacks(updateVisualizer);
            circleAudioWave.clear();
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    public static boolean deleteFilesInDir(File path) {

        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {

                if(files[i].isDirectory()) {

                }
                else {
                    files[i].delete();
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        releaseRecorder();
    }

    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                circleAudioWave.addAmplitude(x); // update view
                circleAudioWave.invalidate(); // refresh view

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };


}
