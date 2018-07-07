package com.orai.qduyhoang.circleaudiowave;


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

public class CircleAudioWave extends View {
    private static final int LINE_WIDTH = 2; // width of drawn lines
    private static final int LINE_SCALE = 60; // scales line lengths
    private LinkedList<Float> amplitudes; // amplitudes for line lengths
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
    // this is called once when the activity starts
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View

        //We only need insert and delete operations
        //-> use a linked list to store amplitudes
        //Complexity (worst case): Insertion O(1)--Deletion O(1)
        amplitudes = new LinkedList<>();
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
        float curX = 0;
        float power;

        // for each item in the amplitudes ArrayList
        if (amplitudes.size() >= 13){
            int amplitudes_size = amplitudes.size();
//            linePaint.setColor(COLOR_LIST[random.nextInt(6)]); // set color to black
            for(int i=0; i < amplitudes_size; i++){
                if (i >= 5){    //Store the most recent 5 data points to smooth out lines
                    power = amplitudes.get(i-5);
                } else {
                    power = amplitudes.removeFirst();
                }
                float scaledHeight = (power / LINE_SCALE); // scale the power
                float lengthCurXFromCenter = curX <= radius ? curX : width - curX;  // length of the current position with respect to the origin

                float maxHeight = 2 * (lengthCurXFromCenter * middle) / (radius);   // Thales's theorem: max height from current position on x-axis
                                                                                    // to the point above it on the circle
                scaledHeight = scaledHeight > maxHeight? maxHeight: scaledHeight;

                // draw a line representing this item in the amplitudes ArrayLi
                canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                        - scaledHeight / 2, linePaint);

                curX += width / amplitudes_size;
            }
        }
    }
}