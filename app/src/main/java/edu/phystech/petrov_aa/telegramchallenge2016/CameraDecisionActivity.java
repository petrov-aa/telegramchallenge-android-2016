package edu.phystech.petrov_aa.telegramchallenge2016;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CameraDecisionActivity extends Activity implements View.OnClickListener {

    final static String EXTRA_MODE_VIDEO = "VIDEOMODE";
    final static String EXTRA_MODE_PHOTO = "PHOTOMODE";
    final static String EXTRA_MODE = "edu.phystech.petrov_aa.telegramchallenge2016.MODE";
    final static String EXTRA_PHOTO = "edu.phystech.petrov_aa.telegramchallenge2016.PHOTO";
    final static String EXTRA_VIDEO = "edu.phystech.petrov_aa.telegramchallenge2016.VIDEO";
    final static String EXTRA_VIDEODURATION = "edu.phystech.petrov_aa.telegramchallenge2016.VIDEODURATION";

    ImagePreview imagePreview;
    SurfaceView16_9 surfaceView16_9;

    FrameLayout cameraDecisionControlsContainer;
    Button cameraDoneButton, cameraCancelButton;
    ImageButton cameraCropButton, cameraPlayButton, cameraPauseButton;
    ProgressBar videoProgress;

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_decision);

        imagePreview = (ImagePreview) findViewById(R.id.image_preview);
        surfaceView16_9 = (SurfaceView16_9) findViewById(R.id.surface16_9);

        /* -- DECISION VIEW (CANCEL and DONE buttons) -- */

        cameraDecisionControlsContainer = (FrameLayout) findViewById(R.id.camera_decision_container);

        cameraDoneButton = (Button) findViewById(R.id.camera_done_button);
        cameraCancelButton = (Button) findViewById(R.id.camera_cancel_button);
        cameraCropButton = (ImageButton) findViewById(R.id.camera_crop_button);
        cameraPlayButton = (ImageButton) findViewById(R.id.camera_play_button);
        cameraPauseButton = (ImageButton) findViewById(R.id.camera_pause_button);
        videoProgress = (ProgressBar) findViewById(R.id.video_progress);

        cameraPauseButton.setVisibility(View.INVISIBLE);

        switch (getIntent().getStringExtra(EXTRA_MODE)) {
            case EXTRA_MODE_PHOTO : {
                imagePreview.setVisibility(View.VISIBLE);
                surfaceView16_9.setVisibility(View.INVISIBLE);
                //photoPath = getIntent().getStringExtra(EXTRA_PHOTO);
                cameraPlayButton.setVisibility(View.GONE);
                cameraPauseButton.setVisibility(View.GONE);
                videoProgress.setVisibility(View.GONE);
                cameraCropButton.setOnClickListener(this);
                cameraCropButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
            }
            break;
            case EXTRA_MODE_VIDEO : {

                imagePreview.setVisibility(View.INVISIBLE);
                surfaceView16_9.setVisibility(View.VISIBLE);
                cameraCropButton.setVisibility(View.GONE);
                cameraPlayButton.setOnClickListener(this);
                cameraPlayButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
                cameraPauseButton.setOnClickListener(this);

                videoProgress.setProgress(0);

                //doVideoProgress();

            }
            break;
        }

        cameraDoneButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
        cameraCancelButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));

        cameraCancelButton.setOnClickListener(this);
        cameraDoneButton.setOnClickListener(this);


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
//        imagePreview.setImageBitmap(bitmap);



    }

    long videoTimeOffset = 0;

    public void doVideoProgress() {

        final long videoDuration = Long.parseLong(getIntent().getStringExtra(EXTRA_VIDEODURATION));

        videoProgress.setMax((int)videoDuration);

        final long startTime = SystemClock.elapsedRealtime() - videoTimeOffset;

        progresser = new VideoProgresser(startTime,videoDuration,this,videoProgress);
        videoProgressThread = new Thread(progresser);
        videoProgressThread.start();
    }

    VideoProgresser progresser;

    private class VideoProgresser implements Runnable {
        long startTime, videoDuration;
        long curTime;
        boolean interrupted = false;
        CameraDecisionActivity cda;
        ProgressBar p;
        public VideoProgresser(long startTime, long videoDuration, CameraDecisionActivity cda, ProgressBar p) {
            this.startTime = startTime;
            this.videoDuration = videoDuration;
            this.cda = cda;
            this.p = p;
        }
        @Override
        public void run() {

            do {
                curTime = SystemClock.elapsedRealtime() - startTime;
                cda.videoTimeOffset = curTime;
                p.setProgress((int)(curTime));
            } while(!interrupted && curTime <= videoDuration);

        }
        public void stopit() {
            interrupted = true;
        }
    }

    Thread videoProgressThread;


    boolean playing = false;

    @Override
    public void onClick(View v) {
        if (v==cameraCropButton) {
            Intent i = new Intent(getApplicationContext(), CameraEditActivity.class);
            startActivity(i);
            return;
        }
        if (v==cameraDoneButton) {
            finish();
            return;
        }
        if (v==cameraCancelButton) {
            finish();
            return;
        }
        if(v==cameraPauseButton || v==cameraPlayButton) {
            if(playing) {
                progresser.stopit();
                cameraPauseButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_edit_diss));
                cameraPlayButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_edit_showup));
            } else {
                doVideoProgress();
                cameraPlayButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.camera_edit_diss));
                Animation anim = AnimationUtils.loadAnimation(this,R.anim.camera_edit_showup);
                cameraPauseButton.startAnimation(anim);
            }
            playing = !playing;
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraDoneButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
        cameraCancelButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
        cameraCropButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.camera_edit_showup));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pending_anim_enter,R.anim.pending_anim_exit);
    }
}
