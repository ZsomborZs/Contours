package com.mobile.strigoy.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.example.strigoy.myapplication.R;

/**
 * Control activity handle the control layout with buttons
 */
public class ControlActivity extends Activity {
    //Views
    private DrawView drawView;
    private LinesView linesView;

    //Buttons
    private ToggleButton line_button, edit_button, animation_button, animation_button2;
    private Button style_button, delete_button, mirror_button, style_button2, clean_button, change_button;

    private RelativeLayout layout;
    private float alpha = 0.f;
    private boolean instructOn = true;
    private int mode;
    public static Point sizeA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getScreenSize();

        //Set title
        Bundle extras = getIntent().getExtras();
        if (extras == null) drawView.mode = 0;
        else drawView.mode = extras.getInt("mode");

        mode = extras.getInt("mode");
        switch (drawView.mode) {
            case 0: setTitle(getString(R.string.b_bezier)); break;
            case 1: setTitle(getString(R.string.b_catmullrom)); break;
            case 2: setTitle(getString(R.string.b_lagrange)); break;
            case 3: setTitle(getString(R.string.b_hermite)); break;
        }

        //getSupportActionBar().hide();

        /**
         * Set activity layout based on the mode
         * Get buttons handle to change properties
         */
        if (extras.getInt("mode") != 4) {
            setContentView(R.layout.activity_main2);
            layout = (RelativeLayout) findViewById(R.id.activity_main2);

            drawView = new DrawView(this);
            drawView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.addView(drawView);

            //Get button handle from the view
            animation_button = (ToggleButton) findViewById(R.id.animButton);
            style_button = (Button) findViewById(R.id.styleButton);
            line_button = (ToggleButton) findViewById(R.id.lineButton);
            edit_button = (ToggleButton) findViewById(R.id.editButton);
            clean_button = (Button) findViewById(R.id.clearButton);
            change_button = (Button) findViewById(R.id.changeButton);

            //Get saved state (e.g orientation change)
            if (savedInstanceState != null) {
                drawView.style = savedInstanceState.getInt("mystyle");
                if (drawView.mode <= 30) drawView.instructOn = false;
                drawView.cx = savedInstanceState.getFloatArray("pointsX");
                drawView.cy = savedInstanceState.getFloatArray("pointsY");
                drawView.pointCount = savedInstanceState.getInt("pointCount");
                drawView.editOn = savedInstanceState.getBoolean("editP");
                drawView.lineOn = savedInstanceState.getBoolean("lineP");
                drawView.animatiOn = savedInstanceState.getBoolean("animP");

                if (drawView.animatiOn) {
                    drawView.setAnimation(1);
                    animation_button.setAlpha(0.4f + alpha);
                }
                if (drawView.lineOn) line_button.setAlpha(0.4f + alpha);
                if (drawView.editOn) {
                    edit_button.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    edit_button.setAlpha(0.4f + alpha);
                }
                for (int ip = 0; ip < drawView.pointCount + 2; ip++) {
                    drawView.cx[ip] = drawView.cx[ip] + sizeA.x / 2;
                    drawView.cy[ip] = -(drawView.cy[ip] + sizeA.y / 2);
                }
                if (drawView.style == 2) {
                    drawView.setPointSize();
                    drawView.autoDiscrete();
                }
                drawView.instructOn2 = false;
                drawView.invalidate();
            }
        } else {
            setContentView(R.layout.activity_main2b);
            layout = (RelativeLayout) findViewById(R.id.activity_main2b);
            linesView = new LinesView(this);
            layout.addView(linesView);

            animation_button2 = (ToggleButton) findViewById(R.id.animButtonB);
            style_button2 = (Button) findViewById(R.id.styleButtonB);
            delete_button = (Button) findViewById(R.id.DeleteButton);
            mirror_button = (Button) findViewById(R.id.MirrorButton);
            layout.setBackgroundResource(0);

            //Get saved state
            if (savedInstanceState != null) {
                linesView.style = savedInstanceState.getInt("mystyle");
                linesView.instructOn = false;
                linesView.mirror = savedInstanceState.getInt("mirror");
                linesView.animatiOn = savedInstanceState.getBoolean("animP");
                if (linesView.animatiOn) {
                    linesView.animationVar = 0.f;
                    animation_button2.setAlpha(0.4f + alpha);
                }
                if (linesView.mirror > 0) mirror_button.setAlpha(0.4f + alpha);
                else mirror_button.setAlpha(0.3f + alpha);
                switch (linesView.mirror) {
                    case 0:
                        mirror_button.setText(getString(R.string.b_m_of));
                        break;
                    case 1:
                        mirror_button.setText(getString(R.string.b_m_v));
                        break;
                    case 2:
                        mirror_button.setText(getString(R.string.b_m_h));
                        break;
                    case 3:
                        mirror_button.setText(getString(R.string.b_m_d));
                        break;
                    case 4:
                        mirror_button.setText(getString(R.string.b_m_q));
                        break;
                }
                linesView.invalidate();
            }
        }

    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        if(mode==4)animation_button2.setChecked(false);
        else animation_button.setChecked(false);
    }*/

    public void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        sizeA = new Point();
        display.getSize(sizeA);
    }

    /**
     * Save states
     *  Points properties
     *  and togglebutton states
     */
    protected void onSaveInstanceState(Bundle myBundle) {
        super.onSaveInstanceState(myBundle);
        if (drawView.mode != 4) {
            myBundle.putInt("mystyle", drawView.style);

            //Recalculate points coordinates
            int fit_size = 1;
            for (int ip = 0; ip < drawView.pointCount + 2; ip++) {
                drawView.cx[ip] = (drawView.cx[ip] - sizeA.x / 2);
                drawView.cy[ip] = (-drawView.cy[ip] - sizeA.y / 2);
                if (sizeA.y > sizeA.x) {
                    if (Math.abs(drawView.cy[ip]) > sizeA.x / 2) fit_size = 2;
                } else if (Math.abs(drawView.cx[ip]) > sizeA.y / 2) fit_size = 2;
            }
            if (fit_size == 2) for (int ip = 0; ip < drawView.pointCount + 2; ip++) {
                drawView.cx[ip] /= 2;
                drawView.cy[ip] /= 2;
            }
            myBundle.putFloatArray("pointsX", drawView.cx);
            myBundle.putFloatArray("pointsY", drawView.cy);
            myBundle.putInt("pointCount", drawView.pointCount);
            myBundle.putBoolean("editP", drawView.editOn);
            myBundle.putBoolean("lineP", drawView.lineOn);
            myBundle.putBoolean("animP", drawView.animatiOn);
        } else {
            myBundle.putInt("mystyle", linesView.style);
            myBundle.putInt("mirror", linesView.mirror);
            myBundle.putBoolean("animP", linesView.animatiOn);
        }
    }

    //Edit button onclick
    public void setEdit(View view) {
        if (mode < 4) {
            if (!drawView.editOn) {
                drawView.editOn = true;
                //edit_button.setTextColor(Color.RED);
                edit_button.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                edit_button.setAlpha(0.4f + alpha);
                drawView.lineOn = true;
                line_button.setChecked(true);//true
                line_button.setAlpha(0.4f + alpha);
                //line_button.setEnabled(false);
                if (instructOn) {
                    drawView.instructOn = true;
                    instructOn = false;
                }
            } else {
                drawView.editOn = false;
                //edit_button.setTextColor(Color.DKGRAY);
                edit_button.setAlpha(0.3f + alpha);
                edit_button.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                //line_button.setEnabled(true);
                drawView.instructOn = false;
            }
            drawView.invalidate();
        } else {
            linesView.defaultLines();
            linesView.invalidate();
        }
    }

    //Line button onclick
    public void setLine(View view) {
        if (mode < 4) {
            if (!drawView.lineOn) {
                drawView.lineOn = true;
                line_button.setAlpha(0.4f + alpha);
            } else {
                drawView.lineOn = false;
                line_button.setAlpha(0.3f + alpha);
            }
            drawView.invalidate();
        } else {
            linesView.mirror++;
            if (linesView.mirror == 5) linesView.mirror = 0;
            if (linesView.mirror > 0) mirror_button.setAlpha(0.4f + alpha);
            else mirror_button.setAlpha(0.3f + alpha);
            switch (linesView.mirror) {
                case 0:
                    mirror_button.setText(getString(R.string.b_m_of));
                    break;
                case 1:
                    mirror_button.setText(getString(R.string.b_m_v));
                    break;
                case 2:
                    mirror_button.setText(getString(R.string.b_m_h));
                    break;
                case 3:
                    mirror_button.setText(getString(R.string.b_m_d));
                    break;
                case 4:
                    mirror_button.setText(getString(R.string.b_m_q));
                    break;
            }
            linesView.invalidate();
        }
    }

    //Animation button onclick
    public void setAnimation(View view) {
        if (mode < 4) {
            if (!drawView.animatiOn) {
                drawView.setAnimation(1);
                drawView.animatiOn = true;
                animation_button.setAlpha(0.4f + alpha);
            } else {
                drawView.animatiOn = false;
                drawView.setAnimation(65);
                animation_button.setAlpha(0.3f + alpha);
            }
            drawView.invalidate();
        } else {
            if (!linesView.animatiOn) {
                linesView.animationVar = 0.f;
                linesView.animatiOn = true;
                animation_button2.setAlpha(0.4f + alpha);
            } else {
                linesView.animatiOn = false;
                animation_button2.setAlpha(0.3f + alpha);
                linesView.animation_offset = 0.f;
            }
            linesView.invalidate();
        }
    }

    //Style button onclick
    public void setStyle(View view) {
        if (mode != 4) {
            drawView.style++;
            switch (drawView.style) {
                case 1:
                    //getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                    //layout.setBackgroundResource(0);
                    //drawView.setBackgroundColor(Color.WHITE);
                    drawView.style_set = true;
                    alpha = 0.4f;
                    line_button.setAlpha(0.4f + alpha);
                    edit_button.setAlpha(0.4f + alpha);
                    animation_button.setAlpha(0.4f + alpha);
                    clean_button.setAlpha(0.4f + alpha);
                    change_button.setAlpha(0.4f + alpha);
                    style_button.setAlpha(0.8f);
                    break;
                case 2:
                    drawView.instructOn = false;
                    drawView.autoDiscrete();
                    break;
                case 3:
                    drawView.style_set = true;
                    alpha = 0.f;
                    line_button.setAlpha(0.4f + alpha);
                    edit_button.setAlpha(0.4f + alpha);
                    animation_button.setAlpha(0.4f + alpha);
                    clean_button.setAlpha(0.4f + alpha);
                    change_button.setAlpha(0.4f + alpha);
                    style_button.setAlpha(0.4f);
                    break;
                case 4:
                    drawView.style = 0;
                    break;
            }
            drawView.invalidate();
        } else {
            linesView.style++;
            switch (linesView.style) {
                case 0:
                    alpha = 0.f;
                    animation_button2.setAlpha(0.4f + alpha);
                    style_button2.setAlpha(0.4f);
                    break;
                case 1:
                    linesView.styleSet = true;
                    alpha = 0.4f;
                    animation_button2.setAlpha(0.4f + alpha);
                    style_button2.setAlpha(0.8f);
                    break;
                case 2:
                    break;
                case 3:
                    linesView.styleSet = true;
                    break;
                case 4:
                    linesView.style = 0;
                    break;
            }
            linesView.invalidate();
        }

    }

    //Clear button onclick
    public void setClear(View view) {
        drawView.clear();
        edit_button.setAlpha(0.3f + alpha);
        edit_button.setChecked(false);
        edit_button.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        drawView.invalidate();
    }

    //Change button onclick
    public void setChange(View view) {
        drawView.mode++;
        if (drawView.mode == 5) drawView.mode = 0;
        drawView.instructOn = false;
        drawView.invalidate();
    }
}
