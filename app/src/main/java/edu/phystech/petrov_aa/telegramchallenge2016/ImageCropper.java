package edu.phystech.petrov_aa.telegramchallenge2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class ImageCropper extends ImageView {

    private Paint paint0;
    private Paint paint;
    private Paint paint2;
    private Paint paint3;

    private Corner[] corners;

    private float corner_offset_x = 0, corner_offset_y = 0;
    private Corner movingCorner = null;
    private boolean movingCrop = false;

    float density;
    int heightScreen;

    int width;
    int height;

    public ImageCropper(Context context) {
        super(context);
        init();
    }

    public ImageCropper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCropper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private GestureDetector gestureDetector;

    private void init() {
        paint0 = new Paint();
        paint0.setColor(Color.BLACK);
        paint0.setAlpha(127);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setAlpha(127);
        paint2.setStrokeWidth(3);
        paint3 = new Paint();
        paint3.setStrokeWidth(3);
        paint3.setColor(Color.WHITE);
        paint3.setAlpha(127);
        paint3.setStyle(Paint.Style.STROKE);

        corners = new Corner[4];
        int w = (int)(getWidth()*0.8);
        int h = (int)(getHeight()*0.8);
        density = getResources().getDisplayMetrics().density;
        heightScreen = getResources().getSystem().getDisplayMetrics().heightPixels;

        corners[0] = (new Corner(150,150));
        corners[1] = (new Corner(500,150));
        corners[2] = (new Corner(500,500));
        corners[3] = (new Corner(150,500));

        corners[0].setRef(corners[3],corners[1]);
        corners[1].setRef(corners[2],corners[0]);
        corners[2].setRef(corners[1],corners[3]);
        corners[3].setRef(corners[0],corners[2]);

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(movingCorner!=null) {
                    movingCorner.setX(Math.round(e2.getX()));
                    movingCorner.setY(Math.round(e2.getY()));
                    invalidate();
                }
                return true;
            }
        };
        gestureDetector = new GestureDetector(getContext(), gestureListener);
        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gestureDetector.onTouchEvent(motionEvent);
//                return true;
//            }
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    for(int i=0; i<corners.length; i++)
                        if( Math.abs(corner_offset_x=(corners[i].getX() - event.getX()))<=corners[i].getR() &&
                                Math.abs(corner_offset_y=(corners[i].getY() - event.getY()))<=corners[i].getR()){
                            movingCorner = corners[i];
                            break;
                        }
                    if(movingCorner==null && event.getX() > corners[0].x && event.getY() < corners[2].y) {
                        movingCrop = true;
                    }
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    movingCorner = null;
                    movingCrop = false;
                } else if (event.getAction() == android.view.MotionEvent.ACTION_MOVE) {
                    if(movingCorner!=null) {

                        int newx = Math.round(event.getX()+corner_offset_x);
                        int newy = Math.round(event.getY()+corner_offset_y);

                        if(movingCorner==corners[0]) {
                            if (newx + 100 > corners[1].x) return true;
                            if (newy + 100 > corners[2].y) return true;
                        }
                        if(movingCorner==corners[1]) {
                            if (newx - 100 < corners[0].x) return true;
                            if (newy + 100 > corners[2].y) return true;
                        }
                        if(movingCorner==corners[2]) {
                            if (newx - 100 < corners[0].x) return true;
                            if (newy - 100 < corners[1].y) return true;
                        }
                        if(movingCorner==corners[3]) {
                            if (newx + 100 > corners[1].x) return true;
                            if (newy - 100 < corners[1].y) return true;
                        }

                        if(newx>=width)
                            movingCorner.setX(width);
                        else if(newx<=0)
                            movingCorner.setX(0);
                        else
                            movingCorner.setX(newx);
                        if(newy>=height)
                            movingCorner.setY(height);
                        else if(newy<=0)
                            movingCorner.setY(0);
                        else
                            movingCorner.setY(newy);
                        invalidate();
                    } else if(movingCrop) {
                        invalidate();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        float maxHeight = heightScreen - density*150;

        int width = getMeasuredWidth();
        int height = width*4/3;
        if(height>maxHeight) {
            height = (int)maxHeight;
            width = height*3/4;
        }

        this.width = width;
        this.height = height;


        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(0,0,width,corners[0].getY(),paint0);
        canvas.drawRect(corners[1].x,corners[1].y,width,corners[2].y,paint0);
        canvas.drawRect(0,corners[2].getY(),width,height,paint0);
        canvas.drawRect(0,corners[0].y,corners[3].x,corners[3].y,paint0);

//        paint.setColor(Color.RED);
//        canvas.drawCircle(corners[0].getX(),corners[0].getY(),50,paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawCircle(corners[1].getX(),corners[1].getY(),50,paint);
//        paint.setColor(Color.BLUE);
//        canvas.drawCircle(corners[2].getX(),corners[2].getY(),50,paint);
//        paint.setColor(Color.MAGENTA);
//        canvas.drawCircle(corners[3].getX(),corners[3].getY(),50,paint);

        float lineX1 = (corners[1].x - corners[0].x)/3f;
        float lineX2 = corners[0].x + lineX1*2f; lineX1 += corners[0].x;
        float lineY1 = (corners[3].y - corners[1].y)/3f;
        float lineY2 = corners[0].y + lineY1*2f; lineY1 += corners[0].y;

        canvas.drawLine(lineX1,corners[0].y,lineX1,corners[3].y,paint2);
        canvas.drawLine(lineX2,corners[0].y,lineX2,corners[3].y,paint2);
        canvas.drawLine(corners[0].x,lineY1,corners[1].x,lineY1,paint2);
        canvas.drawLine(corners[0].x,lineY2,corners[1].x,lineY2,paint2);

        canvas.drawLine(corners[0].x,corners[0].y+1,corners[1].x,corners[1].y+1,paint3);
        canvas.drawLine(corners[3].x,corners[3].y-2,corners[2].x,corners[2].y-2,paint3);
        canvas.drawLine(corners[0].x+1,corners[0].y-1,corners[3].x+1,corners[3].y,paint3);
        canvas.drawLine(corners[1].x-2,corners[1].y-1,corners[2].x-2,corners[2].y,paint3);

        /*
        I decided to make corners inside because if you position them as outline when the image
        is full-screen then corners will be outside the viewport and users might be confused
         */

        canvas.drawRect(corners[0].x,corners[0].y,corners[0].x+40,corners[0].y+10,paint);
        canvas.drawRect(corners[0].x,corners[0].y+10,corners[0].x+10,corners[0].y+40,paint);
        canvas.drawRect(corners[1].x-40,corners[1].y,corners[1].x,corners[1].y+10,paint);
        canvas.drawRect(corners[1].x-10,corners[1].y,corners[1].x,corners[1].y+40,paint);
        canvas.drawRect(corners[2].x-40,corners[2].y-10,corners[2].x,corners[2].y,paint);
        canvas.drawRect(corners[2].x-10,corners[2].y-40,corners[2].x,corners[2].y,paint);
        canvas.drawRect(corners[3].x,corners[3].y-40,corners[3].x+10,corners[3].y,paint);
        canvas.drawRect(corners[3].x,corners[3].y-10,corners[3].x+40,corners[3].y,paint);

        super.onDraw(canvas);
    }

    private class Corner {
        int x, y;
        Corner refX, refY;
        int rad = 50;
        Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
        void setRef(Corner refX, Corner refY) {
            this.refX = refX;
            this.refY = refY;
        }
        int getX() {
            return x;
        }
        int getY() {
            return y;
        }
        void setX(int x) {
            this.x = x;
            refX.x = x;
        }
        void setY(int y) {
            this.y = y;
            refY.y = y;
        }
        float getR() {
            return rad;
        }
    }
}
