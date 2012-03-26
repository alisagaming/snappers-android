package ru.emerginggames.snappers;

import android.content.Context;
import android.graphics.Typeface;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;

import java.util.ArrayList;

public class Resources {
    public static Typeface font;
    public static TiledTexture eyesTexture;
    public static TiledTexture eyeShadowTexture;
    public static TiledTexture snapperTexture;
    public static AssetTexture shadowSnapper;
    public static TiledTexture bangTexture;
    public static TiledTexture blastTexture;
    public static TiledTexture squareButtons;
    public static TiledTexture menuButtons;
    public static AssetTexture dialog;
    public static AssetTexture longDialog;
    public static ArrayList<AnimatedSprite.Frame> eyeFrames;
    public static ArrayList<AnimatedSprite.Frame> bangFrames;
    public static ArrayList<AnimatedSprite.Frame> blastFrames;
    public static ArrayList<AnimatedSprite.Frame> snapper1Frames;
    public static ArrayList<AnimatedSprite.Frame> snapper2Frames;
    public static ArrayList<AnimatedSprite.Frame> snapper3Frames;
    public static ArrayList<AnimatedSprite.Frame> snapper4Frames;
    public static ArrayList<AnimatedSprite.Frame> buttonDim;
    public static ArrayList<AnimatedSprite.Frame> squareButtonHint;
    public static ArrayList<AnimatedSprite.Frame> squareButtonPause;
    public static ArrayList<AnimatedSprite.Frame> squareButtonForward;
    public static ArrayList<AnimatedSprite.Frame> squareButtonRestart;
    public static ArrayList<AnimatedSprite.Frame> squareButtonShop;
    public static ArrayList<AnimatedSprite.Frame> squareButtonMenu;
    public static ArrayList<AnimatedSprite.Frame> menuButtonResume;
    public static ArrayList<AnimatedSprite.Frame> menuButtonRestart;
    public static ArrayList<AnimatedSprite.Frame> menuButtonMenu;
    public static ArrayList<AnimatedSprite.Frame> menuButtonStore;
    private static String dir;

    private static ArrayList<AnimatedSprite.Frame> makeForeBackFrames(int width, int height){
        ArrayList<AnimatedSprite.Frame> frames = new ArrayList<AnimatedSprite.Frame>(width * height * 2 -1);

        int i; int j;
        for (i=0;i<height;i++)
            for (j=0;j<width;j++)
                frames.add(new AnimatedSprite.Frame(j, i));

        for (j=width-2;j>=0;j--)
            frames.add(new AnimatedSprite.Frame(j, height-1));

        for (i=width-2;i>=0;i--)
            for (j=width-1;j>=0;j--)
                frames.add(new AnimatedSprite.Frame(j, i));

        return frames;
    }

    private static ArrayList<AnimatedSprite.Frame> makeForeFrames(int width, int height){
        ArrayList<AnimatedSprite.Frame> frames = new ArrayList<AnimatedSprite.Frame>(width * height);

        int i; int j;
        for (i=0;i<height;i++)
            for (j=0;j<width;j++)
                frames.add(new AnimatedSprite.Frame(j, i));

        return frames;
    }

    public static void createFrames(){
        eyeFrames = makeForeBackFrames(5, 5);
        bangFrames = makeForeFrames(1, 4);
        blastFrames = makeForeFrames(1, 3);
        snapper1Frames = new ArrayList<AnimatedSprite.Frame>(1);
        snapper1Frames.add(new AnimatedSprite.Frame(0, 3));
        snapper2Frames = new ArrayList<AnimatedSprite.Frame>(1);
        snapper2Frames.add(new AnimatedSprite.Frame(0, 1));
        snapper3Frames = new ArrayList<AnimatedSprite.Frame>(1);
        snapper3Frames.add(new AnimatedSprite.Frame(0, 2));
        snapper4Frames = new ArrayList<AnimatedSprite.Frame>(1);
        snapper4Frames.add(new AnimatedSprite.Frame(0, 0));

        buttonDim = new ArrayList<AnimatedSprite.Frame>(1);
        buttonDim.add(new AnimatedSprite.Frame(0, 0));
        squareButtonHint = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonHint.add(new AnimatedSprite.Frame(1, 0));
        squareButtonPause = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonPause.add(new AnimatedSprite.Frame(2, 0));
        squareButtonForward = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonForward.add(new AnimatedSprite.Frame(3, 0));
        squareButtonRestart = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonRestart.add(new AnimatedSprite.Frame(0, 1));
        squareButtonShop = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonShop.add(new AnimatedSprite.Frame(1, 1));
        squareButtonMenu = new ArrayList<AnimatedSprite.Frame>(1);
        squareButtonMenu.add(new AnimatedSprite.Frame(2, 1));

        menuButtonResume = new ArrayList<AnimatedSprite.Frame>(1);
        menuButtonResume.add(new AnimatedSprite.Frame(0, 1));
        menuButtonRestart = new ArrayList<AnimatedSprite.Frame>(1);
        menuButtonRestart.add(new AnimatedSprite.Frame(0, 2));
        menuButtonMenu = new ArrayList<AnimatedSprite.Frame>(1);
        menuButtonMenu.add(new AnimatedSprite.Frame(0, 3));
        menuButtonStore = new ArrayList<AnimatedSprite.Frame>(1);
        menuButtonStore.add(new AnimatedSprite.Frame(0, 4));
    }

    static void loadResources(Context context){
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

       font = Typeface.createFromAsset(context.getAssets(), "shag_lounge.otf");

       eyesTexture = new TiledTexture(dir + "eyes.png", Metrics.snapperSize, Metrics.snapperSize, 0, 0, 1, 0, context, Texture.Option.BILINEAR);
       eyeShadowTexture = new TiledTexture(dir + "eyeShadow.png", Metrics.snapperSize, Metrics.snapperSize, 0, 0, 0, 0, context, Texture.Option.BILINEAR);
       bangTexture = new TiledTexture(dir + "bang.png", Metrics.snapperSize, Metrics.snapperSize, 0,0,0,0, context);
       blastTexture = new TiledTexture(dir + "blast.png", Metrics.blastSize, Metrics.blastSize, 0,0,0,0, context);
       snapperTexture = new TiledTexture(dir + "back.png", Metrics.snapperSize, Metrics.snapperSize, 0,0,0,0, context, Texture.Option.BILINEAR);
       squareButtons = new TiledTexture(dir + "btn-sq.png", Metrics.squareButtonSize, Metrics.squareButtonSize, 0,0,0,0, context);
       menuButtons = new TiledTexture(dir + "btn-long.png", Metrics.menuButtonWidth, Metrics.menuButtonHeight, 0,0,0,0, context);


       shadowSnapper = new AssetTexture(dir + "shadow.png", context, Texture.Option.BILINEAR);
       dialog = new AssetTexture(dir + "dialog.png", context);
       longDialog = new AssetTexture(dir + "dialoglong.png", context);


        eyesTexture.setReusable(true);
        eyeShadowTexture.setReusable(true);
        shadowSnapper.setReusable(true);
        bangTexture.setReusable(true);
        blastTexture.setReusable(true);
        snapperTexture.setReusable(true);
    }
}