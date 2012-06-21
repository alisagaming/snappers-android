package com.emerginggames.snappers.gdx.Elements;

import com.emerginggames.snappers.gdx.helper.IPositionable;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 13.04.12
 * Time: 20:49
 */
public class PositionInfo {
    public IPositionable other;
    public float x;
    public float y;
    public IPositionable.Dir dir;
    public float margin;

    public void set(IPositionable other, IPositionable.Dir dir, float margin) {
        this.other = other;
        this.dir = dir;
        this.margin = margin;
    }

    public void set(float x, float y, IPositionable.Dir dir, float margin) {
        this.other = null;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.margin = margin;
    }
}
