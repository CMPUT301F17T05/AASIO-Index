package com.cmput301.t05.habilect;

/**
 * Created by Ian on 2017-11-08.
 */

public final class MathUtility {
    public static float EasingOut(double time, double duration, int power) {
        return (float) Math.min(1 - Math.pow(1 - (time / duration), power), 1);
    }
}
