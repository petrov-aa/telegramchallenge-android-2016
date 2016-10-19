package edu.phystech.petrov_aa.telegramchallenge2016;

import android.graphics.drawable.TransitionDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

public class SwipeListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 200;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    boolean result = false;

    CameraModeIndicator cameraModeIndicator;
    CameraActivity cameraActivity;
    FrameLayout cameraControlsBack;

    public SwipeListener(CameraModeIndicator cameraModeIndicator, CameraActivity cameraActivity, FrameLayout cameraControlsBack) {
        this.cameraModeIndicator = cameraModeIndicator;
        this.cameraActivity = cameraActivity;
        this.cameraControlsBack = cameraControlsBack;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float v1, float v2) {
        float diffX = event2.getX() - event1.getX();
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(v1) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                onSwipeRight();
            } else {
                onSwipeLeft();
            }
            result = true;
        }
        return result;
    }

    public void onSwipeRight() {
        if(cameraActivity.getCurrentState()==CameraActivity.PHOTO_MODE) {
            cameraActivity.action(CameraActivity.GOTO_VIDEO_MODE);
        }
    }

    public void onSwipeLeft() {
        if(cameraActivity.getCurrentState()==CameraActivity.VIDEO_MODE)
            cameraActivity.action(CameraActivity.GOTO_PHOTO_MODE);
    }

}
