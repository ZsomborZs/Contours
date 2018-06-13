package com.mobile.strigoy.myapplication.utility;

public class Tools {

    //Square
    public static float flo(float number, int power) {
        float sdf = number;
        if (power == -1) number = 1.f;
        else for (int is = 0; is < power; is++) {
            number *= sdf;
        }
        return number;
    }

    //Binominal
    public static float binom(int ib0, int ib1) {
        return fakto(ib0) / (fakto(ib1) * fakto(ib0 - ib1));
    }

    //Factorial
    public static float fakto(int number) {
        float szf0 = 1;
        for (int ifs = 1; ifs < number + 1; ifs++) szf0 *= ifs;
        return szf0;
    }
}
