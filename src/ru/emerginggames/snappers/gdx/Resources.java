package ru.emerginggames.snappers.gdx;

import android.content.Context;
import android.graphics.*;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.gdx.android.BitmapPixmap;
import ru.emerginggames.snappers.gdx.android.ResizedFileTextureData;

import java.lang.reflect.Array;


/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:08
 */
public class Resources {
    protected static String dir;
    protected static final Integer syncLock =  0;

    protected static class Preload {
        public static TextureData eyes;
        public static TextureData eyeShadow;
        public static TextureData snappers;
        public static TextureData snapperBig;
        public static TextureData bang;
        public static TextureData blast;
        public static TextureData squareButtons;
        public static TextureData bg;
        public static boolean isBgLoading;
        public static String bgName;
    }

    public static Context context;

    public static Texture eyesTexture;
    public static Texture eyeShadowTexture;
    public static Texture snapperTexture;

    public static Texture bangTexture;
    public static Texture blastTexture;
    public static Texture squareButtons;
    public static Texture menuButtons;
    public static Texture dialog;
    public static TextureRegion longDialog;
    public static TextureRegion bg;


    public static TextureRegion[] snapperBack;
    public static TextureRegion shadowSnapper;
    public static TextureRegion[] eyeFrames;
    public static TextureRegion[] eyeShadowFrames;
    public static TextureRegion[] blastFrames;
    public static TextureRegion[] bangFrames;
    public static TextureRegion[] squareButtonFrames;
    public static TextureRegion[] menuButtonFrames;

    public static Sound[] popSounds;
    public static Sound  winSound;
    public static Sound  buttonSound;

    public static BitmapFont fnt1;

    public static Typeface font;

    public static boolean texturesLoaded = false;



    public static void init(){
        switch (Metrics.sizeMode){
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

    public static void loadTextures(boolean isGold){
        loadSnapperTextures(isGold);
        loadSquareButtonTextures();
        loadBmpFonts();
    }

    public static void loadBmpFonts(){
        fnt1 = new BitmapFont(Gdx.files.internal("FontShag.fnt"), Gdx.files.internal("FontShag.png"), false);
    }

    private static void loadSquareButtonTextures(){
        squareButtons = new Texture(Preload.squareButtons);
        squareButtonFrames = makeAnimationFrames(squareButtons, Metrics.squareButtonSize, Metrics.squareButtonSize, false, 0, 7);

    }

    public static void loadPausedMenuTextures(){
        Texture temp = new Texture(Gdx.files.internal(dir + "dialoglong.png"), Pixmap.Format.RGBA8888, false);
        longDialog = new TextureRegion(temp, 0, 0, Metrics.menuWidth, Metrics.menuHeight);

        menuButtons= new Texture(Gdx.files.internal(dir + "btn-long.png"), Pixmap.Format.RGBA8888, false);
        menuButtonFrames = makeAnimationFrames(menuButtons, Metrics.menuButtonWidth, Metrics.menuButtonHeight, false, 0, 5);

    }

    private static void loadSnapperTextures(boolean isGold){
        preload();
        int snapperSize = Metrics.snapperSize;

        int snappersStart = isGold ? 25+4: 25;
        snapperTexture = new Texture(Preload.snapperBig);
        snapperTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        snapperTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        eyeFrames = makeAnimationFrames(snapperTexture, snapperSize, snapperSize, true, 0, 25);
        snapperBack = makeAnimationFrames(snapperTexture, snapperSize, snapperSize, false, snappersStart, snappersStart + 4);

        blastTexture = new Texture(Preload.blast);
        blastFrames = makeAnimationFrames(blastTexture, Metrics.blastSize, Metrics.blastSize, false);

        bangTexture = new Texture(Preload.bang);
        bangFrames = makeAnimationFrames(bangTexture, Metrics.bangSize, Metrics.bangSize, false);
    }

    public static void disposeTextures(){
        snapperTexture.dispose();
        bangTexture.dispose();
        blastTexture.dispose();
        squareButtons.dispose();
        if (longDialog != null)
            longDialog.getTexture().dispose();
        if (menuButtons != null)
            menuButtons.dispose();
        if (bg!=null)
            bg.getTexture().dispose();
    }


    private static TextureRegion getTextureRegion(Texture texture, int tileWidth, int tileHeight, int i, int j){
        return new TextureRegion(texture, i * tileWidth, j * tileHeight, tileWidth, tileHeight);
    }

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack){
        TextureRegion[][] frames2 = TextureRegion.split(texture, tileWidth, tileHeight);
        if (frames2.length == 1)
            return null;

        int height = frames2.length;
        int width = frames2[1].length;

        int size = width * height;
        TextureRegion[] frames = new TextureRegion[goBack? size * 2 - 2 : size];

        int i; int j;
        for (i=0; i<height; i++)
            for (j=0; j<width; j++)
                frames[i * width + j] = frames2[i][j];

        if (goBack)
            for (i = 0; i< size-2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;
    }

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack, int start,  int max){
        int cols = texture.getWidth() / tileWidth;
        int rows = texture.getHeight() / tileHeight;

        int size = max - start;
        TextureRegion[] frames = new TextureRegion[goBack? size * 2 - 2 : size];

        for (int i=start; i<max; i++)
            frames[i - start] = new TextureRegion(texture, tileWidth * (i%cols), tileHeight * (i/cols), tileWidth, tileHeight);


        if (goBack)
            for (int i = 0; i< size-2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;
    }

    public static boolean loadBg(String name){
        preloadBg(name);
        if (Preload.bg == null)
            return false;
        
        bg = new TextureRegion(new Texture(Preload.bg),0 , 0, Metrics.screenWidth, Metrics.screenHeight);
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

    public static void loadSounds(){
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

    public static void preload(){
        init();
        createPreload();
        preparePreload();
    }

    protected static void createPreload(){
        if (Preload.bang == null)
            Preload.bang = new ResizedFileTextureData(Gdx.files.internal(dir + "bang.png"), Pixmap.Format.RGBA4444);
        if (Preload.blast == null)
            Preload.blast = new ResizedFileTextureData(Gdx.files.internal(dir + "blast.png"), Pixmap.Format.RGBA4444);
        if (Preload.squareButtons == null)
            Preload.squareButtons = new FileTextureData(Gdx.files.internal(dir + "btn-sq.png"), null, Pixmap.Format.RGBA8888, false);
        if (Preload.snapperBig == null)
            Preload.snapperBig = new FileTextureData(Gdx.files.internal(dir + "snappers.png"), null, Pixmap.Format.RGBA4444, false);
    }

    public static void preparePreload(){
        synchronized (syncLock){
            prepareData(Preload.bang);
            prepareData(Preload.blast);
            prepareData(Preload.squareButtons);
            prepareData(Preload.snapperBig);
        }
    }

    protected static void prepareData(TextureData data){
        if (data != null &&!data.isPrepared())
            data.prepare();
    }
    
    public static void preloadBg(String name){
        createBgPreload(name);
        prepareBgData();
    }

    public static void prepareBgData(){
        if (Preload.isBgLoading)
            return;

        try{
            Preload.isBgLoading = true;
            prepareData(Preload.bg);
        }
        catch (Exception e){
            utilizeBg();
        }
        finally {
            Preload.isBgLoading = false;
        }

    }
    
    protected static void createBgPreload(String name){
        if (name.equals(Preload.bgName))
            return;
        utilizeBg();
        Preload.bg = new ResizedFileTextureData(Gdx.files.internal("bg/" + name),
                Pixmap.Format.RGB565, Metrics.screenWidth, Metrics.screenHeight);
        Preload.bgName = name;
    }

    protected static void utilizeBg(){
        if (Preload.bg == null)
            return;
        if (Preload.bg.isPrepared())
            Preload.bg.consumePixmap().dispose();
        Preload.bg = null;
    }

    public static Typeface getFont(Context context){
        if (font == null)
            font = Typeface.createFromAsset(context.getAssets(), "shag_lounge.otf");
        return font;
    }
}
