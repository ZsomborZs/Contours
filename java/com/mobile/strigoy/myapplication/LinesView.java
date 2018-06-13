package com.mobile.strigoy.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import com.example.strigoy.myapplication.R;
import com.mobile.strigoy.myapplication.utility.Line;
import com.mobile.strigoy.myapplication.utility.Colors;

import java.util.Random;

/**
 * LinesView contain multitouch interface
 * and display multiple lines
 */
public class LinesView extends View {
    private Context cont;
    private Random r;
    private Line[] line;
    private Paint paint;
    private Colors color;
    private SparseArray<PointF> mActivePointers;

    private float textSize = 70, textSize1 = 2;

    public float max_width, max_height; //display max size
    public String instruct; //instruction for user
    public static boolean instructOn = true;
    public boolean animatiOn = false, styleSet = true;

    public int style = 3;
    public static int mirror = 0;

    private int line_count = 10;
    public float animation_offset, animationVar = 0.f;
    private float animationVar1 = 2.5f;

    public LinesView(Context context) {
        super(context);
        this.cont = context;
        mActivePointers = new SparseArray<PointF>();
        r = new Random();
        paint = new Paint();
        color = new Colors();
        color.defaultRainbow();
        line = new Line[line_count];
        defaultLines();
    }

    public void defaultLines() {
        for (int i = 0; i < line_count; i++) line[i] = new Line();
    }

    //Display elements
    protected void onDraw(Canvas canvas) {
        if (style == 0) setBackground(ContextCompat.getDrawable(this.cont, R.drawable.background2));
        if (style == 1) if (styleSet) {
            setBackgroundColor(Color.WHITE);
            styleSet = false;
        }
        if (style == 3) if (styleSet) {
            setBackgroundColor(Color.BLACK);
            styleSet = false;
        }
        max_width = getWidth(); max_height = getHeight();

        if (style == 1) {
            paint.setStrokeWidth(3);
            if (max_width <= 1080 || max_height <= 1080) paint.setStrokeWidth(2);
            if (max_width <= 720 || max_height <= 720) paint.setStrokeWidth(1);
            paint.setColor(Color.rgb(0, 128, 255));
            for (int i1 = 1; i1 < 20; i1++) {
                if (getResources().getConfiguration().orientation == 1) {
                    canvas.drawLine(max_height / 20 * i1, 0, max_height / 20 * i1, max_height, paint);
                    canvas.drawLine(0, max_height / 20 * i1, max_width, max_height / 20 * i1, paint);
                } else {
                    canvas.drawLine(max_width / 20 * i1, 0, max_width / 20 * i1, max_height, paint);
                    canvas.drawLine(0, max_width / 20 * i1, max_width, max_width / 20 * i1, paint);
                }
            }
        }

        //Text
        if (instructOn) {
            paint.setColor(Color.rgb(255, 255, 255));
            if (max_width <= 1080 || max_height <= 1080) {
                textSize = 60; textSize1 = 1;
            }
            if (max_width <= 720 || max_height <= 720) {
                textSize = 50; textSize1 = 1;
            }
            paint.setTextSize(textSize);
            paint.setAlpha(100);//140
            instruct = cont.getString(R.string.instr5);
            canvas.drawText(instruct, getWidth() / 2 - instruct.length() / 2 * (textSize / 2 - textSize1), getHeight() - (getHeight() / 10) * 2, paint);
            instruct = cont.getString(R.string.instr6);
            canvas.drawText(instruct, getWidth() / 2 - instruct.length() / 2 * (textSize / 2 - textSize1), getHeight() - getHeight() / 10, paint);
        }

        //Draw lines
        paint.setStrokeWidth(20); //10 5
        paint.setColor(Color.rgb(0, 0, 0));
        for (int i1 = 0; i1 < line_count; i1++)
            for (int i = 0; i < line[i1].pointCount + 1; i++) {
                setColor();
                if (!line[i1].breakOn[i]) {
                    if (animatiOn) animation_offset = r.nextFloat() * animationVar;
                    canvas.drawLine(line[i1].p[i].x + animation_offset, line[i1].p[i].y + animation_offset, line[i1].p[i + 1].x + animation_offset, line[i1].p[i + 1].y + animation_offset, paint);
                    if (mirror == 1 || mirror == 4)
                        canvas.drawLine(max_width / 2 + (max_width / 2 - line[i1].p[i].x) + animation_offset, line[i1].p[i].y + animation_offset, max_width / 2 + (max_width / 2 - line[i1].p[i + 1].x) + animation_offset, line[i1].p[i + 1].y + animation_offset, paint);
                    if (mirror == 2 || mirror == 4)
                        canvas.drawLine(line[i1].p[i].x + animation_offset, max_height / 2 + (max_height / 2 - line[i1].p[i].y) + animation_offset, line[i1].p[i + 1].x + animation_offset, max_height / 2 + (max_height / 2 - line[i1].p[i + 1].y + animation_offset), paint);
                    if (mirror == 3 || mirror == 4)
                        canvas.drawLine(max_width / 2 + (max_width / 2 - line[i1].p[i].x) + animation_offset, max_height / 2 + (max_height / 2 - line[i1].p[i].y) + animation_offset, max_width / 2 + (max_width / 2 - line[i1].p[i + 1].x) + animation_offset, max_height / 2 + (max_height / 2 - line[i1].p[i + 1].y) + animation_offset, paint);
                }
            }

        if (animatiOn) { //long time = SystemClock.uptimeMillis() % 100L; 4000L
            SystemClock.sleep(100);
            animationVar += animationVar1;
            if (animationVar == 140.f || animationVar == 0.f) {
                animationVar1 *= -1.f;
            }
            invalidate();
        }
        color.defaultRainbow();
    }

    private void setColor() {
        for (int si = 0; si < 30; si++) paint.setColor(color.rainbow());
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

    //If the pointer up, break the line
    private void breakLine() {
        for (int i = 0; i < line_count; i++) {
            if (line[i].pointCount > -1) line[i].breakOn[line[i].pointCount] = true;
        }
    }

    //Touch events
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int maskedAction = event.getActionMasked();
        int pointerId = event.getPointerId(pointerIndex);

        //Single touch events
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            instructOn = false;
            breakLine();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //Build new line point
            if (line[0].pointCount < line[0].limit) {
                line[0].pointCount++;
                line[0].p[line[0].pointCount] = new PointF();
                line[0].p[line[0].pointCount].x = event.getX();
                line[0].p[line[0].pointCount].y = event.getY();
                line[0].p[line[0].pointCount + 1] = new PointF();
                line[0].p[line[0].pointCount + 1].x = line[0].p[line[0].pointCount].x;
                line[0].p[line[0].pointCount + 1].y = line[0].p[line[0].pointCount].y;
            }
        }

        //Multitouch events
        if (maskedAction == MotionEvent.ACTION_UP) {
            breakLine();
        }

        if (maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
            PointF f = new PointF();
            f.x = event.getX(pointerIndex);
            f.y = event.getY(pointerIndex);
            mActivePointers.put(pointerId, f);
            breakLine();
        }

        if (maskedAction == MotionEvent.ACTION_MOVE) {
            int size = event.getPointerCount();
            for (int i = 0; i < size; i++) {
                PointF point = mActivePointers.get(event.getPointerId(i));
                if (point != null) {
                    if (line[i].pointCount < line[i].limit) {
                        line[i].pointCount++;
                        line[i].p[line[i].pointCount] = new PointF();
                        line[i].p[line[i].pointCount].x = event.getX(i);
                        line[i].p[line[i].pointCount].y = event.getY(i);
                        line[i].p[line[i].pointCount + 1] = new PointF();
                        line[i].p[line[i].pointCount + 1].x = line[i].p[line[i].pointCount].x;
                        line[i].p[line[i].pointCount + 1].y = line[i].p[line[i].pointCount].y;
                    }
                }
            }
        }

        if (maskedAction == MotionEvent.ACTION_POINTER_UP) {
            breakLine();
            mActivePointers.remove(pointerId);
        }
        invalidate();
        return true;
    }
}
