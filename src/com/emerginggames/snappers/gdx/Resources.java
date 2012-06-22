package com.emerginggames.snappers.gdx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.gdx.core.BitmapPixmap;
import com.emerginggames.snappers.gdx.core.ResizedFileTextureData;
import com.emerginggames.snappers.utils.WorkerThread;
import com.emerginggames.snappers.gdx.core.PrepareableTextureAtlas;


/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:08
 */
public class Resources {
    protected static String dir;
    protected static final Object syncLock = new Object();

    protected static class Preload {
        public static TextureData snappers;
        public static TextureData bang;
        public static TextureData blast;
        public static TextureData bg;
        public static TextureData hint;
        public static boolean isBgLoading;
        public static String bgName;
    }

    public static Context context;

    public static Texture snapperTexture;

    public static Texture bangTexture;
    public static Texture blastTexture;
    public static Texture hintTexture;
    public static TextureRegion bg;

    public static TextureRegion[] snapperBack;
    public static TextureRegion[] eyeFrames;
    public static TextureRegion[] blastFrames;
    public static TextureRegion[] bangFrames;
    public static TextureRegion[] hintFrames;


    public static Sound[] popSounds;
    public static Sound winSound;
    public static Sound buttonSound;

    public static BitmapFont fnt1;
    public static Typeface font;

    public static void init() {
        switch (Metrics.sizeMode) {
            case modeS:
                dir = "lo/";
                break;
            case modeM:
                dir = "med/";
                break;
            case modeL:
                dir = "hi/";
                break;
        }
    }

    public static void loadTextures(boolean isGold) {
        loadSnapperTextures(isGold);
        loadBmpFonts();
    }

    public static void loadBmpFonts() {
        fnt1 = new BitmapFont(Gdx.files.internal("FontShag.fnt"), Gdx.files.internal("FontShag.png"), false);
    }

    private static void loadSnapperTextures(boolean isGold) {
        preload();
        int snapperSize = Metrics.snapperSize;

        int snappersStart = isGold ? 25 + 4 : 25;
        snapperTexture = new Texture(Preload.snappers);
        snapperTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        snapperTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        eyeFrames = makeAnimationFrames(snapperTexture, snapperSize, snapperSize, true, 0, 25);
        snapperBack = makeAnimationFrames(snapperTexture, snapperSize, snapperSize, false, snappersStart, 4);

        blastTexture = new Texture(Preload.blast);
        blastFrames = makeAnimationFrames(blastTexture, Metrics.blastSize, Metrics.blastSize, false, 0, 3);

        if (Metrics.sizeMode == Metrics.SizeMode.modeS) {
            bangFrames = makeAnimationFrames(snapperTexture, Metrics.bangSize, Metrics.bangSize, false, 33, 5);
        } else {
            bangTexture = new Texture(Preload.bang);
            bangFrames = makeAnimationFrames(bangTexture, Metrics.bangSize, Metrics.bangSize, false);
        }

        hintTexture = new Texture(Preload.hint);
        hintFrames = makeAnimationFrames(hintTexture, Metrics.hintSize, Metrics.hintSize, false, 0, 11);
    }

    private static TextureRegion getTextureRegion(Texture texture, int tileWidth, int tileHeight, int i, int j) {
        return new TextureRegion(texture, i * tileWidth, j * tileHeight, tileWidth, tileHeight);
    }

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack) {
        TextureRegion[][] frames2 = TextureRegion.split(texture, tileWidth, tileHeight);
        if (frames2.length == 1)
            return null;

        int height = frames2.length;
        int width = frames2[1].length;

        int size = width * height;
        TextureRegion[] frames = new TextureRegion[goBack ? size * 2 - 2 : size];

        int i;
        int j;
        for (i = 0; i < height; i++)
            for (j = 0; j < width; j++)
                frames[i * width + j] = frames2[i][j];

        if (goBack)
            for (i = 0; i < size - 2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;
    }

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack, int start, int size) {
        int cols = texture.getWidth() / tileWidth;

        TextureRegion[] frames = new TextureRegion[goBack ? size * 2 - 2 : size];

        for (int i = 0; i < size; i++)
            frames[i] = new TextureRegion(texture, tileWidth * ((i + start) % cols), tileHeight * ((i + start) / cols), tileWidth, tileHeight);


        if (goBack)
            for (int i = 0; i < size - 2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;
    }

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack, int startX, int startY, int max) {
        int width = texture.getWidth();

        int size = max;
        TextureRegion[] frames = new TextureRegion[goBack ? size * 2 - 2 : size];
        int posx = startX;
        int posy = startY;
        if (posx > width) {
            posy += (tileHeight * (posx / width));
            posx = posx % width;
        }

        for (int i = 0; i < max; i++) {
            frames[i] = new TextureRegion(texture, posx, posy, tileWidth, tileHeight);
            posx += tileWidth;
            if (posx + tileWidth > width) {
                posx = 0;
                posy += tileHeight;
            }
        }

        if (goBack)
            for (int i = 0; i < size - 2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;
    }

    public static boolean loadBg(String name) {
        preloadBg(name);
        if (Preload.bg == null)
            return false;

        bg = new TextureRegion(new Texture(Preload.bg), 0, 0, Metrics.screenWidth, Metrics.screenHeight);
        return true;
    }

    public static TextureRegion normLoadTexture(String name, Pixmap.Format format) {
        try {
            Bitmap bgSource = BitmapFactory.decodeStream(context.getAssets().open(name));
            TextureRegion reg = BitmapPixmap.bitmapToTexture(bgSource, format);
            bgSource.recycle();
            return reg;
        } catch (Exception e) {
            return null;
        }
    }

    public static void loadSounds() {
        popSounds = new Sound[5];
        popSounds[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop1.mp3"));
        popSounds[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop2.mp3"));
        popSounds[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop3.mp3"));
        popSounds[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop4.mp3"));
        popSounds[4] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop5.mp3"));
        //popSounds[5] = Gdx.audio.newSound(Gdx.files.internal("sounds/pop6.mp3"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("sounds/win1.mp3"));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/button2g.mp3"));
    }

    public static void preload() {
        init();
        createPreload();
        preparePreload();
    }

    protected static void createPreload() {
        if (Gdx.files == null)
            return;
        synchronized (syncLock) {
            if (Preload.bang == null && Metrics.sizeMode != Metrics.SizeMode.modeS)
                Preload.bang = new ResizedFileTextureData(Gdx.files.internal(dir + "bang.png"), Pixmap.Format.RGBA4444);
            if (Preload.blast == null)
                Preload.blast = new ResizedFileTextureData(Gdx.files.internal(dir + "blast.png"), Pixmap.Format.RGBA4444);
            if (Preload.snappers == null)
                Preload.snappers = new FileTextureData(Gdx.files.internal(dir + "snappers.png"), null, Pixmap.Format.RGBA4444, false);
            if (Preload.hint == null)
                Preload.hint = new FileTextureData(Gdx.files.internal(dir + "hint_circle.png"), null, Pixmap.Format.RGBA4444, false);
        }
    }

    public static void preparePreload() {
        synchronized (syncLock) {
            prepareData(Preload.bang);
            prepareData(Preload.blast);
            prepareData(Preload.snappers);
            prepareData(Preload.hint);
        }
    }

    protected static void prepareData(TextureData data) {
        //Log.v(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        if (data != null && !data.isPrepared())
            data.prepare();
    }

    protected static void prepareData(PrepareableTextureAtlas.TextureAtlasData data) {
        if (data != null && !data.isPrepared())
            data.prepare();
    }

    public static void preloadBg(String name) {
        createBgPreload(name);
        prepareBgData();
    }

    public static void prepareBgData() {
        if (Preload.isBgLoading)
            return;

        try {
            Preload.isBgLoading = true;
            prepareData(Preload.bg);
        } catch (Exception e) {
            utilizeBg();
        } finally {
            Preload.isBgLoading = false;
        }
    }

    protected static void createBgPreload(String name) {
        if (name.equals(Preload.bgName) && Preload.bg != null)
            return;
        utilizeBg();
        if (Gdx.files == null)
            return;
        if (Metrics.sizeMode == Metrics.SizeMode.modeS)
            Preload.bg = new ResizedFileTextureData(Gdx.files.internal("bg-lo/" + name), Pixmap.Format.RGB565, Metrics.screenWidth, Metrics.screenHeight);
        else
            Preload.bg = new ResizedFileTextureData(Gdx.files.internal("bg/" + name), Pixmap.Format.RGB565, Metrics.screenWidth, Metrics.screenHeight);
        Preload.bgName = name;
    }

    protected static void utilizeBg() {
        //Log.v(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        if (Preload.bg == null)
            return;
        if (Preload.bg.isPrepared())
            Preload.bg.consumePixmap().dispose();
        Preload.bg = null;
    }

    public static Typeface getFont(Context context) {
        if (font == null)
            font = Typeface.createFromAsset(context.getAssets(), "shag_lounge.otf");
        return font;
    }

    public static void preloadResourcesInWorker(String backName){
        if (backName != null)
            preloadRunnable.bgName = backName;
        WorkerThread.getInstance().post(preloadRunnable);
    }

    private static class PreloadRunnable implements Runnable{
        public String bgName;
        @Override
        public void run() {
            createPreload();
            preparePreload();
            if (bgName != null)
                preloadBg(bgName);
            Log.v("Snappers - PRELOAD", "Preload done");
        }
    }
    private static PreloadRunnable preloadRunnable = new PreloadRunnable();



}