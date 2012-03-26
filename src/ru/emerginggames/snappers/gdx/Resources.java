package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.emerginggames.snappers.Metrics;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:08
 */
public class Resources {
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

    public static void loadTextures(boolean isGold){
        String dir = "";
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
        //eyesTexture.bind();
        //Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL11.GL_COMBINE);

        eyeFrames = makeAnimationFrames(eyesTexture, snapperSize, snapperSize, 0, true);

        eyeShadowTexture = new Texture(Gdx.files.internal(dir + "eyeShadow.png"));
        eyeShadowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        eyeShadowTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        //eyeShadowTexture.bind();
        //Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);


        eyeShadowFrames = makeAnimationFrames(eyeShadowTexture, snapperSize, snapperSize, 0, true);

        blastTexture = new Texture(Gdx.files.internal(dir + "blast.png"));
        blastFrames = makeAnimationFrames(blastTexture, Metrics.blastSize, Metrics.blastSize, 0, false);
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

    private static TextureRegion[] makeAnimationFrames(Texture texture, int tileWidth, int tileHeight, int border, boolean goBack){
        int cols = texture.getWidth() / (tileWidth + border);
        int rows = texture.getHeight() / (tileHeight + border);

        int size = cols * rows;
        TextureRegion[] frames = new TextureRegion[goBack? size * 2 - 2 : size];

        for (int row = 0, y=0; row < rows; row++, y += (tileHeight + border))
            for (int col = 0, x=0; col < cols; col++, x += (tileWidth + border))
                frames[row * cols + col] = new TextureRegion(texture, x, y, tileWidth, tileHeight);

        if (goBack)
            for (int i = 0; i< size-2; i++)
                frames[size + i] = frames[size - i - 2];

        return frames;

    }
}
