package com.mobile.strigoy.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import com.example.strigoy.myapplication.R;
import com.mobile.strigoy.myapplication.utility.Colors;

/**
 * This view contain touch point interface and display curves
 */
public class DrawView extends View {
    private Context context;
    private Paint paint;
    private Colors color;
    private float point_size;

    public static final int max_point = 10000; //point array final size
    public float max_width, max_height; //display size
    public int pointCount = -1;

    public float[] cx = new float[max_point], cy = new float[max_point]; //x,y touch control points

    public static int touchRange = 60; //60 90 touch size range

    public static int mode = 0;
    public String instruct; //text
    boolean instructOn = true, instructOn2 = true; //enable text

    public boolean editOn = false, lineOn = true, animatiOn = false, style_set = true; //enable functions

    private int animationS1 = 0, animationS2 = 10; //for animation
    public int style = 0;

    private float radius = 20, textSize = 70, textSize1 = 2; //sizes

    private Contours contours; //curve functions

    public DrawView(Context context) {
        super(context);
        this.context = context;
        paint = new Paint();
        contours = new Contours();
        color = new Colors();
        defaultColor();

        //Initialize point
        for (int i = 0; i < max_point; i++) {
            cx[i] = 0;
            cy[i] = 0;
        }
        if (mode == 1) contours.hermiteMag = 0.25f;
    }

    //Make discrete value
    public void autoDiscrete() {
        for (int i = 0; i < pointCount + 2; i++) {
            cx[i] = discrete(cx[i]);
            cy[i] = -discrete(-cy[i]);
        }
    }

    //Based on the display set point size
    public void setPointSize() {
        max_width = ControlActivity.sizeA.x;
        max_height = ControlActivity.sizeA.y;
        if (getResources().getConfiguration().orientation == 1)
            point_size = max_width / 2 - (max_height / 20 * 6);
        else
            point_size = max_height / 2 - (max_width / 20 * 6);
    }

    //Different screen size require different text size
    private void setTextCustom() {
        paint.setColor(Color.rgb(255, 255, 255));
        if (style == 1 || style == 2) paint.setColor(Color.rgb(0, 0, 0));
        if (max_width <= 1080 || max_height <= 1080) {
            textSize = 60;
            textSize1 = 1;
        }
        if (max_width <= 720 || max_height <= 720) {
            textSize = 50;
            textSize1 = 1;
        }
        paint.setTextSize(textSize);
        paint.setAlpha(100);//140
        paint.setFakeBoldText(true);
    }

    //Display elements
    protected void onDraw(Canvas canvas) {
        //Set background
        if (style == 0)
            setBackground(ContextCompat.getDrawable(this.context, R.drawable.background2));
        else if (style_set) {
            style_set = false;
            if (style == 1 || style == 2) setBackgroundColor(Color.WHITE);
            if (style == 3) setBackgroundColor(Color.BLACK);
        }

        max_width = getWidth();
        max_height = getHeight();

        //long time = SystemClock.uptimeMillis() % 100L; //4000L

        //Animation
        if (animatiOn) {
            SystemClock.sleep(1);
            if (max_width <= 720 || max_height <= 720) animationS2 = 2;
            animationS1++;
            if (animationS1 == animationS2) {
                contours.resolution++;
                if (contours.resolution == 10) contours.resolution = 1;
                //paint.setAlpha(instr_a); //128 255 full
                animationS1 = 0;
            }
            invalidate();
        }

        //Draw text
        if (mode == 0 && pointCount > 34) instructOn = true;
        if (instructOn) {
            setTextCustom();
            if (!editOn && pointCount < 34) if (mode < 30) {
                instruct = context.getString(R.string.instr1);
                canvas.drawText(instruct, getWidth() / 2 - instruct.length() / 2 * (textSize / 2 - textSize1), getHeight() - (getHeight() / 10) * 2, paint);
                instruct = context.getString(R.string.instr2);
            } else instruct = context.getString(R.string.instr3);
            if (editOn) {
                paint.setColor(Color.rgb(255, 0, 0));
                paint.setAlpha(180);
                instruct = context.getString(R.string.instr4);
            }
            if (mode == 0 && pointCount > 34) {
                paint.setColor(Color.rgb(255, 0, 0));
                paint.setAlpha(180);
                instruct = context.getString(R.string.instr7);
            }
            canvas.drawText(instruct, getWidth() / 2 - instruct.length() / 2 * (textSize / 2 - textSize1), getHeight() - getHeight() / 10, paint);
        }

        if (instructOn2 && pointCount == 0 && mode < 30) {
            setTextCustom();
            paint.setColor(Color.rgb(255, 0, 0));
            paint.setAlpha(180);
            instruct = context.getString(R.string.instr8);
            canvas.drawText(instruct, getWidth() / 2 - instruct.length() / 2 * (textSize / 2 - textSize1), getHeight() - getHeight() / 10 * 2, paint);
        }

        //Draw grid
        if (style == 1 || style == 2) {
            paint.setStrokeWidth(3);
            if (max_width <= 1080 || max_height <= 1080) paint.setStrokeWidth(2);
            if (max_width <= 720 || max_height <= 720) paint.setStrokeWidth(1);
            paint.setColor(Color.rgb(0, 128, 255));
            paint.setAlpha(255);
            for (int i1 = 1; i1 < 20; i1++) {
                if (style == 2 && i1 == 10) paint.setColor(Color.rgb(255, 0, 0));
                else paint.setColor(Color.rgb(0, 128, 255));
                if (getResources().getConfiguration().orientation == 1) {
                    point_size = max_width / 2 - (max_height / 20 * 6);

                    if (style == 2 && i1 == 6) paint.setColor(Color.rgb(255, 0, 0));
                    else paint.setColor(Color.rgb(0, 128, 255));
                    canvas.drawLine(max_height / 20 * i1 + point_size, 0, max_height / 20 * i1 + point_size, max_height, paint);

                    if (style == 2 && i1 == 10) paint.setColor(Color.rgb(255, 0, 0));
                    else paint.setColor(Color.rgb(0, 128, 255));
                    canvas.drawLine(0, max_height / 20 * i1 + point_size, max_width, max_height / 20 * i1 + point_size, paint);
                } else {
                    point_size = max_height / 2 - (max_width / 20 * 6);
                    canvas.drawLine(max_width / 20 * i1 + point_size, 0, max_width / 20 * i1 + point_size, max_height, paint);

                    if (style == 2 && i1 == 6) paint.setColor(Color.rgb(255, 0, 0));
                    else paint.setColor(Color.rgb(0, 128, 255));
                    canvas.drawLine(0, max_width / 20 * i1 + point_size, max_width, max_width / 20 * i1 + point_size, paint);
                }
            }
        }
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(13);//15 20

        if (max_width <= 1080 || max_height <= 1080) {
            paint.setStrokeWidth(9);
            radius = 15;
        }
        if (max_width <= 720 || max_height <= 720) {
            paint.setStrokeWidth(4);
            radius = 10;
        }

        //Draw control points and control line
        paint.setAlpha(140);
        if (mode < 30 && lineOn)
            for (int i1 = 0; i1 < pointCount + 1; i1++) {
                if (style == 2) {
                    paint.setColor(Color.rgb(100, 100, 100)); paint.setAlpha(170);
                }
                if (style == 3) {
                    paint.setColor(Color.rgb(170, 170, 170)); paint.setAlpha(130);
                }
                canvas.drawLine(cx[i1], -cy[i1], cx[i1 + 1], -cy[i1 + 1], paint);

                if (style == 2) {
                    paint.setColor(Color.rgb(0, 255, 128)); paint.setAlpha(170);
                }
                if (style == 3) {
                    paint.setColor(Color.rgb(84, 200, 230)); paint.setAlpha(130);
                }
                canvas.drawCircle(cx[i1], -cy[i1], radius, paint);
            }

        defaultColor();

        //Calculate curves
        contours.update(cx, cy, pointCount);
        switch (mode) {
            case 0: contours.makeBezier(); break;
            case 1: contours.makeCatmullrom(); break;
            case 2: contours.makeOverhauser(); break;
            case 3: contours.makeBspline(); break;
            case 4: contours.makeLagrange(); break;
        }

        //Draw curves
        for (int gi = 0; gi < contours.c_pointCount; gi++) {
            setColor();
            canvas.drawLine(contours.gx[gi], -contours.gy[gi], contours.gx[gi + 1], -contours.gy[gi + 1], paint);
        }
    }

    //Set basic color
    private void defaultColor() {
        if (style != 3) color.defaultRainbow();
        else color.defaultRainbow();
    }

    //Adjust color and stroke width
    private void setColor() {
        for (int si = 0; si < 30; si++)
            if (style != 3) paint.setColor(color.rainbow());
            else paint.setColor(color.grayscale());
        paint.setStrokeWidth(20);//20
        if (max_width <= 1080 || max_height <= 1080) paint.setStrokeWidth(15);
        if (max_width <= 720 || max_height <= 720) paint.setStrokeWidth(10);

        paint.setAlpha(110);//128
        if (style == 1) paint.setAlpha(190);
        if (style == 2) {
            paint.setColor(Color.rgb(20, 20, 200)); //50
            paint.setAlpha(190);
        }
    }

    //Touch events
    public boolean onTouchEvent(MotionEvent event) {
        instructOn = false;
        int si1 = 0;

        //Insert new point when pointer down
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            boolean changePos = false;
            for (int i = 0; i < pointCount + 1; i++)
                if (event.getX() > cx[i] - touchRange && event.getX() < cx[i] + touchRange &&
                        -event.getY() > cy[i] - touchRange && -event.getY() < cy[i] + touchRange) {
                    if (!editOn) contours.grab[i] = true;
                    else si1 = i;
                    changePos = true;
                }
            if (editOn && changePos && pointCount > 0) {
                for (int i = si1; i < pointCount + 1; i++) {
                    cx[i] = cx[i + 1]; cy[i] = cy[i + 1];
                }
                pointCount--;
                cx[pointCount + 1] = cx[pointCount]; cy[pointCount + 1] = cy[pointCount];
            }

            if (pointCount < max_point && !changePos && !editOn && mode != 30 && cx[pointCount + 1] != event.getX() && cy[pointCount + 1] != -event.getY()) {
                pointCount++;
                cx[pointCount] = event.getX(); cy[pointCount] = -event.getY();
                if (style > 0) {
                    cx[pointCount] = discrete(cx[pointCount]);
                    cy[pointCount] = -discrete(-cy[pointCount]);
                }
                cx[pointCount + 1] = cx[pointCount]; cy[pointCount + 1] = cy[pointCount];
                contours.grab[pointCount] = true; contours.grab[pointCount + 1] = true;
            }//setContentView(mCustomDrawableView);
            return true;
        }

        //Moving point when pointer drag
        if (event.getAction() == MotionEvent.ACTION_MOVE && !editOn) {
            for (int i = 0; i < pointCount + 1; i++) {
                if (contours.grab[i]) {
                    cx[i] = event.getX(); cy[i] = -event.getY();
                    if (style == 1 || style == 2) {
                        cx[i] = discrete(cx[i]); cy[i] = -discrete(-cy[i]);
                    }
                    if (i == pointCount) {
                        cx[pointCount + 1] = cx[pointCount];
                        cy[pointCount + 1] = cy[pointCount];
                    }
                    break;
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (int i = 0; i < max_point; i++) contours.grab[i] = false;
        }

        invalidate();
        return super.onTouchEvent(event);
    }

    //Set animation resolution
    public void setAnimation(int new_resolution) {
        contours.resolution = new_resolution;
    }

    //Default settings
    public void clear() {
        pointCount = -1;
        contours.c_pointCount = -1;
        editOn = false;
        instructOn2 = false;
    }

    //Calculate discrete grid coordinate
    public float discrete(float num) {
        float dimension, d0, d1;
        if (getResources().getConfiguration().orientation == 1) dimension = max_height / 20;
        else dimension = max_width / 20;

        d0 = (int) (num / dimension);
        if (d0 == 0) d0 = 1; //board

        d1 = num % dimension;
        if (d1 >= dimension / 2) d0++;
        return d0 * dimension + point_size;
    }
}