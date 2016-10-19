package edu.phystech.petrov_aa.telegramchallenge2016;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.TransitionDrawable;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity implements
        View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener, AngleSeekBar.OnAngleChangeListener, View.OnTouchListener {


    public final static int GOTO_PHOTO_MODE = 0;
    public final static int GOTO_VIDEO_MODE = 1;
    public final static int TAKE_PICTURE = 2;
    public final static int START_VIDEO_RECORDING = 3;
    public final static int END_VIDEO_RECORDING = 4;
    public final static int START_INSTANTVIDEO = 5;
    public final static int END_INSTANTVIDEO = 6;
    public final static int SWITCH_FRONT_REAR = 7;
    public final static int SWITCH_FLASH_MODE = 8;

    public final static int PHOTO_MODE = 100;
    public final static int VIDEO_MODE = 101;
    public final static int VIDEO_RECORDING = 102;
    public final static int INSTANTVIDEO_RECORDING = 103;
    public final static int DECISION_MODE = 104;
    public final static int EDITING_MODE = 105;
    private int state = PHOTO_MODE;
    public int getCurrentState() {
        return state;
    }

    Resources  resources;

    SurfaceView4_3 surfaceView4_3;
    SurfaceView16_9 surfaceView16_9;

    FrameLayout cameraControlsBack;
    LinearLayout cameraControlsContainer;
    CameraButton cameraButton;
    FrameLayout cameraSwitchContainer;
    ToggleButton cameraSwitch;
    CameraModeIndicator cameraModeIndicator;
    FrameLayout cameraModeContainer;
    CameraFlashSelector cameraFlashSelector;
    FrameLayout cameraFlashContainer;

    Chronometer videoTime;


    private GestureDetectorCompat gestureDetector;
    private SwipeListener swipeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        state=PHOTO_MODE;

        resources = getResources();

        surfaceView4_3 = (SurfaceView4_3) findViewById(R.id.surface4_3);
        surfaceView16_9 = (SurfaceView16_9) findViewById(R.id.surface16_9);
        surfaceView16_9.setVisibility(View.INVISIBLE);

        videoTime = (Chronometer) findViewById(R.id.video_time);
//        videoTime.setFormat("HH:MM:SS");
        videoTime.setVisibility(View.INVISIBLE);


        cameraControlsBack = (FrameLayout) findViewById(R.id.camera_controls_back);
        cameraControlsContainer = (LinearLayout) findViewById(R.id.camera_controls_container);

        /* CAMERA BUTTON */

        FrameLayout cameraButtonContainer = (FrameLayout) findViewById(R.id.camera_button_container);
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.cam_but_imagebuttonsize),resources.getDimensionPixelSize(R.dimen.cam_but_imagebuttonsize), 0x11);
        cameraButton = new CameraButton(this);
        cameraButtonContainer.addView(cameraButton);
        cameraButton.setLayoutParams(layoutParams1);
        cameraButton.setBackgroundResource(0);
        cameraButton.setOnClickListener(this);
        cameraButton.setOnLongClickListener(this);
        cameraButton.setOnTouchListener(this);


        /* FRONT-REAR SWITCHER */

        cameraSwitchContainer = (FrameLayout) findViewById(R.id.camera_switch_container);
        cameraSwitch = (ToggleButton) findViewById(R.id.camera_switcher);
        cameraSwitch.setOnCheckedChangeListener(this);

        /* CAMERA MODE INDICATOR */

        cameraModeContainer = (FrameLayout) findViewById(R.id.camera_mode_indicator_container);
        cameraModeIndicator = new CameraModeIndicator(this);
        cameraModeContainer.addView(cameraModeIndicator);
        cameraModeIndicator.setBackground(getResources().getDrawable(R.drawable.camera_mode_indicator));

        cameraControlsBack = (FrameLayout) findViewById(R.id.camera_controls_back);

        swipeListener = new SwipeListener(cameraModeIndicator,this,cameraControlsBack);
        gestureDetector = new GestureDetectorCompat(this,swipeListener);

        /* FLASH BUTTON */

        cameraFlashContainer = (FrameLayout) findViewById(R.id.camera_flash_container);

        ImageButton flashAuto = (ImageButton) findViewById(R.id.camera_flash_auto);
        ImageButton flashOn = (ImageButton) findViewById(R.id.camera_flash_on);
        ImageButton flashOff = (ImageButton) findViewById(R.id.camera_flash_off);

        cameraFlashSelector = new CameraFlashSelector(this,flashAuto,flashOn,flashOff);

        surfaceHolder4_3 = surfaceView4_3.getHolder();
        surfaceHolder16_9 = surfaceView16_9.getHolder();
        surfaceHolder4_3.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder16_9.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (checkCameraHardware(this));



    }



    int cameraRearId = -1, cameraFrontId = -1, activeCameraId;

    int cameraRearOrientation, cameraFrontOrientation;
    SurfaceHolder surfaceHolder4_3, surfaceHolder16_9;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraControlsContainer.clearAnimation();
        cameraFlashContainer.clearAnimation();
        cameraModeContainer.clearAnimation();
        cameraButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_resume));
        cameraSwitchContainer.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_resume));
        cameraFlashContainer.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_resume));
        cameraModeIndicator.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_resume));

        if(state!=VIDEO_RECORDING && state!=INSTANTVIDEO_RECORDING)
            videoTime.setVisibility(View.INVISIBLE);

    }


    public void switchCameraFrontRear() {

        if(activeCameraId!=-1) {
            if (activeCameraId == cameraRearId) {
                activeCameraId = cameraFrontId;
            } else if (activeCameraId == cameraFrontId) {
                activeCameraId = cameraRearId;
            }
        }

    }

    public void switchCameraMode() {
        switchCameraMode(state);
    }

    public void switchCameraMode(int state) {
        if(state==VIDEO_MODE) {
            surfaceView4_3.setVisibility(View.INVISIBLE);
            surfaceView16_9.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(cameraControlsBack.getContext(),R.anim.camera_back_totrans);
                    cameraControlsBack.startAnimation(anim);
                }
            }, 150);

        } else if(state==PHOTO_MODE) {
            surfaceView4_3.setVisibility(View.VISIBLE);
            surfaceView16_9.setVisibility(View.INVISIBLE);

        }
    }


    public void cameraFocus() {

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else
            return false;
    }



    public void flashClick(View view) {
        action(SWITCH_FLASH_MODE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        cameraFocus();
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (state) {
            case PHOTO_MODE:
                action(TAKE_PICTURE);
                break;
            case INSTANTVIDEO_RECORDING:
                action(END_INSTANTVIDEO);
                break;
            case VIDEO_MODE:
                recordVideo();
                action(START_VIDEO_RECORDING);
                break;
            case VIDEO_RECORDING:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), CameraDecisionActivity.class);
                        i.putExtra(CameraDecisionActivity.EXTRA_MODE,CameraDecisionActivity.EXTRA_MODE_VIDEO);
                        i.putExtra(CameraDecisionActivity.EXTRA_VIDEODURATION, Long.toString(SystemClock.elapsedRealtime() - videoTime.getBase()));
                        startActivity(i);
                    }
                }, 250);
                action(END_VIDEO_RECORDING);
                break;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP & state==INSTANTVIDEO_RECORDING) {
            action(END_INSTANTVIDEO);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if(cameraButton.getState()==CameraButton.PHOTO)
            action(START_INSTANTVIDEO);
        if(cameraButton.getState()==CameraButton.VIDEO)
            action(START_VIDEO_RECORDING);
        return true;
    }

    public void recordVideo() {
        /* */
    }

    boolean ableToSwitch = true;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!ableToSwitch) return;

        ableToSwitch = false;
        cameraSwitch.setEnabled(false);
        if (isChecked) {
            ImageView img = (ImageView) findViewById(R.id.camera_switch_fill);
            img.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_switch_circle_down));
        } else {
            ImageView img = (ImageView) findViewById(R.id.camera_switch_fill);
            img.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_switch_circle_up));
        }
        ImageView img2 = (ImageView) findViewById(R.id.camera_switch_icon);
        img2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_switch_rotate));
        cameraButton.startAnimation(CameraButton.SWITCH_FRONT_REAR);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchCameraFrontRear();
                ableToSwitch = true;
                cameraSwitch.setEnabled(true);
            }
        }, 220);
    }

    @Override
    public void onAngleChange(float angle) {
//        angleTextView.setText(String.format(Locale.ENGLISH,"%.1f",angle)+(char)0x00B0);
    }


    boolean photoTaken = false;


    /**
     * This method controls all the processes in the camera
     */
    public void action(final int actionValue) {
        switch (actionValue) {
            case GOTO_PHOTO_MODE : {

                Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        switchCameraMode();
                        cameraModeIndicator.startAnimation(CameraModeIndicator.SWITCH_TO_RIGHT2);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        cameraControlsBack.startAnimation(AnimationUtils.loadAnimation(cameraControlsBack.getContext(),R.anim.camera_back_tosolid));
                        cameraButton.startAnimation(CameraButton.SWITCH_TO_PHOTOMODE);
                    }
                };

                cameraModeIndicator.startAnimation(CameraModeIndicator.SWITCH_TO_RIGHT1,animatorListener);

                state = PHOTO_MODE;

            }
            break;
            case TAKE_PICTURE : {
                photoTaken = true;
                cameraButton.startAnimation(CameraButton.SHOT);
                cameraControlsContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_controls_hide));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), CameraDecisionActivity.class);
                        i.putExtra(CameraDecisionActivity.EXTRA_MODE,CameraDecisionActivity.EXTRA_MODE_PHOTO);
                        startActivity(i);
                    }
                }, 250);
            }
            break;
            case GOTO_VIDEO_MODE : {

                Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        switchCameraMode();
                        cameraModeIndicator.startAnimation(CameraModeIndicator.SWITCH_TO_LEFT2);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        cameraButton.startAnimation(CameraButton.SWITCH_TO_VIDEOMODE);
                    }
                };

                cameraModeIndicator.startAnimation(CameraModeIndicator.SWITCH_TO_LEFT1,animatorListener);
                state = VIDEO_MODE;

            }
            break;
            case START_VIDEO_RECORDING : {
                cameraButton.startAnimation(CameraButton.START_RECORDING);
                cameraModeIndicator.endAnimation();
                cameraModeIndicator.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_indic_hide));
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.camera_back_hide);
                anim.setInterpolator(new AccelerateInterpolator());
                cameraControlsBack.startAnimation(anim);
                ((FrameLayout)findViewById(R.id.camera_switch_container)).startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_indic_hide));
                state = VIDEO_RECORDING;

                videoTime.startAnimation(AnimationUtils.loadAnimation(this,R.anim.video_time_showup));

                videoTime.setBase(SystemClock.elapsedRealtime());
                videoTime.start();

            }
            break;
            case END_VIDEO_RECORDING : {
                cameraButton.startAnimation(CameraButton.STOP_RECORDING);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.camera_indic_hide);
                anim.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return Math.abs(input -1f);
                    }
                });
                ((FrameLayout)findViewById(R.id.camera_switch_container)).startAnimation(anim);
                cameraModeIndicator.startAnimation(anim);
                Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.camera_back_show);
                anim2.setInterpolator(new DecelerateInterpolator());
                cameraControlsBack.startAnimation(anim2);
                state = VIDEO_MODE;

                videoTime.startAnimation(AnimationUtils.loadAnimation(this,R.anim.video_time_hide));
            }
            break;
            case START_INSTANTVIDEO : {
                state = INSTANTVIDEO_RECORDING;
                Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                };

                cameraButton.startAnimation(CameraButton.START_INSTANT_RECORDING,listener);
                ((FrameLayout)findViewById(R.id.camera_switch_container)).startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_indic_hide));
                TransitionDrawable transition = (TransitionDrawable) cameraControlsBack.getBackground();
                transition.startTransition(250);
                cameraControlsBack.startAnimation(AnimationUtils.loadAnimation(cameraControlsBack.getContext(),R.anim.camera_back_totrans));

                videoTime.startAnimation(AnimationUtils.loadAnimation(this,R.anim.video_time_showup));
                videoTime.setBase(SystemClock.elapsedRealtime());
                videoTime.start();
            }
            break;
            case END_INSTANTVIDEO : {
                cameraButton.startAnimation(CameraButton.STOP_INSTANT_RECORDING);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.camera_indic_hide);
                anim.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return Math.abs(input -1f);
                    }
                });
                ((FrameLayout)findViewById(R.id.camera_switch_container)).startAnimation(anim);
                TransitionDrawable transition = (TransitionDrawable) cameraControlsBack.getBackground();
                transition.reverseTransition(250);
                Animation anim2 = AnimationUtils.loadAnimation(cameraControlsBack.getContext(),R.anim.camera_back_totrans);
                anim2.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return Math.abs(input -1f);
                    }
                });
                cameraControlsBack.startAnimation(anim2);
                state = PHOTO_MODE;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), CameraDecisionActivity.class);
                        i.putExtra(CameraDecisionActivity.EXTRA_MODE,CameraDecisionActivity.EXTRA_MODE_VIDEO);
                        i.putExtra(CameraDecisionActivity.EXTRA_VIDEODURATION, Long.toString(SystemClock.elapsedRealtime() - videoTime.getBase()));
                        startActivity(i);
                    }
                }, 250);

                videoTime.startAnimation(AnimationUtils.loadAnimation(this,R.anim.video_time_hide));
            }
            break;
            case SWITCH_FLASH_MODE : {
                cameraFlashSelector.next();
            }
            break;

        }
    }

}
