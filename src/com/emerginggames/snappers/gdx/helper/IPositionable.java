package com.emerginggames.snappers.gdx.helper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 0:30
 */
public interface IPositionable {
    public enum Dir { UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT, CENTER};

    public void positionRelative(IPositionable other, Dir dir, float margin);
    public void positionRelative(float x, float y, Dir dir, float margin);
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public void setPosition(float x, float y);
    public float getTop();
    public float getRight();
}
