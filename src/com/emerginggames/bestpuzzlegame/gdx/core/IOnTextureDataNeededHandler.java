package com.emerginggames.bestpuzzlegame.gdx.core;

import android.graphics.Bitmap;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 7:43
 */
public interface IOnTextureDataNeededHandler {
    public Bitmap textureInfoNeeded(int width, int height);
    public boolean recycleBitmap();
}
