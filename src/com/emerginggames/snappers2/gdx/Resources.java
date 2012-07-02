package com.emerginggames.snappers2.gdx;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.emerginggames.snappers2.Metrics;
import com.emerginggames.snappers2.Settings;
import com.emerginggames.snappers2.gdx.core.PrepareableTextureAtlas;
import com.emerginggames.snappers2.utils.WorkerThread;


/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:08
 */
public class Resources {
    protected static String dir;
    protected static final Object syncLock = new Object();
    public static Context context;

    protected static class Preload {
        public static TextureData bg;
        public static boolean isBgLoading;
        public static String bgName;
        public static PrepareableTextureAtlas.TextureAtlasData mainTextures;
    }

    public static TextureRegion bg;
    public static PrepareableTextureAtlas mainTextures;
    public static TextureRegion hintCircle;
    public static TextureRegion rays;

    public static PrepareableTextureAtlas.AtlasRegion[] snapperBack;
    public static PrepareableTextureAtlas.AtlasRegion[] eyeFrames;
    public static PrepareableTextureAtlas.AtlasRegion[] blastFrames;
    public static PrepareableTextureAtlas.AtlasRegion[] bangFrames;
    public static PrepareableTextureAtlas.AtlasRegion[] hintFrames;


    public static Sound[] popSounds;
    public static Sound winSound;
    public static Sound fanfareSound;
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
        fnt1 = new BitmapFont();
        fnt1.setScale(2);
    }

    private static void loadSnapperTextures(boolean isGold) {
        preload();

        mainTextures = new PrepareableTextureAtlas(Preload.mainTextures);

        eyeFrames = new PrepareableTextureAtlas.AtlasRegion[28];
        for (int i = 0; i < 14; i++)
            eyeFrames[i] = mainTextures.findRegion(String.format("e%d", i+1));
        for (int i=14; i<28; i++)
            eyeFrames[i] = eyeFrames[27-i];

        snapperBack = new PrepareableTextureAtlas.AtlasRegion[5];
        snapperBack[0] = mainTextures.findRegion(String.format("red"));
        snapperBack[1] = mainTextures.findRegion(String.format("red"));
        snapperBack[2] = mainTextures.findRegion(String.format("green"));
        snapperBack[3] = mainTextures.findRegion(String.format("yellow"));
        snapperBack[4] = mainTextures.findRegion(String.format("blue"));

        blastFrames = new PrepareableTextureAtlas.AtlasRegion[2];
        blastFrames[0] = mainTextures.findRegion(String.format("blast0"));
        blastFrames[1] = mainTextures.findRegion(String.format("blast"));

        bangFrames  = new PrepareableTextureAtlas.AtlasRegion[5];
        for (int i = 0; i < 5; i++)
            bangFrames[i] = mainTextures.findRegion(String.format("b%d", 5 + i));

        hintFrames  = new PrepareableTextureAtlas.AtlasRegion[18];
        for (int i = 0; i < 10; i++)
            hintFrames[i] = mainTextures.findRegion(String.format("h%d", i+1));
        for (int i=10; i<18; i++)
            hintFrames[i] = hintFrames[18-i];

        hintCircle = mainTextures.findRegion("round");
        rays = mainTextures.findRegion("rays");
    }

    public static boolean loadBg(String name) {
        preloadBg(name);
        if (Preload.bg == null)
            return false;
        float scale = (float)Metrics.bgSourceHeight / Metrics.screenHeight;
        int width = (int)(scale * Metrics.screenWidth);
        if (width > Metrics.bgSourceWidth)
            width = Metrics.bgSourceWidth;
        int bgStartX = (Metrics.bgSourceWidth - width)/2;

        bg = new TextureRegion(new Texture(Preload.bg), bgStartX, 0, width, Metrics.bgSourceHeight);
        return true;
    }

    public static void loadSounds() {
        popSounds = new Sound[5];
        popSounds[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/p1.mp3"));
        popSounds[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/p2.mp3"));
        popSounds[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/p3.mp3"));
        popSounds[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/p4.mp3"));
        popSounds[4] = Gdx.audio.newSound(Gdx.files.internal("sounds/p5.mp3"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("sounds/winsound.mp3"));
        fanfareSound = Gdx.audio.newSound(Gdx.files.internal("sounds/winsound.mp3"));
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
            if (Preload.mainTextures == null){
                FileHandle packFile = Gdx.files.internal(dir + "mainTexture.txt");
                Preload.mainTextures = new PrepareableTextureAtlas.TextureAtlasData(packFile, packFile.parent(), false);
            }
        }
    }

    public static void preparePreload() {
        synchronized (syncLock) {
            prepareData(Preload.mainTextures);
        }
    }

    protected static void prepareData(TextureData data) {
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
        Preload.bg = new FileTextureData(Gdx.files.internal("bg/" +dir + name), null,  Pixmap.Format.RGB888, false);
        Preload.bgName = name;
    }

    protected static void utilizeBg() {
        if (Preload.bg == null)
            return;
        if (Preload.bg.isPrepared())
            Preload.bg.consumePixmap().dispose();
        Preload.bg = null;
    }

    public static Typeface getFont(Context context) {
        if (font == null)
            font = Typeface.createFromAsset(context.getAssets(), Settings.FONT);
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
