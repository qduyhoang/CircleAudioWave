# Circle Audio Wave - Audio Wave Visualizer for Android

## Getting started

### Add data
```java
addAmplitude(float amplitude)
```
For every datapoint, it generates 4 other "fake" datapoints. The degree of similarities between generated datapoints and the original datapoint is determined by [setAudioWaveChaos()](#change-audio-waves-chaos)

### Change the direction of the audio wave
```java
setAudioWaveDirection(int direction)
```
There are two possible inputs: LEFT_TO_RIGHT( = 0) or RIGHT_TO_LEFT ( = 1)

Default value: LEFT_TO_RIGHT

### Set audio wave paddings
```java
setAudioWavePadding(int width, int height)
```
Audio waves are already constrained by the dimensions of view. This method offers a way to better fit background images and drawable of all shapes.

Default value: width = 0, height = 0

### Set minimum height of audio wave (dp) to display on View
```java
setMinimumDisplayHeight(int minimum)
```
Any audio wave with height smaller than the specified value will not be displayed. Good for noise cancelling.

Default value: 10

### Change audio waves' chaos
```java
setAudioWaveChaos(float chaos)
```
Degree of chaos of the audio waves. Range [0 - 1] : Chaotic - Peaceful

Default value: 0.4

### Scale audio wave lengths
```java
setAudioWaveLengthScale(int scale)
```
Scale the length of the audio wave. The larger the scale, the smaller the length.

Default value: 60

### Change paint style
```java
getPaint()
```
Return a Paint object that you could use to change its style


### Working Example
#### Defining the view in XML
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="180dp"
    android:layout_alignParentBottom="true">


    <com.orai.qduyhoang.sample.CircleAudioWave
        android:id="@+id/record_button"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_centerHorizontal="true"
        android:background="@drawable/my_background_drawable"
        android:layout_margin="5dp" />

    <TextView
        android:id="@+id/record_status"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="25dp"
        android:gravity="center"
        android:text="Start Recording"
        android:textColor="@android:color/black"
        android:textSize="30sp" />

</RelativeLayout>
```

#### Make a directory to store our recordings. Let's call it "/recordings
```java
String OUTPUT_DIRECTORY = "/recordings"
File outputDir = new File(Environment.getExternalStorageDirectory(),
                OUTPUT_DIRECTORY);
if (!outputDir.exists()) {
    outputDir.mkdirs();
}
```

#### Don't forget to declare permissions to record and store files in AndroidManifest and ask for permissions in-app

AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

MainActivity.java
```java
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
```

#### Let's create and set an OnClickListener for our record button
```java
CircleAudioWave recordButton = findViewById(R.id.record_button)
recordButton.setOnClickListener(recordClick)

OnClickListener recordClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!isRecording) {

                recordStatus.setText("Stop Recording");

                //Setting up the recorder
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
```

#### Now create a thread to collect the data every 50 milliseconds
```java
Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording)                          // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                circleAudioWave.addAmplitude(x);      // call method addAmplitude() to add data to our view
                circleAudioWave.invalidate();         // refresh our view

                // update in 50 milliseconds
                handler.postDelayed(this, 50);
            }
        }
    };
 ```
 
 #### As a last step, we need to clear data and remove the worker thread when we finish recording
 ```java
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
```


