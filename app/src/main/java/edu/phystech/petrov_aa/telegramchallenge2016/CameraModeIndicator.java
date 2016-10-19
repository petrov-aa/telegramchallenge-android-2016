package edu.phystech.petrov_aa.telegramchallenge2016;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.ArrayList;

public class CameraModeIndicator extends ImageView implements ValueAnimator.AnimatorUpdateListener {

    public static final int SWITCH_TO_RIGHT1 = 0;
    public static final int SWITCH_TO_RIGHT2 = 2;
    public static final int SWITCH_TO_LEFT1 = 1;
    public static final int SWITCH_TO_LEFT2 = 3;
    public static final int DRAW_BACK = -1;

    public static final int ON_RIGHT = 0;
    public static final int ON_LEFT = 1;

    private float[] makeBorders(float br) {
        float[] a = new float[8];
        for (int i=0; i<8; i++) {
            a[i] = br;
        }
        return a;
    }

    float circle_diam, circle_rad, indicatorwidth;

    AnimatorSet animatorSet;
    ShapeHolder shapeLeftCircle,shapeRightCircle,shapeToggle;
    private ArrayList<ShapeHolder> shapes;
    Resources resources;

    int state;

    public CameraModeIndicator(Context context) {
        super(context);

        resources = getResources();

        state = ON_LEFT;

        circle_diam = resources.getDimension(R.dimen.cam_mode_circle_diameter);
        circle_rad = circle_diam / 2f;
        indicatorwidth = resources.getDimension(R.dimen.cam_mode_width);

        shapes = new ArrayList<>();
        ShapeDrawable s_tmp;

        // Left Circle
        s_tmp = new ShapeDrawable(new RoundRectShape(makeBorders(resources.getDimension(R.dimen.cam_mode_circle_diameter)/2f),null,null));
        shapeLeftCircle = new ShapeHolder(s_tmp,circle_diam,circle_diam,0,0);
        s_tmp.getPaint().setColor(resources.getColor(R.color.camera_mode_inactive));
        shapes.add(shapeLeftCircle);
        // Right Circle
        s_tmp = new ShapeDrawable(new RoundRectShape(makeBorders(resources.getDimension(R.dimen.cam_mode_circle_diameter)/2f),null,null));
        shapeRightCircle = new ShapeHolder(s_tmp,circle_diam,circle_diam,indicatorwidth-circle_diam,0);
        s_tmp.getPaint().setColor(resources.getColor(R.color.camera_mode_inactive));
        shapes.add(shapeRightCircle);
        // Circle Toggle
        s_tmp = new ShapeDrawable(new OvalShape());
        shapeToggle = new ShapeHolder(s_tmp,circle_diam,circle_diam,0,0);
        s_tmp.getPaint().setColor(resources.getColor(R.color.camera_mode_active));
        shapes.add(shapeToggle);
    }

    public void createAnimation(int value) {
        animatorSet = new AnimatorSet();
        float[] d = new float[2];
        switch (value) {
            case SWITCH_TO_LEFT1 : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeToggle,"x",0,indicatorwidth-circle_diam);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeLeftCircle,"width",circle_diam,indicatorwidth);
                animator1.setDuration(300);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animatorSet.play(animator1).with(animator2);
                state = ON_LEFT;
            }
            break;
            case SWITCH_TO_RIGHT1 : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeToggle,"x",indicatorwidth-circle_diam,0);
                PropertyValuesHolder pvhX1 = PropertyValuesHolder.ofFloat("x",indicatorwidth-circle_diam,0);
                PropertyValuesHolder pvhWidth1 = PropertyValuesHolder.ofFloat("width",circle_diam,indicatorwidth);
                ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(shapeRightCircle,pvhX1,pvhWidth1);
                animator1.setDuration(300);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animatorSet.play(animator1).with(animator2);
                state = ON_RIGHT;
            }
            break;
            case SWITCH_TO_LEFT2 : {
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeLeftCircle,"width",indicatorwidth,circle_diam);
                animator3.setDuration(300);
                animator3.addUpdateListener(this);
                animatorSet.play(animator3);
                state = ON_LEFT;
            }
            break;
            case SWITCH_TO_RIGHT2 : {
                PropertyValuesHolder pvhX2 = PropertyValuesHolder.ofFloat("x",0,indicatorwidth-circle_diam);
                PropertyValuesHolder pvhWidth2 = PropertyValuesHolder.ofFloat("width",indicatorwidth,circle_diam);
                ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(shapeRightCircle,pvhX2,pvhWidth2);
                animator3.setDuration(300);
                animator3.addUpdateListener(this);
                animatorSet.play(animator3);
                state = ON_RIGHT;
            }
            break;
        }
    }

    public void startAnimation(int value, Animator.AnimatorListener listener) {
        createAnimation(value);
        animatorSet.addListener(listener);
        animatorSet.start();
    }

    public void startAnimation(int value) {
        createAnimation(value);
        animatorSet.start();
    }

    public void endAnimation() {
        if(animatorSet != null && animatorSet.isRunning()) animatorSet.end();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float[] d = new float[2];
        for (ShapeHolder shp : shapes) {
            shp.getXY(d);
            canvas.translate(d[0],d[1]);
            shp.getShape().draw(canvas);
            canvas.translate(-d[0],-d[1]);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    private class ShapeHolder {
        private ShapeDrawable shape;
        private float width_px0, height_px0, width_px, height_px, x, y, x_offset, y_offset;

        public ShapeHolder(ShapeDrawable shape, float width_px, float height_px, float x_offset, float y_offset) {
            this.shape = shape;
            width_px0 = this.width_px = width_px;
            height_px0 = this.height_px = height_px;
            this.x_offset = x_offset;
            this.y_offset = y_offset;
            x = y = 0;
            this.shape.getShape().resize(width_px,height_px);
        }

        public void setWidth(float width_px) {
            this.width_px = width_px;
            x = width_px0 - width_px;
            shape.getShape().resize(this.width_px,this.height_px);
        }

        public void setX(float x_px) {
            x_offset = x_px;
        }

        public void setY(float y_px) {
            y_offset = y_px;
        }

        public float[] getXY(float[] d) {
            d[0] = x_offset;
            d[1] = y_offset;
            return d;
        }

        public void setAlpha(float alpha) {
            shape.setAlpha((int)((alpha * 255f) + .5f));
        }

        public ShapeDrawable getShape() {
            return shape;
        }
    }
}
