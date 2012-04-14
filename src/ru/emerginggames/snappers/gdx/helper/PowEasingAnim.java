package ru.emerginggames.snappers.gdx.helper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.04.12
 * Time: 4:51
 */
public class PowEasingAnim implements IAnimationFunction {
    double rate;
    float k;

    public PowEasingAnim(float rate) {
        this.rate = rate;
        k = (float)Math.pow(0.5, 1-rate);
    }

    @Override
    public float getMult(float t) {
        //return (float)Math.pow(t, rate);
        return t <=0.5 ? k * (float)Math.pow(t, rate) :
                1 - k * (float)Math.pow(1-t, rate);
    }
}
