package com.mobile.strigoy.myapplication;

/**
 * Created cy Strigoy on 2017.02.27..
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import java.io.InputStream;

/**
 * Gif view display animated background gif image
 */
public class GifView extends View {

    private InputStream mInputStream;
    private Movie mMovie;
    private int mWidth, mHeight;
    private long mStart;
    private Context mContext;
    public int gifscale=9;//5 8

    public void setScale(int num){
        this.gifscale=num;
    }

    public GifView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs.getAttributeName(1).equals("background")) {
            int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
            setGifImageRes(id);
        }
    }

    private void init() {
        setFocusable(true);
        mMovie = Movie.decodeStream(mInputStream);

        mWidth = mMovie.width();
        mHeight = mMovie.height();

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth*gifscale, mHeight*gifscale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale((float)gifscale,(float)gifscale);
        long now = SystemClock.uptimeMillis();

        if (mStart == 0) {
            mStart = now;
        }

        if (mMovie != null) {

            int duration = mMovie.duration();
            if (duration == 0) {
                duration = 1000;
            }

            int relTime = (int) ((now - mStart) % duration);

            mMovie.setTime(relTime);

            mMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }

    public void setGifImageRes(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

    /*public boolean onTouchEvent(MotionEvent event){
        MainActivity.button.setVisibility(VISIBLE);
        //return super.onTouchEvent(event);
        return true;
    }*/
}