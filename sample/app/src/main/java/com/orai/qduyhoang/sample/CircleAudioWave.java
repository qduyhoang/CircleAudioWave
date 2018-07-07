package com.orai.qduyhoang.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CircleAudioWave extends View {
    private static final int LINE_WIDTH = 2; // width of visualizer lines
    private static final int LINE_SCALE = 60; // scales visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics
    private int[]COLOR_LIST = {Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.DKGRAY, Color.BLUE, Color.BLACK };
    private Random random;

    // constructor
    public CircleAudioWave(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.BLACK); // set color to black
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
        linePaint.setFakeBoldText(true);
        random = new Random();
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
        amplitudes = new ArrayList<Float>(width / LINE_WIDTH);
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        // add newest and buffered data to the amplitudes ArrayList
        amplitudes.add((float) (amplitude*0.8));
        amplitudes.add((float) (amplitude*0.9));
        amplitudes.add(amplitude);
        amplitudes.add((float) (amplitude*0.9));
        amplitudes.add((float) (amplitude*0.8));
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        int radius = width / 2;
        float curX = 0; // start curX at zero

        // for each item in the amplitudes ArrayList
        if (amplitudes.size() > 10){
            //  linePaint.setColor(COLOR_LIST[random.nextInt(6)]); // set color to black
            for(int i=0; i<amplitudes.size(); i++){
                float power = amplitudes.get(i);
                float scaledHeight = (power / LINE_SCALE); // scale the power
                float lengthCurXFromCenter = curX <= radius ? curX : width - curX;
                float maxHeight = 2 * (lengthCurXFromCenter * middle) / (radius);

                scaledHeight = scaledHeight > maxHeight? maxHeight: scaledHeight;

                curX += this.width / amplitudes.size();

                // draw a line representing this item in the amplitudes ArrayList
                canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                        - scaledHeight / 2, linePaint);
            }
            amplitudes.remove(0);
            amplitudes.remove(1);
            amplitudes.remove(2);
            amplitudes.remove(3);
            amplitudes.remove(4);
        }
    }

}