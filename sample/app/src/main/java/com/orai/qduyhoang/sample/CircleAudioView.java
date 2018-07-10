package com.orai.qduyhoang.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

public class CircleAudioView extends View {

    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;


    protected LinkedList<Float> amplitudes; // amplitudes for line lengths
    protected int width; // width of this View
    protected int height; // height of this View
    protected Paint linePaint; // specifies line drawing characteristics

    protected Random random;

    protected int audioWaveLengthScale;      // scales audio wave lengths
    protected int minimumDisplayHeight;  //Minimum height of audio wave (dP) to display on View
    protected int audioWaveDirection;   //Direction of the audio wave (left-right or right-left)
    protected int audioWaveWidthPadding;   //Padding of the audio wave inside being drawn inside view
    protected int audioWaveHeightPadding;   //Padding of the audio wave inside being drawn inside view
    protected float audioWaveChaos;         //Degree of chaos of the audio waves. Range [0 - 1] : Chaotic - Peaceful

    protected int[]COLOR_LIST = {Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.DKGRAY, Color.BLUE, Color.BLACK };

    public CircleAudioView(Context context) {
        super(context);
        random = new Random();
    }

    public CircleAudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        //Complexity: Insertion O(1)--Deletion O(1)
        amplitudes = new LinkedList<>();
    }


    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
    }


    public void setAudioWaveDirection(int direction){
        if (direction == LEFT_TO_RIGHT){
            this.audioWaveDirection = LEFT_TO_RIGHT;
        } else {
            this.audioWaveDirection = RIGHT_TO_LEFT;
        }
    }

    public void setAudioWavePadding(int width, int height){
        this.audioWaveWidthPadding = width;
        this.audioWaveHeightPadding = height;
    }

    //Set minimum height of audio wave (dP) to display on View
    public void setMinimumDisplayHeight(int minimum){
        this.minimumDisplayHeight = minimum;
    }

    public void setAudioWaveChaos(float chaos){
        this.audioWaveChaos = chaos;
    }

    public void setAudioWaveLengthScale(int scale){
        this.audioWaveLengthScale = scale;
    }

    public Paint getPaint(){
        return this.linePaint;
    }
}
