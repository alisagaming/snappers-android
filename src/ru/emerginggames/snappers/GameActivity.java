package ru.emerginggames.snappers;

import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.Display;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.FrameListener;
import com.e3roid.event.SceneUpdateListener;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.model.Level;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:07
 */
public class GameActivity extends E3Activity implements SceneUpdateListener, FrameListener {

    private final static int WIDTH  = 480;
    private final static int HEIGHT = 800;
    private long lastTimeUpdate;
    private GameController gameController;

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
        //scene.registerUpdateListener(10, this);
        lastTimeUpdate = SystemClock.uptimeMillis();
        scene.addFrameListener(this);

        scene.setBackgroundColor(0, 1f, 1f);
        createFremes();
        gameController = new GameController(scene, getWidth(), getHeight());
        Level level = new Level();
        level.number = 1;
        level.complexity = 1;
        level.zappers = "333332222211111111111111122222";

        gameController.launchLevel(level);

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

    private void createFremes(){
        Resources.eyeFrames = makeForeBackFrames(5, 5);
        Resources.bangFrames = makeForeFrames(1, 4);
        Resources.blastFrames = makeForeFrames(1, 3);
        Resources.snapperRedFrames = new ArrayList<AnimatedSprite.Frame>();
        Resources.snapperRedFrames.add(new AnimatedSprite.Frame(0, 3));
        Resources.snapperYellowFrames = new ArrayList<AnimatedSprite.Frame>();
        Resources.snapperYellowFrames.add(new AnimatedSprite.Frame(0, 2));
        Resources.snapperGreenFrames = new ArrayList<AnimatedSprite.Frame>();
        Resources.snapperGreenFrames.add(new AnimatedSprite.Frame(0, 1));
        Resources.snapperBlueFrames = new ArrayList<AnimatedSprite.Frame>();
        Resources.snapperBlueFrames.add(new AnimatedSprite.Frame(0, 0));
    }

    @Override
    public void onLoadResources() {
        Typeface fnt = Typeface.createFromAsset(getAssets(), "shag_lounge.otf");
        //label = new OutlinedTextSprite("Loading...",  50, Color.WHITE, Color.BLACK, Color.TRANSPARENT, fnt, this);
        int width = getWidth();

        String dir;

        if (width <=320) {
            dir = "lo/";
            Metrics.snapperSize = 32;
            Metrics.bangSize = 40;
            Metrics.blastSize = 9;
        }
        else if (width < 600) {
            dir = "med/";
            Metrics.snapperSize = 48;
            Metrics.bangSize = 48;
            Metrics.blastSize = 18;
        }
        else {
            dir = "hi/";
            Metrics.snapperSize = 96;
            Metrics.bangSize = 96;
            Metrics.blastSize = 36;
        }

        Resources.eyesTexture = new TiledTexture(dir + "eyes-s.png", Metrics.snapperSize, Metrics.snapperSize, 0, 0, 0, 0, this);
        Resources.shadowSnapper = new AssetTexture(dir + "shadow.png", this);
        Resources.bangTexture = new TiledTexture(dir + "bang.png", Metrics.bangSize, Metrics.bangSize, 0,0,0,0, this);
        Resources.blastTexture = new TiledTexture(dir + "blast.png", Metrics.blastSize, Metrics.blastSize, 0,0,0,0, this);
        Resources.snapperTexture = new TiledTexture(dir + "back.png", Metrics.snapperSize, Metrics.snapperSize, 0,0,0,0, this);


        Resources.eyesTexture.setReusable(true);
        Resources.shadowSnapper.setReusable(true);
        Resources.bangTexture.setReusable(true);
        Resources.blastTexture.setReusable(true);
        Resources.snapperTexture.setReusable(true);


    }

    @Override
    public void onUpdateScene(E3Scene scene, long elapsedMsec) {
        gameController.update(elapsedMsec);
    }

    @Override
    public void beforeOnDraw(E3Scene scene, GL10 gl) {
        long now = SystemClock.uptimeMillis();
        long elapsedMsec = now - lastTimeUpdate;
        lastTimeUpdate = now;
        gameController.update(elapsedMsec);
    }

    @Override
    public void afterOnDraw(E3Scene scene, GL10 gl) {
    }

    public static class Resources {
        public static TiledTexture eyesTexture;
        public static TiledTexture snapperTexture;
        public static AssetTexture shadowSnapper;
        public static TiledTexture bangTexture;
        public static TiledTexture blastTexture;
        public static ArrayList<AnimatedSprite.Frame> eyeFrames;
        public static ArrayList<AnimatedSprite.Frame> bangFrames;
        public static ArrayList<AnimatedSprite.Frame> blastFrames;
        public static ArrayList<AnimatedSprite.Frame> snapperRedFrames;
        public static ArrayList<AnimatedSprite.Frame> snapperYellowFrames;
        public static ArrayList<AnimatedSprite.Frame> snapperGreenFrames;
        public static ArrayList<AnimatedSprite.Frame> snapperBlueFrames;
    }

    public static class Metrics{
        public static int snapperSize;
        public static int bangSize;
        public static int blastSize;
    }
}
