package ru.emerginggames.snappers.gdx.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 30.03.12
 * Time: 0:35
 */
public class ResizedFileTextureData implements TextureData{
    final FileHandle file;
    protected boolean isPrepared;
    Pixmap.Format format;
    Pixmap pixmap;
    int width = 0;
    int height = 0;
    int normWidth;
    int normHeight;
    
    public ResizedFileTextureData(FileHandle file, Pixmap.Format format) {
        this.file = file;
        this.format = format;
    }

    public ResizedFileTextureData(FileHandle file, Pixmap.Format format, int width, int height) {
        this.file = file;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    @Override
    public TextureDataType getType() {
        return TextureDataType.Pixmap;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void prepare() {
        if (isPrepared())
            return;
        //Texture.setEnforcePotImages(false);

        pixmap = new Pixmap(file);
        if (width == 0){
            width = pixmap.getWidth();
            height = pixmap.getHeight();
        }
        normWidth = nextPowerOfTwo(width);
        normHeight = nextPowerOfTwo(height);
        if (format == null)
            format = pixmap.getFormat();
        if (normHeight != height || normWidth != width
                || width != pixmap.getWidth() || height != pixmap.getHeight()
                || !pixmap.getFormat().equals(format)){
            Pixmap tmp = new Pixmap(normWidth, normHeight, format);
            Pixmap.Blending blend = Pixmap.getBlending();
            Pixmap.setBlending(Pixmap.Blending.None);
            tmp.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, width, height);
            pixmap.dispose();
            pixmap = tmp;
            Pixmap.setBlending(blend);
        }
        isPrepared = true;
    }

    @Override
    public Pixmap consumePixmap() {
        if (!isPrepared) throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
        isPrepared = false;
        Pixmap pixmap = this.pixmap;
        this.pixmap = null;
        return pixmap;
    }

    @Override
    public boolean disposePixmap() {
        return true;
    }

    @Override
    public void consumeCompressedData() {
        if (!isPrepared) throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");

        Gdx.gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
        Gdx.gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0,
                pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        pixmap.dispose();
        isPrepared = false;

    }

    @Override
    public int getWidth() {
        return normWidth;
    }

    @Override
    public int getHeight() {
        return normHeight;
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

    protected static int nextPowerOfTwo(int val){
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
        if (val<=2048)
            return 2048;
        return 4096;
    }
}
