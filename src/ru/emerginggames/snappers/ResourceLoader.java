package ru.emerginggames.snappers;

import android.content.Context;
import android.graphics.Typeface;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;
import ru.emerginggames.snappers.GameActivity.Resources;
import ru.emerginggames.snappers.GameActivity.Metrics;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.03.12
 * Time: 10:32
 */
public class ResourceLoader {
    private static String dir;



    public static void onLoadResources(Context context, int width) {
        setScreenMode(width);
        setMetrics();
        loadResources(context);
        setSnapperMult();
    }

    private static void setScreenMode(int width){
        if (width <320)
            GameActivity.sizeMode = GameActivity.SizeMode.modeS;
        else if (width < 600)
            GameActivity.sizeMode = GameActivity.SizeMode.modeM;
        else
            GameActivity.sizeMode = GameActivity.SizeMode.modeL;
    }

    private static void setMetrics(){
        switch (GameActivity.sizeMode){
            case modeS:
                Metrics.snapperSize = 32;
                Metrics.blastSize = 14;
                Metrics.squareButtonSize = 48;
                Metrics.infoFontSize = 22;
                break;
            case modeM:
                Metrics.snapperSize = 48;
                Metrics.blastSize = 18;
                Metrics.squareButtonSize = 64;
                Metrics.infoFontSize = 30;
                break;
            case modeL:
                Metrics.snapperSize = 96;
                Metrics.blastSize = 36;
                Metrics.squareButtonSize = 128;
                Metrics.infoFontSize = 60;
                break;
        }
        Metrics.screenMargin = Metrics.squareButtonSize/12;
    }

    private static void loadResources(Context context){
        switch (GameActivity.sizeMode){
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

        Resources.font = Typeface.createFromAsset(context.getAssets(), "shag_lounge.otf");

        Resources.eyesTexture = new TiledTexture(dir + "eyes.png", Metrics.snapperSize, Metrics.snapperSize, 0, 0, 1, 0, context, Texture.Option.BILINEAR);
        Resources.eyeShadowTexture= new TiledTexture(dir + "eyeShadow.png", Metrics.snapperSize, Metrics.snapperSize, 0, 0, 0, 0, context, Texture.Option.BILINEAR);
        Resources.shadowSnapper = new AssetTexture(dir + "shadow.png", context);
        Resources.bangTexture = new TiledTexture(dir + "bang.png", Metrics.snapperSize, Metrics.snapperSize, 0,0,0,0, context);
        Resources.blastTexture = new TiledTexture(dir + "blast.png", Metrics.blastSize, Metrics.blastSize, 0,0,0,0, context);
        Resources.snapperTexture = new TiledTexture(dir + "back.png", Metrics.snapperSize, Metrics.snapperSize, 0,0,0,0, context, Texture.Option.BILINEAR);
        Resources.squareButtons= new TiledTexture(dir + "btn-sq.png", Metrics.squareButtonSize, Metrics.squareButtonSize, 0,0,0,0, context);

        Resources.eyesTexture.setReusable(true);
        Resources.eyeShadowTexture.setReusable(true);
        Resources.shadowSnapper.setReusable(true);
        Resources.bangTexture.setReusable(true);
        Resources.blastTexture.setReusable(true);
        Resources.snapperTexture.setReusable(true);
    }

    private static void setSnapperMult(){
        switch (GameActivity.sizeMode){
            case modeS:
                Metrics.snapperMult1 = 1;
                Metrics.snapperMult2 = 0.9f;
                Metrics.snapperMult3 = 0.81f;
                Metrics.snapperMult4 = 0.73f;
                break;
            case modeM:
                Metrics.snapperMult1 = 1.23f;
                Metrics.snapperMult2 = 1.11f;
                Metrics.snapperMult3 = 1;
                Metrics.snapperMult4 = 0.9f;
                break;
            case modeL:
                Metrics.snapperMult1 = 1;
                Metrics.snapperMult2 = 0.9f;
                Metrics.snapperMult3 = 0.81f;
                Metrics.snapperMult4 = 0.73f;
                break;
        }
    }

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
        Resources.eyeFrames = makeForeBackFrames(5, 5);
        Resources.bangFrames = makeForeFrames(1, 4);
        Resources.blastFrames = makeForeFrames(1, 3);
        Resources.snapper1Frames = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.snapper1Frames.add(new AnimatedSprite.Frame(0, 3));
        Resources.snapper2Frames = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.snapper2Frames.add(new AnimatedSprite.Frame(0, 1));
        Resources.snapper3Frames = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.snapper3Frames.add(new AnimatedSprite.Frame(0, 2));
        Resources.snapper4Frames = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.snapper4Frames.add(new AnimatedSprite.Frame(0, 0));

        Resources.squareButtonDim = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonDim.add(new AnimatedSprite.Frame(0, 0));
        Resources.squareButtonHint = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonHint.add(new AnimatedSprite.Frame(1, 0));
        Resources.squareButtonPause = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonPause.add(new AnimatedSprite.Frame(2, 0));
        Resources.squareButtonForward = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonForward.add(new AnimatedSprite.Frame(3, 0));
        Resources.squareButtonRestart = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonRestart.add(new AnimatedSprite.Frame(0, 1));
        Resources.squareButtonShop = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonShop.add(new AnimatedSprite.Frame(0, 2));
        Resources.squareButtonMenu = new ArrayList<AnimatedSprite.Frame>(1);
        Resources.squareButtonMenu.add(new AnimatedSprite.Frame(0, 3));
    }
}
