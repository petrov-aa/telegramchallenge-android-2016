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
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import java.util.ArrayList;

public class CameraButton extends ImageButton implements ValueAnimator.AnimatorUpdateListener {

    final static int SHOT = 0;
    final static int SWITCH_FRONT_REAR = 1;
    final static int SWITCH_TO_VIDEOMODE = 2;
    final static int SWITCH_TO_PHOTOMODE = 3;
    final static int START_RECORDING = 4;
    final static int STOP_RECORDING = 5;
    final static int START_INSTANT_RECORDING = 6;
    final static int STOP_INSTANT_RECORDING = 7;

    final static int PHOTO = 0;
    final static int VIDEO = 1;
    final static int RECORDING = 2;
    final static int INSTANTRECORDING = 3;

    Resources resources;
    float imagebuttonsize, outer_w, outer_ww, outer_h,
            outer_br, blue1_w, blue1_ww, blue1_h, blue1_br,
            blue1_sh, blue2_w, blue2_h, rec_d, rec_v, rec_r, stop_d, stop_br, stop_r, cam_default;

    ShapeHolder shapeOuter, shapeBlue1, shapeBlue2, shapeRec, shapeCameraSwitch, shapeStopIcon;
    AnimatorSet animatorSet;
    int state;

    ArrayList<ShapeHolder> shapes;

    public CameraButton(Context context) {
        super(context);
        resources = getResources();
        createButton();
    }

    private float[] makeBorders(float br) {
        float[] a = new float[8];
        for (int i=0; i<8; i++) {
            a[i] = br;
        }
        return a;
    }

    public int getState() {
        return state;
    }


    private void createButton() {
        // loading of resources
        imagebuttonsize = resources.getDimension(R.dimen.cam_but_imagebuttonsize);
        outer_w = outer_h = resources.getDimension(R.dimen.cam_but_outer_size);
        outer_ww = resources.getDimension(R.dimen.cam_but_outer_widewidth);
        outer_br = outer_w/2f;
        blue1_w = blue1_h = resources.getDimension(R.dimen.cam_but_blue1_size);
        blue1_ww = resources.getDimension(R.dimen.cam_but_blue1_widewidth);
        blue1_br = blue1_w/2f;
        blue1_sh = resources.getDimension(R.dimen.cam_but_blue1_shuttersize);
        blue2_w = blue2_h = resources.getDimension(R.dimen.cam_but_blue2_size);
        rec_d = resources.getDimension(R.dimen.cam_but_rec_default);
        rec_v = resources.getDimension(R.dimen.cam_but_rec_onvideomode);
        rec_r = resources.getDimension(R.dimen.cam_but_rec_onrecording);
        stop_d = resources.getDimension(R.dimen.cam_but_stop_default);
        stop_br = resources.getDimension(R.dimen.cam_but_stop_borderradius);
        stop_r = resources.getDimension(R.dimen.cam_but_stop_onrecording);
        cam_default = resources.getDimension(R.dimen.cam_but_default);

        shapes = new ArrayList<>();
        ShapeDrawable s_tmp;

        // Outer Circle (the White one)
        s_tmp = new ShapeDrawable(new RoundRectShape(makeBorders(outer_br),null,null));
        shapeOuter = new ShapeHolder(s_tmp, outer_w, outer_h,
                (imagebuttonsize - outer_w)/2.0f, (imagebuttonsize - outer_h)/2.0f);
        s_tmp.getPaint().setColor(resources.getColor(R.color.camera_button_stroke));
        shapes.add(shapeOuter);

        // Inner Circle (the Blue one)
        s_tmp = new ShapeDrawable(new RoundRectShape(makeBorders(blue1_br),null,null));
        shapeBlue1 = new ShapeHolder(s_tmp, blue1_w, blue1_h,
                (imagebuttonsize - blue1_w)/2.0f, (imagebuttonsize - blue1_h)/2.0f);
        s_tmp.getPaint().setColor(getResources().getColor(R.color.camera_button_blue1));
        shapes.add(shapeBlue1);

        // Inner of Inner Circle (the other Blue one)
        s_tmp = new ShapeDrawable(new OvalShape());
        shapeBlue2 = new ShapeHolder(s_tmp, blue2_w, blue2_h,
                (imagebuttonsize - blue2_w)/2.0f, (imagebuttonsize - blue2_h)/2.0f);
        s_tmp.getPaint().setColor(getResources().getColor(R.color.camera_button_blue2));
        shapes.add(shapeBlue2);

        // Inner red Circle
        s_tmp = new ShapeDrawable(new OvalShape());
        shapeRec = new ShapeHolder(s_tmp, rec_d, rec_d,
                imagebuttonsize/2.0f, imagebuttonsize/2.0f);
        s_tmp.getPaint().setColor(getResources().getColor(R.color.camera_button_rec));
        shapes.add(shapeRec);

        // Needs to enlighten the whole button on camera rear-front switching
        s_tmp = new ShapeDrawable(new OvalShape());
        shapeCameraSwitch = new ShapeHolder(s_tmp,outer_w,outer_h,
                (imagebuttonsize - outer_w)/2.0f,(imagebuttonsize - outer_h)/2.0f);
        shapeCameraSwitch.getShape().setAlpha(0);
        s_tmp.getPaint().setColor(getResources().getColor(R.color.camera_button_stroke));
        shapes.add(shapeCameraSwitch);

        // Stop Icon
        s_tmp = new ShapeDrawable(new RoundRectShape(makeBorders(stop_br),null,null));
        shapeStopIcon = new ShapeHolder(s_tmp,stop_d,stop_d,
                imagebuttonsize/2.0f,imagebuttonsize/2.0f);
        s_tmp.getPaint().setColor(getResources().getColor(R.color.camera_button_stroke));
        shapes.add(shapeStopIcon);
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

    private void createAnimation(int value) {
        if(animatorSet!=null && animatorSet.isRunning()) animatorSet.end();
        animatorSet = new AnimatorSet();
        switch (value) {
            case SWITCH_TO_VIDEOMODE : {
                // white circle
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeOuter, "width", outer_w, outer_ww);
                animator1.setDuration(250);
                animator1.addUpdateListener(this);
                // outer blue circle
                PropertyValuesHolder pvhWidth = PropertyValuesHolder.ofFloat("width", blue1_w, blue1_ww);
                PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
                ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(shapeBlue1, pvhWidth, pvhAlpha);
                animator2.setDuration(250);
                animator2.addUpdateListener(this);
                // inner blue circle
                PropertyValuesHolder pvhWidth2 = PropertyValuesHolder.ofFloat("width", blue2_w, cam_default);
                PropertyValuesHolder pvhHeight2 = PropertyValuesHolder.ofFloat("height", blue2_w, cam_default);
                ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(shapeBlue2, pvhWidth2, pvhHeight2);
                animator3.setDuration(250);
                animator3.addUpdateListener(this);
                // red circle
                PropertyValuesHolder pvhWidth3 = PropertyValuesHolder.ofFloat("width", cam_default, rec_v);
                PropertyValuesHolder pvhHeight3 = PropertyValuesHolder.ofFloat("height", cam_default, rec_v);
                ObjectAnimator animator4 = ObjectAnimator.ofPropertyValuesHolder(shapeRec, pvhWidth3, pvhHeight3);
                animator4.setDuration(250);
                animator4.setStartDelay(100);
                animator4.addUpdateListener(this);
                animatorSet.playTogether(animator1, animator2, animator3, animator4);
                state = VIDEO;
            }
            break;
            case SWITCH_TO_PHOTOMODE : {
                // white circle
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeOuter, "width", outer_ww, outer_w);
                animator1.setDuration(250);
                animator1.addUpdateListener(this);
                // outer blue circle
                PropertyValuesHolder pvhWidth = PropertyValuesHolder.ofFloat("width", blue1_ww, blue1_w);
                PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
                ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(shapeBlue1, pvhWidth, pvhAlpha);
                animator2.setDuration(250);
                animator2.addUpdateListener(this);
                // inner blue circle
                PropertyValuesHolder pvhWidth2 = PropertyValuesHolder.ofFloat("width", cam_default, blue2_w);
                PropertyValuesHolder pvhHeight2 = PropertyValuesHolder.ofFloat("height", cam_default, blue2_h);
                ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(shapeBlue2, pvhWidth2, pvhHeight2);
                animator3.setDuration(250);
                animator3.addUpdateListener(this);
                // red circle
                PropertyValuesHolder pvhWidth3 = PropertyValuesHolder.ofFloat("width", rec_v, cam_default);
                PropertyValuesHolder pvhHeight3 = PropertyValuesHolder.ofFloat("height", rec_v, cam_default);
                ObjectAnimator animator4 = ObjectAnimator.ofPropertyValuesHolder(shapeRec, pvhWidth3, pvhHeight3);
                animator4.setDuration(250);
                animator4.addUpdateListener(this);
                animatorSet.playTogether(animator1, animator2, animator3, animator4);
                state = PHOTO;
            }
            break;
            case SWITCH_FRONT_REAR : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeCameraSwitch, "alpha", 0f, 0.3f);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeCameraSwitch, "alpha", 0.3f, 0f);
                animator1.setDuration(100);
                animator2.setDuration(100);
                animatorSet.playSequentially(animator1, animator2);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
            }
            break;
            case SHOT : {
                // TODO try to calculate to make shuttersize 30
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeBlue1,"size",blue1_w,blue2_w);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeBlue1,"size",blue2_w,blue1_w);
                animator1.addUpdateListener(this);
                animator3.addUpdateListener(this);
                animator1.setDuration(50);
                animator3.setDuration(50);
                animator3.setStartDelay(100);
                animatorSet.playSequentially(animator1,animator3);
            }
            break;
            case START_RECORDING : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeOuter,"width",outer_ww,cam_default);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeOuter,"height",outer_h,cam_default);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeRec,"size",cam_default,rec_r);
                ObjectAnimator animator3pause = ObjectAnimator.ofFloat(shapeRec,"size",cam_default,cam_default);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(shapeStopIcon,"size",cam_default,stop_r);
                ObjectAnimator animator5pause = ObjectAnimator.ofFloat(shapeStopIcon,"size",cam_default,cam_default);
                animator1.addUpdateListener(this);
                animator3.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animator5.addUpdateListener(this);
                animator3.setInterpolator(new OvershootInterpolator(3f));
                animator1.setDuration(300);
                animator2.setDuration(300);
                animator3pause.setDuration(150);
                animator3.setDuration(250);
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playSequentially(animator3pause,animator3);
                animator5pause.setDuration(300);
                animator5.setDuration(150);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet1.playSequentially(animator5pause,animator5);
                animatorSet.play(animator1).with(animator2).with(animatorSet1).with(animatorSet2);
                state = RECORDING;
            }
            break;
            case STOP_RECORDING : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeRec,"size",rec_r,rec_v);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeOuter,"width",cam_default,outer_ww);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeOuter,"height",cam_default,outer_h);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(shapeStopIcon,"size",stop_r,stop_d);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animator3.addUpdateListener(this);
                animator4.addUpdateListener(this);
                animatorSet.playTogether(animator1,animator2,animator3,animator4);
                animatorSet.setDuration(250);
                state = VIDEO;
            }
            break;
            case START_INSTANT_RECORDING : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeOuter,"size",outer_w,cam_default);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeBlue1,"size",blue1_w,cam_default);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeBlue2,"size",blue2_w,cam_default);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(shapeRec,"size",rec_d,rec_r);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(shapeStopIcon,"size",stop_d,stop_r);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animator3.addUpdateListener(this);
                animator4.addUpdateListener(this);
                animator5.addUpdateListener(this);
                animatorSet.playTogether(animator1,animator2,animator3,animator4,animator5);
                animatorSet.setDuration(300);
                state = INSTANTRECORDING;
            }
            break;
            case STOP_INSTANT_RECORDING : {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(shapeOuter,"size",cam_default,outer_w);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shapeBlue1,"size",cam_default,blue1_w);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(shapeBlue2,"size",cam_default,blue2_w);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(shapeRec,"size",rec_r,rec_d);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(shapeStopIcon,"size",stop_r,stop_d);
                animator1.addUpdateListener(this);
                animator2.addUpdateListener(this);
                animator3.addUpdateListener(this);
                animator4.addUpdateListener(this);
                animator5.addUpdateListener(this);
                animatorSet.playTogether(animator1,animator2,animator3,animator4,animator5);
                animatorSet.setDuration(300);
                state = PHOTO;
            }
            break;
        }
    }

    public void startAnimation(int value, Animator.AnimatorListener animatorListener) {
        createAnimation(value);
        animatorSet.addListener(animatorListener);
        animatorSet.start();
        invalidate();
    }

    public void startAnimation(int value) {
        createAnimation(value);
        animatorSet.start();
        invalidate();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    private class ShapeHolder {
        private ShapeDrawable shape;
        private float width_px0, height_px0, width_px, height_px, mid_x_px, mid_y_px, x, y;

        public ShapeHolder(ShapeDrawable shape, float width_px, float height_px, float mid_x_px, float mid_y_px) {
            this.shape = shape;
            width_px0 = this.width_px = width_px;
            height_px0 = this.height_px = height_px;
            this.mid_x_px = mid_x_px;
            this.mid_y_px = mid_y_px;
            x = 0;
            y = 0;
            this.shape.getShape().resize(width_px,height_px);
        }

        public void setWidth(float width_px) {
            this.width_px = width_px;
            x = width_px0 - width_px;
            shape.getShape().resize(this.width_px,this.height_px);
        }

        public void setHeight(float height_px) {
            this.height_px = height_px;
            y = height_px0 - height_px;
            shape.getShape().resize(this.width_px,this.height_px);
        }

        /**
         * A method for both width and height
         * @param size = width = height (px)
         */
        public void setSize(float size) {
            this.height_px = size;
            this.width_px = size;
            x = width_px0 - size;
            y = height_px0 - size;
            shape.getShape().resize(this.width_px,this.height_px);
        }

        public float[] getXY(float[] d) {
            d[0] = mid_x_px + x/2f;
            d[1] = mid_y_px  + y/2f;
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