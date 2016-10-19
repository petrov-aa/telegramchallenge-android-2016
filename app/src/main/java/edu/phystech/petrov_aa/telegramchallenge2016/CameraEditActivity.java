package edu.phystech.petrov_aa.telegramchallenge2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CameraEditActivity extends Activity  implements View.OnClickListener, AngleSeekBar.OnAngleChangeListener {

    ImageCropper imageCropper;

    AngleSeekBar angleSeekBar;
    TextView angleSeekBarValue;
    ImageButton angleButton;

    Button cancelButton, resetButton, doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_edit);

        angleSeekBar = (AngleSeekBar) findViewById(R.id.camera_edit_angle_seekbar);
        angleSeekBarValue = (TextView) findViewById(R.id.camera_edit_angle_textview);
//        angleSeekBar.setOnSeekBarChangeListener(this);
        angleSeekBar.addOnAngleChangeListener(this);
        angleButton = (ImageButton) findViewById(R.id.angle_button);

        cancelButton = (Button) findViewById(R.id.camera_edit_cancel_button);
        resetButton = (Button) findViewById(R.id.camera_edit_reset_button);
        doneButton = (Button) findViewById(R.id.camera_edit_done_button);

        cancelButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);

        imageCropper = (ImageCropper) findViewById(R.id.image_cropper);

    }

    @Override
    public void onClick(View v) {
        if (v==resetButton) {
            angleSeekBar.setProgress(450);
        } else
        if (v==cancelButton) {
            finish();
//            overridePendingTransition(R.anim.pending_anim_enter,R.anim.pending_anim_exit);
        } else
        if (v==doneButton) {
            finish();
//            overridePendingTransition(R.anim.pending_anim_enter,R.anim.pending_anim_exit);
        }
    }

    @Override
    public void onAngleChange(float angle) {
        angleSeekBarValue.setText(String.format(Locale.ENGLISH,"%.1f",angle)+(char)0x00B0);
    }

    final static int ORIENTATION_0 = 0;
    final static int ORIENTATION_270 = 270;
    final static int ORIENTATION_180 = 180;
    final static int ORIENTATION_90 = 90;
    private int orientation = ORIENTATION_0;

    public void rotateButtonClick(View view) {
        int prevOrientation;

        if(orientation+90>360) {
            prevOrientation = ORIENTATION_0;
            orientation = ORIENTATION_0 + 90;
        } else {
            prevOrientation = orientation;
            orientation = orientation + 90;
        }

        Animation anim = new RotateAnimation(-prevOrientation,-orientation,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setFillAfter(true);
        anim.setDuration(250);
        anim.setInterpolator(new DecelerateInterpolator());
        angleButton.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.pending_anim_enter,R.anim.pending_anim_exit);
    }
}
