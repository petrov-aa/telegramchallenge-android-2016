package edu.phystech.petrov_aa.telegramchallenge2016;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImagePreview extends ImageView {

    Context context;
    float density;

    int heightScreen;

    public ImagePreview(Context context) {
        super(context);
        init();
    }

    public ImagePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImagePreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        density = getResources().getDisplayMetrics().density;
        heightScreen = getResources().getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        float maxHeight = heightScreen - density*150;

        int width = getMeasuredWidth();
        int height = width*4/3;
        if(width > heightScreen) {
            height = getMeasuredHeight();
            width = height*3/4;
        } else if(height>maxHeight) {
            height = (int)maxHeight;
            width = height*3/4;
        }
        //System.out.println(Integer.toString(getMeasuredWidth())+" "+Integer.toString(getMeasuredHeight())+" "+Integer.toString(width)+" "+Integer.toString(height));

        setMeasuredDimension(width,height);
    }
}
