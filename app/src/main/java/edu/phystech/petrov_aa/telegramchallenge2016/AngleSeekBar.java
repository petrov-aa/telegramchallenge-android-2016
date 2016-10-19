package edu.phystech.petrov_aa.telegramchallenge2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import java.util.ArrayList;

public class AngleSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    ArrayList<OnAngleChangeListener> listeners;

    float[] borders;
    int bar_num;

    public AngleSeekBar(Context context) {
        super(context);
        init();
    }

    public AngleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AngleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borders = new float[8];
        for(int i=0;i<8;i++) {
            borders[i] = 3;
        }
        bar_num = 16;
        setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float mid_y = getHeight() / 2f;
        float mid_x = getWidth() / 2f;
        float width2 = mid_x;

        float bar_width = 4;

        int max = getMax();
        int progress = getProgress();

        int progress_i = Math.round((progress * (bar_num-1)*2)/(float)max) - bar_num+1;
        int progressStart, progressEnd;
        if(progress_i>=0) {
            progressStart = 0;
            progressEnd = progress_i;
        } else {
            progressStart = progress_i;
            progressEnd = 0;
        }

        Paint paint = new Paint();

        for(int i=-bar_num+1; i<bar_num; i++) {

            float left = - bar_width/2f + (width2)*1.15f*transform(i); // 1.15f
            float right = left + bar_width;

            if(i >= progressStart && i <= progressEnd) {
                paint.setColor(Color.parseColor("#54ACF0"));
            } else {
                int cval = 255 - 230/bar_num*Math.abs(i);
                paint.setColor(Color.rgb(cval,cval,cval));
            }

            if(i!=progress_i) {
                canvas.drawRect(mid_x + left,mid_y-15,mid_x + right,mid_y+15,paint);
            } else {
                canvas.drawRect(mid_x + left,mid_y-21,mid_x + right,mid_y+21,paint);
            }

        }

        paint.setColor(Color.parseColor("#54ACF0"));

        RoundRectShape rrs = new RoundRectShape(borders,null,null);
        rrs.resize(bar_width*2f,60);
        canvas.translate(mid_x - bar_width,mid_y-30);
        rrs.draw(canvas,paint);
        canvas.translate(-mid_x + bar_width,-mid_y+30);

    }

    private float transform(float x) {
        return (float)Math.sin((float)x*Math.PI/3f/(float)bar_num); // 3f
    }

    public void addOnAngleChangeListener(OnAngleChangeListener listener) {
        if(listeners==null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float pr = (getProgress() - getMax()/2f);
        for(OnAngleChangeListener listener : listeners)
            listener.onAngleChange(pr/10f);
        invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public interface OnAngleChangeListener {
        public void onAngleChange(float angle);
    }

}
