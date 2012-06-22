package com.emerginggames.snappers.model;

import android.graphics.Bitmap;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 02.04.12
 * Time: 8:14
 */
public class ImageDrawInfo {
    public int id;
    public boolean isTransparent;
    public boolean isHighQuality;

    public ImageDrawInfo(int id, boolean transp, boolean hq) {
        this.id = id;
        isTransparent = transp;
        isHighQuality = hq;
    }

    public Bitmap.Config getConfig(){
        if (isHighQuality)
            return Bitmap.Config.ARGB_8888;
        else if (isTransparent)
            return Bitmap.Config.ARGB_4444;
        else
            return Bitmap.Config.RGB_565;
    }
}
