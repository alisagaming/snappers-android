package ru.emerginggames.snappers;

import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.FrameListener;
import com.e3roid.event.SceneUpdateListener;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.controller.IGameOverListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.view.ButtonView;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:07
 */
public class GameActivity extends E3Activity implements SceneUpdateListener, FrameListener {
    public static enum SizeMode {
        modeS, modeM, modeL
    }
    public static SizeMode sizeMode;

    private final static int WIDTH  = 480;
    private final static int HEIGHT = 800;
    private long lastTimeUpdate;
    private GameController gameController;

    private ButtonView pauseButton;
    private ButtonView hintButton;
    private ButtonView nextButton;
    private ButtonView restartButton;
    private ButtonView shopButton;
    private ButtonView menuButton;
    
    private int width;
    private int height;

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

        ResourceLoader.createFrames();
        IGameOverListener gameOverListener = new IGameOverListener() {
            @Override
            public void gameWon() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void gameLost() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        width = getWidth();
        height = getHeight();
        gameController = new GameController(scene, width, height, gameOverListener, this);
        Level level = new Level();
        level.number = 123;
        level.complexity = 1;
        level.tapsCount = 99;
        level.packNumber = 12;
        level.zappers = "123412341234123412341234123412";

        defineButtons(scene);

        gameController.launchLevel(level);

        return scene;
    }

    @Override
    public void onLoadResources() {
        ResourceLoader.onLoadResources(this, getWidth());
        //label = new OutlinedTextSprite("Loading...",  50, Color.WHITE, Color.BLACK, Color.TRANSPARENT, fnt, this);
    }

    public void defineButtons(E3Scene scene){
        int buttonWidth = GameActivity.Metrics.squareButtonSize;
        int margin = Metrics.screenMargin;
        buttonWidth += margin;
        pauseButton = new ButtonView(Resources.squareButtons, width -  buttonWidth, margin, Resources.squareButtonPause, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onPauseBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        pauseButton.addToScene(scene);

        hintButton = new ButtonView(Resources.squareButtons, width - buttonWidth * 2, margin, Resources.squareButtonHint, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onHintBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        hintButton.addToScene(scene);

        shopButton = new ButtonView(Resources.squareButtons, width - buttonWidth, margin, Resources.squareButtonShop, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onShopBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        shopButton.addToScene(scene);
        shopButton.hide();

        nextButton = new ButtonView(Resources.squareButtons, width - buttonWidth * 2, margin, Resources.squareButtonForward, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onNextBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        nextButton.addToScene(scene);
        nextButton.hide();

        restartButton = new ButtonView(Resources.squareButtons, width - buttonWidth * 3, margin, Resources.squareButtonRestart, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onRestartBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        restartButton.addToScene(scene);
        restartButton.hide();

        menuButton = new ButtonView(Resources.squareButtons, width - buttonWidth * 4, margin, Resources.squareButtonMenu, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onMenuBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuButton.addToScene(scene);
        menuButton.hide();

    }

    private void onPauseBtn(){

    }

    private void onHintBtn(){

    }

    private void onShopBtn(){

    }

    private void onNextBtn(){

    }

    private void onRestartBtn(){
        gameController.restartLevel();
    }

    private void onMenuBtn(){
        finish();
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
        public static Typeface font;
        public static TiledTexture eyesTexture;
        public static TiledTexture eyeShadowTexture;
        public static TiledTexture snapperTexture;
        public static AssetTexture shadowSnapper;
        public static TiledTexture bangTexture;
        public static TiledTexture blastTexture;
        public static TiledTexture squareButtons;
        public static ArrayList<AnimatedSprite.Frame> eyeFrames;
        public static ArrayList<AnimatedSprite.Frame> bangFrames;
        public static ArrayList<AnimatedSprite.Frame> blastFrames;
        public static ArrayList<AnimatedSprite.Frame> snapper1Frames;
        public static ArrayList<AnimatedSprite.Frame> snapper2Frames;
        public static ArrayList<AnimatedSprite.Frame> snapper3Frames;
        public static ArrayList<AnimatedSprite.Frame> snapper4Frames;
        public static ArrayList<AnimatedSprite.Frame> squareButtonDim;
        public static ArrayList<AnimatedSprite.Frame> squareButtonHint;
        public static ArrayList<AnimatedSprite.Frame> squareButtonPause;
        public static ArrayList<AnimatedSprite.Frame> squareButtonForward;
        public static ArrayList<AnimatedSprite.Frame> squareButtonRestart;
        public static ArrayList<AnimatedSprite.Frame> squareButtonShop;
        public static ArrayList<AnimatedSprite.Frame> squareButtonMenu;
    }

    public static class Metrics{
        public static int snapperSize;
        public static int blastSize;
        public static int squareButtonSize;
        public static int infoFontSize;
        public static int screenMargin;
        public static float snapperMult1;
        public static float snapperMult2;
        public static float snapperMult3;
        public static float snapperMult4;

        //fill screen width & height with values actual for tablets
        public static int screenWidth;
        public static int screenHeight;
    }
}
