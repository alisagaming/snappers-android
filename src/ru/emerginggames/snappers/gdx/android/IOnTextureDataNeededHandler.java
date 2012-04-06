package ru.emerginggames.snappers.gdx.android;

import android.graphics.Bitmap;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 7:43
 */
public interface IOnTextureDataNeededHandler {
    public Bitmap textureInfoNeeded();
    public boolean recycleBitmap();
}
