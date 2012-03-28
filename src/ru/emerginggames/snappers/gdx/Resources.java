package ru.emerginggames.snappers.gdx;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.gdx.android.BitmapPixmap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.io.InputStream;


/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:08
 */
public class Resources {
    protected static String dir;
    
    public static Context context;
    
    public static Texture eyesTexture;
    public static Texture eyeShadowTexture;
    public static Texture snapperTexture;

    public static Texture bangTexture;
    public static Texture blastTexture;
    public static Texture squareButtons;
    public static Texture menuButtons;
    public static Texture dialog;
    public static Texture longDialog;


    public static TextureRegion[] snapperBack;
    public static TextureRegion shadowSnapper;
    public static TextureRegion[] eyeFrames;
    public static TextureRegion[] eyeShadowFrames;
    public static TextureRegion[] blastFrames;
    public static TextureRegion[] bangFrames;
    public static TextureRegion[] squareButtonFrames;
    public static TextureRegion[] menuButtonFrames;

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
    }

    private static void loadSquareButtonTextures(){
        squareButtons = new Texture(Gdx.files.internal(dir + "btn-sq.png"));
        squareButtonFrames = makeAnimationFrames(squareButtons, Metrics.squareButtonSize, Metrics.squareButtonSize, false, 7);

    }

    public static void loadPausedMenuTextures(){
        longDialog = new Texture(Gdx.files.internal(dir + "dialoglong.png"), Pixmap.Format.RGBA8888, false);
        
        menuButtons= new Texture(Gdx.files.internal(dir + "btn-long.png"));
        menuButtonFrames = makeAnimationFrames(menuButtons, Metrics.menuButtonWidth, Metrics.menuButtonHeight, false, 5);

    }

    private static void loadSnapperTextures(boolean isGold){
        int snapperSize = Metrics.snapperSize;

        snapperTexture = new Texture(Gdx.files.internal(dir + "back.png"), Pixmap.Format.RGBA8888, false);
        snapperTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        snapperTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        int isGoldN = isGold? 1:0;
        snapperBack = new TextureRegion[5];
        snapperBack[1] = getTextureRegion(snapperTexture, snapperSize, snapperSize, isGoldN, 3);
        snapperBack[2] = getTextureRegion(snapperTexture, snapperSize, snapperSize, isGoldN, 1);
        snapperBack[3] = getTextureRegion(snapperTexture, snapperSize, snapperSize, isGoldN, 2);
        snapperBack[4] = getTextureRegion(snapperTexture, snapperSize, snapperSize, isGoldN, 0);
        shadowSnapper = getTextureRegion(snapperTexture, snapperSize, snapperSize, 0, 4);

        eyesTexture = new Texture(Gdx.files.internal(dir + "eyes.png"), Pixmap.Format.RGBA4444, false);
        eyesTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        eyesTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        eyeFrames = makeAnimationFrames(eyesTexture, snapperSize, snapperSize, true);

        eyeShadowTexture = new Texture(Gdx.files.internal(dir + "eyeShadow.png"));
        eyeShadowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        eyeShadowTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        eyeShadowFrames = makeAnimationFrames(eyeShadowTexture, snapperSize, snapperSize, true);

        blastTexture = new Texture(Gdx.files.internal(dir + "blast.png"));
        blastFrames = makeAnimationFrames(blastTexture, Metrics.blastSize, Metrics.blastSize, false);

        bangTexture = new Texture(Gdx.files.internal(dir + "bang.png"));
        bangFrames = makeAnimationFrames(bangTexture, snapperSize, snapperSize, false);
    }

    public static void disposeTextures(){
        eyesTexture.dispose();
        eyeShadowTexture.dispose();
        snapperTexture.dispose();
        bangTexture.dispose();
        blastTexture.dispose();
        squareButtons.dispose();
        if (longDialog != null)
            longDialog.dispose();
        if (menuButtons != null)
            menuButtons.dispose();
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

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, boolean goBack, int max){
        int cols = texture.getWidth() / tileWidth;
        int rows = texture.getHeight() / tileHeight;

        int size = cols * rows;
        if (max != 0 && size > max)
            size = max;
        TextureRegion[] frames = new TextureRegion[goBack? size * 2 - 2 : size];

        for (int row = 0, y=0, pos=0; row < rows & pos <size; row++, y += tileHeight)
            for (int col = 0, x=0; col < cols & pos < size; col++, x += tileWidth, pos++)
                if (max == 0 || pos < max)
                    frames[pos] = new TextureRegion(texture, x, y, tileWidth, tileHeight);
                else
                    break;

        if (goBack)
            for (int i = 0; i< size-2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;

    }
}
