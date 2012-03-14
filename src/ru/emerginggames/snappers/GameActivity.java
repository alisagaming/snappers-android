package ru.emerginggames.snappers;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.SceneUpdateListener;
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:07
 */
public class GameActivity extends E3Activity implements SceneUpdateListener {

    private final static int WIDTH  = 480;
    private final static int HEIGHT = 800;

    private final static int SPLASH_MSEC = 3000;

    private Sprite logo;
    private Texture logoTexture;
    private OutlinedTextSprite label;
    private AnimatedSprite eyesSprite;


    @Override
    public E3Engine onLoadEngine() {
        Display display = getWindowManager().getDefaultDisplay();
        //E3Engine engine = new E3Engine(this, WIDTH, HEIGHT);
        E3Engine engine = new E3Engine(this, display.getWidth(), display.getHeight());
        engine.requestFullScreen();
        engine.requestPortrait();
        return engine;
    }

    @Override
    public E3Scene onLoadScene() {
        E3Scene scene = new E3Scene();

        // start next activity after waiting 3 seconds.
        scene.registerUpdateListener(SPLASH_MSEC, this);

        int centerX = (getWidth()  - logoTexture.getWidth())  / 2;
        int centerY = (getHeight() - logoTexture.getHeight()) / 2;

        logo = new Sprite(logoTexture, centerX, centerY);

        // show logo in 3 seconds and scale logo in 1 seconds.

        scene.getTopLayer().add(logo);
        scene.setBackgroundColor(0, 1f, 1f);

        label.move((getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 3 * 2);

        scene.getTopLayer().add(label);

        Resources.eyeFrames = makeForeBackFrames(5,5);
        Resources.bangFrames = makeForeFrames(1, 4);
        Resources.blastFrames = makeForeFrames(1, 3);

        eyesSprite = new AnimatedSprite(Resources.eyesTexture, 200, 200);
        eyesSprite.setPosition(100, 300);
        eyesSprite.animate(50, Resources.eyeFrames);

        scene.getTopLayer().add(eyesSprite);

        return scene;
    }

    private ArrayList<AnimatedSprite.Frame> makeForeBackFrames(int width, int height){
        ArrayList<AnimatedSprite.Frame> frames = new ArrayList<AnimatedSprite.Frame>();

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

    private ArrayList<AnimatedSprite.Frame> makeForeFrames(int width, int height){
        ArrayList<AnimatedSprite.Frame> frames = new ArrayList<AnimatedSprite.Frame>();

        int i; int j;
        for (i=0;i<height;i++)
            for (j=0;j<width;j++)
                frames.add(new AnimatedSprite.Frame(j, i));

        return frames;
    }

    @Override
    public void onLoadResources() {
        logoTexture = new AssetTexture("logo.png", this);
        Typeface fnt = Typeface.createFromAsset(getAssets(), "shag_lounge.otf");
        label = new OutlinedTextSprite("Loading...",  50, Color.WHITE, Color.BLACK, Color.TRANSPARENT, fnt, this);
        int width = getWidth();

        String dir;
        int eyeSpriteSize;
        int bangSize;
        int blastSize;

        if (width <=320) {
            dir = "lo/";
            eyeSpriteSize = 32;
            bangSize = 40;
            blastSize = 9;
        }
        else if (width < 600) {
            dir = "med/";
            eyeSpriteSize = 48;
            bangSize = 60;
            blastSize = 18;
        }
        else {
            dir = "hi/";
            eyeSpriteSize = 96;
            bangSize = 120;
            blastSize = 36;
        }

        Resources.eyesTexture = new TiledTexture(dir + "eyes-s.png", eyeSpriteSize, eyeSpriteSize, 0, 0, 0, 0, this);
        Resources.shadowSnapper = new AssetTexture(dir + "shadow.png", this);
        Resources.redSnapper = new AssetTexture(dir + "red.png", this);
        Resources.yellowSnapper = new AssetTexture(dir + "yellow.png", this);
        Resources.greenSnapper = new AssetTexture(dir + "green.png", this);
        Resources.blueSnapper = new AssetTexture(dir + "blue.png", this);
        Resources.bangTexture = new TiledTexture(dir + "bang.png", bangSize, bangSize, 0,0,0,0, this);
        Resources.blastTexture = new TiledTexture(dir + "blast.png", blastSize, blastSize, 0,0,0,0, this);


        Resources.eyesTexture.setReusable(true);
        Resources.shadowSnapper.setReusable(true);
        Resources.redSnapper.setReusable(true);
        Resources.yellowSnapper.setReusable(true);
        Resources.greenSnapper.setReusable(true);
        Resources.blueSnapper.setReusable(true);
        Resources.bangTexture.setReusable(true);
        Resources.blastTexture.setReusable(true);
    }

    @Override
    public void onUpdateScene(E3Scene scene, long elapsedMsec) {
        scene.unregisterUpdateListener(this);
    }

    public static class Resources {
        public static TiledTexture eyesTexture;
        public static AssetTexture redSnapper;
        public static AssetTexture yellowSnapper;
        public static AssetTexture greenSnapper;
        public static AssetTexture blueSnapper;
        public static AssetTexture shadowSnapper;
        public static TiledTexture bangTexture;
        public static TiledTexture blastTexture;
        public static ArrayList<AnimatedSprite.Frame> eyeFrames;
        public static ArrayList<AnimatedSprite.Frame> bangFrames;
        public static ArrayList<AnimatedSprite.Frame> blastFrames;
    }
}
