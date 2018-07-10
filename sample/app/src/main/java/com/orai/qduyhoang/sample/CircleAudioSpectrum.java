package com.orai.qduyhoang.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

public class CircleAudioSpectrum extends CircleAudioWave {
    private Random random;
    private int radius;
    private Paint circlePaint;
    private Paint linePaint;
    private float[] points;

    private static final float AUDIO_WAVE_CHAOS = (float) 0.4;  //Range [0 - 1] : Chaotic - Peaceful

    public CircleAudioSpectrum(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint = new Paint(); // create Paint for lines
        circlePaint.setColor(Color.WHITE); // set color to black
        circlePaint.setStrokeWidth(1); // set stroke width
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.BLACK); // set color to black
        linePaint.setStrokeWidth(1); // set stroke width

        audioWaveChaos = AUDIO_WAVE_CHAOS;
    }


    @Override
    // add the given amplitude and buffered data to the amplitudes LinkedList
    public void addAmplitude(float amplitude) {
        float scale;
        for (int i = 0; i < 120; i++){
            //random scale in range [audioWaveChaos, 1]
            scale = audioWaveChaos + random.nextFloat() * (1 - audioWaveChaos);
            amplitudes.add(amplitude * scale);
        }
    }

    // draw the audio wave with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        if (radius == -1) {
            radius = (int) (getHeight() * 0.65 / 2);
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, circlePaint);
        if (amplitudes.size() > 120) {
            double angle = 0;
            int amplitude_size = amplitudes.size();
            if (points == null || points.length < amplitude_size * 4) {
                points = new float[amplitude_size * 4];
            }

            for (int i = 0; i < 120; i++, angle += 3) {
                double t = Math.log(amplitudes.removeFirst() / 70) * 2;

                if (i > 30 && i < 60){
                    t = t * 3;
                } else if ( i > 90){
                    t = t * 3;
                }
                points[i * 4] = (float) (getWidth() / 2
                        + radius
                        * Math.cos(Math.toRadians(angle)));

                points[i * 4 + 1] = (float) (getHeight() / 2
                        + radius
                        * Math.sin(Math.toRadians(angle)));

                points[i * 4 + 2] = (float) (getWidth() / 2
                        + (radius + t)
                        * Math.cos(Math.toRadians(angle)));

                points[i * 4 + 3] = (float) (getHeight() / 2
                        + (radius + t)
                        * Math.sin(Math.toRadians(angle)));
            }

            canvas.drawLines(points, linePaint);
        }
        super.onDraw(canvas);
    }
}