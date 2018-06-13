package com.mobile.strigoy.myapplication;

import com.mobile.strigoy.myapplication.utility.Tools;

/**
 * All math functions to calculate contours
 */
public class Contours {
    float[] x = new float[DrawView.max_point], y = new float[DrawView.max_point]; //coordinates
    private int pointCount = -1;

    //Control and curve points
    private float[] cx = new float[DrawView.max_point], cy = new float[DrawView.max_point];
    public float[] gx = new float[DrawView.max_point], gy = new float[DrawView.max_point];

    public boolean[] grab = new boolean[DrawView.max_point]; //when point grabbed

    public int resolution = 65; //0 16 15 35 65 curve resolution (to)
    public float t, tot; //accesories
    public int c_pointCount = -1;

    public int hermiteCount = 3, hermite_pCount = (hermiteCount * 2) + 1;
    public float[] hx = new float[hermite_pCount], hy = new float[hermite_pCount];
    public float[] ex = new float[hermiteCount + 1], ey = new float[hermiteCount + 1];

    public float hermiteMag = 2.f; //Hermite magnitude 1.f 2.f 3.f

    //Value update before calculate
    public void update(float[] cx, float[] cy, int pdb) {
        this.cx = cx.clone();
        this.cy = cy.clone();
        this.pointCount = pdb;
    }

    /**
     * All functions takes the control points (b)
     * and calculate the curve points (g)
     */
    protected void makeBezier() {
        if (pointCount != -1) {
            t = 0.f;
            tot = 1.f / (float) resolution;
            c_pointCount = resolution;
            for (int i1 = 0; i1 < resolution + 1; i1++) {
                gx[i1] = 0.f;
                gy[i1] = 0.f;
                for (int i = 0; i < pointCount + 1; i++) {
                    gx[i1] += bezier(cx, pointCount, i);
                    gy[i1] += bezier(cy, pointCount, i);
                }
                t += tot;
            }
        }
    }

    protected void makeCatmullrom() {
        hermiteMag = 0.25f;
        if (pointCount > 1) {
            hx[0] = cx[0]; hy[0] = cy[0]; hx[1] = cx[1]; hy[1] = cy[1];
            ex[0] = cx[1]; ey[0] = cy[1]; ex[1] = cx[2]; ey[1] = cy[2];
            getHermite(0);
            for (int i1 = 0; i1 < resolution + 1; i1++) {
                gx[i1] = x[i1]; gy[i1] = y[i1];
            }
        }
        c_pointCount = resolution;
        for (int i2 = 1; i2 < pointCount - 1; i2++) {
            t = 0.f;
            tot = 1.f / (float) resolution;
            for (int i1 = 0; i1 < resolution + 1; i1++) {
                c_pointCount++;
                gx[c_pointCount] = catmullrom(cx, i2 - 1);
                gy[c_pointCount] = catmullrom(cy, i2 - 1);
                t += tot;
            }
        }
        if (pointCount > 1) {
            hx[0] = cx[pointCount]; hy[0] = cy[pointCount];
            hx[1] = cx[pointCount - 1]; hy[1] = cy[pointCount - 1];
            ex[0] = cx[pointCount - 1]; ey[0] = cy[pointCount - 1];
            ex[1] = cx[pointCount - 2]; ey[1] = cy[pointCount - 2];
            getHermite(0);
            for (int i1 = resolution; i1 > -1; i1--) {
                c_pointCount++;
                gx[c_pointCount] = x[i1];
                gy[c_pointCount] = y[i1];
            }
        }
    }

    protected void makeOverhauser() {
        if (pointCount != -1) {
            c_pointCount = -1;
            for (int i2 = -1; i2 < pointCount - 1; i2++) {
                overhauser(cy, i2);
                for (int i1 = 0; i1 < 4; i1++) y[i1] = x[i1];
                overhauser(cx, i2);
                t = 0.f;
                tot = 1.f / (float) resolution;
                for (int i1 = 0; i1 < resolution + 1; i1++) {
                    c_pointCount++;
                    gx[c_pointCount] = 0.f; gy[c_pointCount] = 0.f;
                    for (int i = 0; i < 4; i++) {
                        gx[c_pointCount] += bezier(x, 3, i);
                        gy[c_pointCount] += bezier(y, 3, i);
                    }
                    t += tot;
                }
            }
        }
    }

    protected void makeBspline() {
        if (pointCount > 2) {
            c_pointCount = resolution;
            for (int i2 = 2; i2 < pointCount; i2++) {
                t = 0.f;
                tot = 1.f / (float) resolution;
                for (int i1 = 0; i1 < resolution + 1; i1++) {
                    c_pointCount++;
                    gx[c_pointCount] = bspline(cx, i2); gy[c_pointCount] = bspline(cy, i2);
                    t += tot;
                    if (i2 == 2) {
                        if (i1 == 0) {
                            hx[0] = cx[0]; hy[0] = cy[0];
                            hx[1] = gx[c_pointCount]; hy[1] = gy[c_pointCount];
                        }
                        if (i1 == 2) {
                            ex[1] = hx[1] + (gx[c_pointCount] - hx[1]) * 30.f;
                            ey[1] = hy[1] + (gy[c_pointCount] - hy[1]) * 30.f;
                            ex[0] = hx[0] + (gx[c_pointCount] - hx[0]) * 0.7f;
                            ey[0] = hy[0] + (gy[c_pointCount] - hy[0]) * 0.7f;
                        }
                    }
                    if (i2 == pointCount - 1) {
                        if (i1 == resolution - 2) {
                            hx[2] = cx[pointCount]; hy[2] = cy[pointCount];
                            ex[3] = gx[c_pointCount]; ey[3] = gy[c_pointCount];
                        }
                        if (i1 == resolution) {
                            hx[3] = gx[c_pointCount]; hy[3] = gy[c_pointCount];
                            ex[3] = hx[3] + (ex[3] - hx[3]) * 30.f;
                            ey[3] = hy[3] + (ey[3] - hy[3]) * 30.f;
                            ex[2] = hx[2] + (gx[c_pointCount] - hx[2]) * 0.7f;
                            ey[2] = hy[2] + (gy[c_pointCount] - hy[2]) * 0.7f;
                        }
                    }
                }
            }
            getHermite(0);
            for (int i = 0; i < resolution + 1; i++) {
                gx[i] = x[i];
                gy[i] = y[i];
            }
            c_pointCount += resolution;
            getHermite(2);
            for (int i = 0; i < resolution + 1; i++) {
                gx[c_pointCount - i] = x[i]; gy[c_pointCount - i] = y[i];
            }
        }
    }

    protected void makeLagrange() {
        if (pointCount != -1) {
            tot = (cx[pointCount] - cx[0]) / (float) resolution;
            c_pointCount = 0;
            gx[0] = cx[0];
            gy[0] = cy[0];
            t = cx[0];
            for (int i1l = 1; i1l < resolution + 1; i1l++) {
                c_pointCount++;
                t += tot;
                gx[c_pointCount] = t;
                gy[c_pointCount] = lagrange(t, cx, cy, pointCount + 1);
            }
        }
    }

    private void getHermite(int pi) {
        t = 0.f;
        tot = 1.f / (float) resolution;
        for (int hi1 = 0; hi1 < resolution + 1; hi1++) {
            x[hi1] = hermite(hx, ex, pi);
            y[hi1] = hermite(hy, ey, pi);
            t += tot;
        }
    }

    public float bezier(float[] b, int bn, int bi) {
        return ((float) Tools.binom(bn, bi)) *
                Tools.flo(1.f - t, (bn - bi) - 1) *
                Tools.flo(t, bi - 1) *
                b[bi];
    }

    public float hermite(float[] p, float[] m, int s) {
        return (2.f * Tools.flo(t, 2) - 3.f * Tools.flo(t, 1) + 1.f) * p[0 + s] +
                (Tools.flo(t, 2) - 2.f * Tools.flo(t, 1) + t) * (m[0 + s] - p[0 + s]) * hermiteMag +//*2.f + //2.f magnitude
                (-2.f * Tools.flo(t, 2) + 3.f * Tools.flo(t, 1)) * p[1 + s] +
                (Tools.flo(t, 2) - Tools.flo(t, 1)) * (m[1 + s] - p[1 + s]) * hermiteMag;//* 2.f; //2.f magnitude
    }

    public void overhauser(float p[], int s) {
        float[] u = new float[]{1.f, 2.f, 3.f, 4.f};
        float vp1 = 0, vp0 = 0, vix = 0; //plus-minus half

        vp1 = (p[2 + s] - p[1 + s]) / (u[2] - u[1]);
        if (s > -1) vp0 = (p[1 + s] - p[0 + s]) / (u[1] - u[0]);
        vix = ((u[2] - u[1]) * vp0 + (u[1] - u[0]) * vp1) / (u[2] - u[0]);
        x[0] = p[1 + s];
        x[1] = (p[1 + s] + (1.f / 3.f) * (u[2] - u[1]) * vix);

        vp1 = (p[3 + s] - p[2 + s]) / (u[3] - u[2]);
        vp0 = (p[2 + s] - p[1 + s]) / (u[2] - u[1]);
        vix = ((u[3] - u[2]) * vp0 + (u[2] - u[1]) * vp1) / (u[3] - u[1]);
        x[2] = (p[2 + s] - (1.f / 3.f) * (u[3] - u[2]) * vix);
        x[3] = p[2 + s];
    }

    public float b_knots(int s) {
        if (s == -2)
            return ((1.f / 6.f) * (Tools.flo(-t, 2) + 3.f * Tools.flo(t, 1) - 3.f * t + 1.f)); //((1.f/6.f) * flo(1-t, 2));
        if (s == -1) return ((1.f / 6.f) * (3.f * Tools.flo(t, 2) - 6.f * Tools.flo(t, 1) + 4.f));
        if (s == 0)
            return ((1.f / 6.f) * (-3.f * Tools.flo(t, 2) + 3.f * Tools.flo(t, 1) + 3.f * t + 1.f));
        if (s == 1) return ((1.f / 6.f) * Tools.flo(t, 2));
        return 0;
    }

    public float bspline(float[] p, int pi) {
        float bp = 0;
        for (int sj = -2; sj < 2; sj++) bp += b_knots(sj) * p[sj + pi];
        return bp;
    }

    public float catmullrom(float[] p, int i) {
        return 0.5f * ((2.f * p[i + 1]) + //0.5f
                (-p[i + 0] + p[i + 2]) * t +
                (2.f * p[i + 0] - 5.f * p[i + 1] + 4.f * p[i + 2] - p[i + 3]) * Tools.flo(t, 1) +
                (-p[i + 0] + 3.f * p[i + 1] - 3.f * p[i + 2] + p[i + 3]) * Tools.flo(t, 2));
    }

    public float lagrange(float xl, float[] lrx, float[] lry, int n) {
        float a, b, k = 0.f;
        for (int il = 0; il < n; il++) {
            a = 1.f;
            b = 1.f;
            for (int jl = 0; jl < n; jl++)
                if (jl != il && xl != lrx[jl]) {
                    a *= (xl - lrx[jl]);
                    b *= (lrx[il] - lrx[jl]);
                }
            k += (a / b) * lry[il];
        }
        return k;
    }
}
