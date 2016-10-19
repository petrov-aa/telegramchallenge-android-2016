package edu.phystech.petrov_aa.telegramchallenge2016;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class SurfaceView16_9 extends SurfaceView {

    Context context;
    float density;

    int heightScreen;

    public SurfaceView16_9(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        density = getResources().getDisplayMetrics().density;
        heightScreen = getResources().getSystem().getDisplayMetrics().heightPixels;
    }

    public SurfaceView16_9(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        density = getResources().getDisplayMetrics().density;
        heightScreen = getResources().getSystem().getDisplayMetrics().heightPixels;
    }

    public SurfaceView16_9(Context context) {
        super(context);
        this.context = context;
        density = getResources().getDisplayMetrics().density;
        heightScreen = getResources().getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        float maxHeight = heightScreen - density*150;

        int width = getMeasuredWidth();
        int height = width*16/9;

        if(height>getMeasuredHeight()) {
            height = getMeasuredHeight();
            width = height*9/16;
        }

        System.out.println(Integer.toString(getMeasuredWidth())+" "+Integer.toString(getMeasuredHeight())+" "+Integer.toString(width)+" "+Integer.toString(height));

        setMeasuredDimension(width,height);
    }
}
