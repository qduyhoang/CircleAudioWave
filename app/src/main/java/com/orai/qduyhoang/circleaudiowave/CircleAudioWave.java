package com.orai.qduyhoang.circleaudiowave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class CircleAudioWave extends CircleAudioView {
    private static final int LINE_WIDTH = 2; // width of drawn lines
    private static final int LINE_SCALE = 60; // scales audio wave lengths
    private static final int MINIMUM_DISPLAY_HEIGHT = 10;
    private static final float AUDIO_WAVE_CHAOS = (float) 0.4;  //Range [0 - 1] : Chaotic - Peaceful


    private Paint linePaint; // specifies line drawing characteristics

    // constructor
    public CircleAudioWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.BLACK); // set color to black
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width

        audioWaveLengthScale = LINE_SCALE;
        minimumDisplayHeight = MINIMUM_DISPLAY_HEIGHT;
        audioWaveDirection = LEFT_TO_RIGHT;
        audioWaveWidthPadding = 0;
        audioWaveHeightPadding = 0;
        audioWaveChaos = AUDIO_WAVE_CHAOS;
    }


    // add the given amplitude and buffered data to the amplitudes LinkedList
    public void addAmplitude(float amplitude) {
        float scale;
        for (int i = 0; i <= 4; i++){
            scale = audioWaveChaos + random.nextFloat() * (1 - audioWaveChaos);
            amplitudes.add(amplitude * scale);
        }
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = (height / 2); // get the middle of the View
        int radius = (width / 2);
        float curX;
        if (audioWaveDirection == LEFT_TO_RIGHT){
            curX = width - audioWaveWidthPadding;
        } else {
            curX = audioWaveWidthPadding;
        }

        float power;

        // for each item in the amplitudes ArrayList
        if (amplitudes.size() >= 13){
            int amplitudes_size = amplitudes.size();
            float step = ((width - 2 * audioWaveWidthPadding) / amplitudes_size + 1); //Line width
//            linePaint.setColor(COLOR_LIST[random.nextInt(6)]); // set color to black
            for(int i=0; i < amplitudes_size; i++){
                if (i >= 5){    //Store the most recent 5 data points to smooth out lines
                    power = amplitudes.get(i-5);
                } else {
                    power = amplitudes.removeFirst();
                }
                float scaledHeight = (power / LINE_SCALE); // scale the power

                //Don't display any value less than the minimum display height - preventing noise
                if (scaledHeight >= minimumDisplayHeight){
                    float lengthCurXFromCenter = curX <= radius ? curX : width - curX;  // length of the current position with respect to the origin

                    float maxHeight =(2 * lengthCurXFromCenter * middle) / (radius) - 2 * audioWaveHeightPadding;   // Thales's theorem: max height from current position on x-axis
                    // to the point above it on the circle
                    scaledHeight = scaledHeight > maxHeight? maxHeight: scaledHeight;


                    // draw a line representing this item in the amplitudes
                    canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                            - scaledHeight / 2, linePaint);


                    if (audioWaveDirection == LEFT_TO_RIGHT) {
                        curX -= step;
                    } else {
                        curX += step;
                    }
                }
            }
        }
    }
}
