package ru.emerginggames.snappers.gdx.android;

import android.graphics.Bitmap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.04.12
 * Time: 18:19
 */
public class BitmapManagedTextureData implements TextureData{
    Bitmap bitmap;
    Pixmap pixmap;
    Pixmap.Format format;
    ByteArrayOutputStream outStream;
    boolean isPrepared;

    public BitmapManagedTextureData(Bitmap bitmap, Pixmap.Format format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public void consumeCompressedData() {
        if (!isPrepared) throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");

        Gdx.gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
        Gdx.gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0,
                pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        pixmap.dispose();
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
        int size = bitmap.getWidth() * bitmap.getHeight() * 3;
        if (outStream == null)
            outStream = new ByteArrayOutputStream(size);
        while (true){
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream))
                break;
            size = size * 3 / 2;
            outStream = new ByteArrayOutputStream(size);
        }
        byte[] img = outStream.toByteArray();

        pixmap = new Pixmap(img, 0, img.length);

        isPrepared = true;
    }

    @Override
    public Pixmap consumePixmap() {
        isPrepared = false;
        Pixmap px = pixmap;
        pixmap = null;
        return px;
    }

    @Override
    public boolean disposePixmap() {
        return true;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getWidth();
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
