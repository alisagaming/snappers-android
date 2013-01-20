package ru.emerginggames.snappers.gdx.core;

import android.graphics.Bitmap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.emerginggames.bestpuzzlegame.gdx.core.BitmapManagedTextureData;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.04.12
 * Time: 18:46
 */
public class BitmapManagedTexture extends Texture{
    protected TextureData textureData;

    public BitmapManagedTexture(Bitmap bitmap) {
        super(prepareTextureData(bitmap));
        textureData = getTextureData();
    }

    public void updateTexture(){
        textureData.prepare();
        bind();
        textureData.consumeCompressedData();
        setFilter(getMinFilter(), getMagFilter());
        setWrap(getUWrap(), getVWrap());
    }

    protected static TextureData prepareTextureData(Bitmap bitmap){
        return new BitmapManagedTextureData(bitmap, Pixmap.Format.RGBA4444);
    }
}
