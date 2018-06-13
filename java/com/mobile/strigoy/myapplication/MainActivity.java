package com.mobile.strigoy.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

import android.graphics.Point;
import android.util.TypedValue;
import com.example.strigoy.myapplication.R;

/**
 * Main program entry
 * Menu with animated background
 */
public class MainActivity extends Activity {
    private int mode=0;
    private GifView myGifView;
    private RelativeLayout mainLayouth;
    private FrameLayout mainLayout;
    private Button button[]=new Button[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        if(size.x<=720)DrawView.touchRange =60;
        if(size.x<=320)DrawView.touchRange =30;

        /**
         * Check SDK version
         * 22 and upper SDK version display animated background
         */
        if(Integer.valueOf(android.os.Build.VERSION.SDK_INT)==19){LinesView.mirror =4;LinesView.instructOn =false;}
        if(getResources().getConfiguration().orientation==1){
            setContentView(R.layout.activity_mainh);
            mainLayouth=(RelativeLayout)findViewById(R.id.activity_mainh);
            if(size.x<=320){
                TextView tv=(TextView)findViewById(R.id.textViewh);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.f);
            }
            if(Integer.valueOf(android.os.Build.VERSION.SDK_INT)>22) {
                myGifView=(GifView)findViewById(R.id.myGifImageh);
                myGifView.setGifImageRes(R.drawable.gif_horizontal);
                myGifView.setScale(9);
                if(size.x<=720)myGifView.setScale(7);
                if(size.x>1080)myGifView.setScale(12);
            }else {
                mainLayouth.setBackgroundResource(R.drawable.background3);
                TextView tv=(TextView)findViewById(R.id.textViewh);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55.f);

                button[0]=(Button)findViewById(R.id.buttonh);
                button[1]=(Button)findViewById(R.id.button2h);
                button[2]=(Button)findViewById(R.id.button3h);
                button[3]=(Button)findViewById(R.id.button4h);
                button[4]=(Button)findViewById(R.id.button6h);
                for(int i=0;i<5;i++)button[i].setAlpha(0.8f);
            }
        }else{
            setContentView(R.layout.activity_main);
            mainLayout=(FrameLayout)findViewById(R.id.activity_main);
            if(Integer.valueOf(android.os.Build.VERSION.SDK_INT)>22) {
            myGifView=(GifView)findViewById(R.id.myGifImage);
            myGifView.setGifImageRes(R.drawable.rajzgifw);
            myGifView.setScale(7);

            if(size.y<=720)myGifView.setScale(5);
            if(size.y>1080)myGifView.setScale(9);}else mainLayout.setBackgroundResource(R.drawable.background3);
        }

        /*button[0]=(Button)findViewById(R.id.buttonh);button[0].setVisibility(View.GONE);

        //One touch detect
        myGifView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for(int i=0;i<5;i++)button[i].setVisibility(View.VISIBLE);
                return true;
            }
        });*/
    }

    //Start control activity
    public void startFull(){
        Intent intent=new Intent(this,ControlActivity.class);
        intent.putExtra("mode",mode);
        startActivity(intent);

        //Activity change transition
        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }

    /**
     * Button onclick
     * 5 menu button available
     */
    public void mode0(View view){
        mode=0;
        startFull();
    }

    public void mode1(View view){
        mode=1;
        startFull();
    }

    public void mode2(View view){
        mode=2;
        startFull();
    }

    public void mode3(View view){
        mode=3;
        startFull();
    }
    public void mode4(View view){
        mode=4;
        startFull();
    }
}
