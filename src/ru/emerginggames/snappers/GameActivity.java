package ru.emerginggames.snappers;

import android.graphics.Color;
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
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;
import ru.emerginggames.snappers.view.ButtonView;
import ru.emerginggames.snappers.view.HideableLayer;

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
    
    private HideableLayer mainBtnLayer;
    private HideableLayer gameOverLayer;
    private HideableLayer pausedLayer;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;
    private Shape blackout;

    
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

        mainBtnLayer = new HideableLayer(false);
        gameOverLayer = new HideableLayer(true);
        pausedLayer = new HideableLayer(true);
        scene.addEventListener(pausedLayer);
        scene.addEventListener(gameOverLayer);
        scene.addEventListener(mainBtnLayer);




        IGameOverListener gameOverListener = new IGameOverListener() {
            @Override
            public void gameWon() {
                showGameWonMenu();
            }

            @Override
            public void gameLost() {
                showGameLostMenu();
            }
        };
        width = getWidth();
        height = getHeight();
        gameController = new GameController(scene, width, height, gameOverListener, this);
        Level level = new Level();
        level.number = 123;
        level.complexity = 1;
        level.tapsCount = 10;
        level.packNumber = 12;
        level.zappers = "123412341234123412341234123412";

        defineMainButtons();
        defineGameOverLayerContents();
        definePauseLayerContents();

        scene.addLayer(mainBtnLayer);
        scene.addLayer(pausedLayer);
        scene.addLayer(gameOverLayer);
        pausedLayer.hide();
        gameOverLayer.hide();


        gameController.launchLevel(level);


        return scene;
    }

    @Override
    public void onLoadResources() {
        ResourceLoader.onLoadResources(this, getWidth());
        blackout = new Shape(0,0, getWidth(), getHeight());
        blackout.setColor(0,0,0, 0.5f);
        //label = new OutlinedTextSprite("Loading...",  50, Color.WHITE, Color.BLACK, Color.TRANSPARENT, fnt, this);
    }
    
    private void defineMainButtons(){
        int margin = Metrics.screenMargin;

        pauseButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(1), margin, Resources.squareButtonPause, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onPauseBtn();
                return true;
            }
        };
        pauseButton.addToLayer(mainBtnLayer);

        hintButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(2), margin, Resources.squareButtonHint, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onHintBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        hintButton.addToLayer(mainBtnLayer);
    }

    private void defineGameOverLayerContents(){
        int margin = Metrics.screenMargin;
        gameOverLayer.add(blackout);

        shopButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(1), margin, Resources.squareButtonShop, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onShopBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        shopButton.addToLayer(gameOverLayer);

        nextButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(2), margin, Resources.squareButtonForward, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onNextBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        nextButton.addToLayer(gameOverLayer);

        restartButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(3), margin, Resources.squareButtonRestart, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onRestartBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        restartButton.addToLayer(gameOverLayer);

        menuButton = new ButtonView(Resources.squareButtons, getSquareButtonPosX(4), margin, Resources.squareButtonMenu, Resources.squareButtonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                onMenuBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuButton.addToLayer(gameOverLayer);

        wonText = new OutlinedTextSprite("Huzzah!", Metrics.infoFontSize * 3/2, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, this);
        lostText = new OutlinedTextSprite("Level failed", Metrics.infoFontSize * 3/2, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, this);
        scoreText = new OutlinedTextSprite("", Metrics.infoFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, this);
        
        int textPos = Math.round(height * 0.4f);
        int center = width/2;
        wonText.move((width - wonText.getWidth())/2, textPos);
        lostText.move((width - lostText.getWidth())/2, textPos);
        scoreText.move(width, textPos + wonText.getTextHeight());
        gameOverLayer.add(wonText);
        gameOverLayer.add(lostText);
        gameOverLayer.add(scoreText);
    }
    
    public int getSquareButtonPosX(int n){
        return width - (Metrics.squareButtonSize + Metrics.screenMargin) * n;
    }

    private void definePauseLayerContents(){
        pausedLayer.add(blackout);
    }
    

    private void showGameLostMenu() {
        showBtnLayer(gameOverLayer);

        wonText.hide();
        lostText.show();
        shopButton.moveX(getSquareButtonPosX(0));
        nextButton.moveX(getSquareButtonPosX(0));
        restartButton.moveX(getSquareButtonPosX(1));
        menuButton.moveX(getSquareButtonPosX(2));
        scoreText.setText(String.format("Possible in %d touches.", gameController.logic.level.tapsCount));
        scoreText.moveX((width - scoreText.getWidth())/2);

    }

    private void showGameWonMenu(){
        showBtnLayer(gameOverLayer);
        wonText.show();
        lostText.hide();
        scoreText.setText(String.format("Score: %d", gameController.logic.level.complexity));
        scoreText.moveX((width - scoreText.getWidth()) / 2);
        shopButton.moveX(getSquareButtonPosX(1));
        nextButton.moveX(getSquareButtonPosX(2));
        restartButton.moveX(getSquareButtonPosX(3));
        menuButton.moveX(getSquareButtonPosX(4));

    }
    
    private void showBtnLayer(HideableLayer layer){
        mainBtnLayer.setVisibility(mainBtnLayer == layer);
        gameOverLayer.setVisibility(gameOverLayer == layer);
        pausedLayer.setVisibility(pausedLayer == layer);
    }

    private void onPauseBtn(){
        showBtnLayer(pausedLayer);
    }

    private void onHintBtn(){

    }

    private void onShopBtn(){

    }

    private void onNextBtn(){
        //TODO: get next level
        showBtnLayer(mainBtnLayer);
    }

    private void onRestartBtn(){
        gameController.restartLevel();
        showBtnLayer(mainBtnLayer);
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
        public static TiledTexture longButtons;
        public static AssetTexture dialog;
        public static AssetTexture longDialog;

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
        public static int longButtonWidth;
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
