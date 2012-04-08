package ru.emerginggames.snappers.gdx.core;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 7:41
 */
public class BitmapManagedTextureData implements TextureData{
    Bitmap bitmap;
    Pixmap.Format format;
    boolean isPrepared;
    IOnTextureDataNeededHandler updateHandler;
    int width = 0;
    int height = 0;


    public BitmapManagedTextureData(IOnTextureDataNeededHandler updateHandler, Pixmap.Format format) {
        this.format = format;
        this.updateHandler = updateHandler;
    }

    @Override
    public void consumeCompressedData() {
        if (!isPrepared) throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");

        Gdx.gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        if (updateHandler.recycleBitmap())
            bitmap.recycle();
        isPrepared = false;
    }

    @Override
    public TextureDataType getType() {
        return TextureDataType.Compressed;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void prepare() {
        bitmap = updateHandler.textureInfoNeeded(width, height);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        isPrepared = true;
    }

    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    @Override
    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Pixmap.Format getFormat() {
        return format;
    }

    @Override
    public boolean useMipMaps() {
        return false;
    }

    @Override
    public boolean isManaged() {
        return true;
    }
}
