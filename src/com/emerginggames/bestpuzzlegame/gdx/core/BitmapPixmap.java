package com.emerginggames.bestpuzzlegame.gdx.core;

import android.graphics.Bitmap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 18:31
 */
public class BitmapPixmap {

    public static Pixmap bitmapToPixmap(Bitmap bitmap){
        int size = bitmap.getWidth() * bitmap.getHeight() * 2;

        ByteArrayOutputStream outStream;
        while (true){
            outStream = new ByteArrayOutputStream(size);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream))
                break;
            size = size * 3 / 2;
        }
        byte[] img = outStream.toByteArray();

        return new Pixmap(img, 0, img.length);
    }
    
    public static TextureRegion bitmapToTexture(Bitmap bitmap, Pixmap.Format format){
        Pixmap pixmap = bitmapToPixmap(bitmap);
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        int normWidth = nextPowerOfTwo(width);
        int normHeight = nextPowerOfTwo(height);
        Texture texture;
        if (format == null)
            format = pixmap.getFormat();
        if (normHeight != height || normWidth != width || !pixmap.getFormat().equals(format)){
            Pixmap tmp = new Pixmap(normWidth, normHeight, format);
            Pixmap.Blending blend = Pixmap.getBlending();
            Pixmap.setBlending(Pixmap.Blending.None);
            tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
            Pixmap.setBlending(blend);
            texture =  new Texture(tmp, format, false);
            tmp.dispose();
        }
        else
            texture =  new Texture(pixmap, format, false);
        pixmap.dispose();
        return new TextureRegion(texture, 0, 0, width, height);
    }


    public static int nextPowerOfTwo(int val){
        if (val<=4)
            return 4;
        if (val<=8)
            return 8;
        if (val<=16)
            return 16;
        if (val<=32)
            return 32;
        if (val<=64)
            return 64;
        if (val<=128)
            return 128;
        if (val<=256)
            return 256;
        if (val<=512)
            return 512;
        if (val<=1024)
            return 1024;
        return 2048;
    }

}
