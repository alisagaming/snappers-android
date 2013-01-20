package com.emerginggames.bestpuzzlegame.gdx.helper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.04.12
 * Time: 4:28
 */
public class QuadEasingAnim implements IAnimationFunction {

    @Override
    public float getMult(float t) {
        return t<0.5f ? t*t : -t * (t-1);
    }
}
