package com.emerginggames.bestpuzzlegame.gdx.helper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.04.12
 * Time: 0:19
 */
public class LinearAnimation implements IAnimationFunction{

    @Override
    public float getMult(float normTime) {
        return normTime;
    }
}
