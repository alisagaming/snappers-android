package com.emerginggames.snappers.gdx.helper;

import android.util.FloatMath;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.04.12
 * Time: 3:49
 */
public class SinAnimation implements IAnimationFunction {

    @Override
    public float getMult(float normTime) {
        return -0.5f * (FloatMath.cos(3.14f*normTime) - 1);
    }
}
