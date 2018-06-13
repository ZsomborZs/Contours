package com.mobile.strigoy.myapplication.utility;

import android.graphics.Color;

/**
 * Calculate colors
 */
public class Colors {
    private int[] color = new int[3];
    private int colorShift = 1;

    public void defaultRainbow() {
        colorShift = 1;
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
    }

    public void defaultGrayscale() {
        colorShift = 1;
        color[0] = 0;
        color[1] = 255;
        color[2] = 0;
    }

    //Calculate continuous rainbow color
    public int rainbow() {
        if (colorShift == 1) {
            if (color[2] < 255) color[2]++;
            else if (color[0] > 0) color[0]--;
            else if (color[1] < 255) color[1]++;
            else colorShift *= -1;
        } else if (color[2] > 0) color[2]--;
        else if (color[0] < 255) color[0]++;
        else if (color[1] > 0) color[1]--;
        else colorShift *= -1;
        return Color.rgb(color[0], color[1], color[2]);
    }

    //Calculate continuous grayscale color
    public int grayscale() {
        if (colorShift == 1) {
            if (color[0] < 200) {
                color[0]++;
                color[2]++;
            } else colorShift *= -1;
        } else if (color[0] > 0) {
            color[0]--;
            color[2]--;
        } else colorShift *= -1;
        return Color.rgb(color[0], color[1], color[2]);
    }
}
