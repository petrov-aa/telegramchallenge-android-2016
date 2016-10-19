package edu.phystech.petrov_aa.telegramchallenge2016;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import edu.phystech.petrov_aa.telegramchallenge2016.R;


public class CameraFlashSelector{

    private Context context;

    private ImageButton flashAuto, flashOn, flashOff;
    private int pointer = MODE_AUTO, limit = 3;

    public final static int MODE_AUTO = 0, MODE_ON = 1, MODE_OFF = 2;

    public CameraFlashSelector(Context context, ImageButton flashAuto, ImageButton flashOn, ImageButton flashOff) {
        this.context = context;
        this.flashAuto = flashAuto;
        this.flashOn = flashOn;
        this.flashOff = flashOff;
        flashOn.setVisibility(View.INVISIBLE);
        flashOff.setVisibility(View.INVISIBLE);
    }

    public int getMode() {
        return pointer;
    }

    public int next() {
        pointer = pointer + 1 == limit ? 0 : pointer + 1;

        switch (pointer) {
            case MODE_AUTO : {
                flashOff.startAnimation(AnimationUtils.loadAnimation(context, R.anim.camera_flash_remove));
                flashAuto.startAnimation(AnimationUtils.loadAnimation(context,R.anim.camera_flash_appear));
            }
            break;
            case MODE_ON : {
                flashAuto.startAnimation(AnimationUtils.loadAnimation(context,R.anim.camera_flash_remove));
                flashOn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.camera_flash_appear));
            }
            break;
            case MODE_OFF : {
                flashOn.startAnimation(AnimationUtils.loadAnimation(context,R.anim.camera_flash_remove));
                flashOff.startAnimation(AnimationUtils.loadAnimation(context,R.anim.camera_flash_appear));
            }
            break;
        }

        return pointer;
    }
}
