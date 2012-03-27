package ru.emerginggames.snappers;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import com.e3roid.event.FrameListener;
import com.e3roid.event.SceneUpdateListener;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.controller.IGameOverListener;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.view.*;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:07
 */
public class GameActivityE3 extends E3Activity implements SceneUpdateListener, FrameListener, IGameEventListener {
    public static final String LEVEL_PARAM_TAG = "LEVEL";

    private long lastTimeUpdate;
    private GameController gameController;

    private ButtonView pauseButton;
    private ButtonView hintButton;
    
    private HideableLayer mainBtnLayer;
    private GameOverLayer gameOverLayer;
    private MainMenuLayer pausedLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        gameController = new GameController(gameOverListener, this);

    }

    @Override
    public E3Engine onLoadEngine() {
        E3Engine engine = new E3Engine(this, Metrics.screenWidth, Metrics.screenHeight, E3Engine.RESOLUTION_EXACT);
        engine.requestFullScreen();
        engine.requestPortrait();
        return engine;
    }

    @Override
    public E3Scene onLoadScene() {
        Level level = (Level)getIntent().getSerializableExtra(LEVEL_PARAM_TAG);

        MyScene scene = new MyScene();
        //scene.registerUpdateListener(10, this);
        lastTimeUpdate = SystemClock.uptimeMillis();
        scene.addFrameListener(this);

        scene.setBackgroundColor(0, 1f, 1f);

        Resources.createFrames();

        mainBtnLayer = new HideableLayer(false);
        gameOverLayer = new GameOverLayer(Metrics.screenWidth, Metrics.screenHeight, this, this);
        pausedLayer = new MainMenuLayer(Metrics.screenWidth, Metrics.screenHeight, this, this);
        scene.addEventListener(pausedLayer);
        scene.addEventListener(gameOverLayer);
        scene.addEventListener(mainBtnLayer);

        gameController.setScene(scene, Metrics.screenWidth, Metrics.screenHeight);
        gameOverLayer.setGameController(gameController);

        defineMainButtons();

        mainBtnLayer.moveReset();
        pausedLayer.moveReset();
        gameOverLayer.moveReset();
        scene.addLayer(mainBtnLayer);
        scene.addLayer(pausedLayer);
        scene.addLayer(gameOverLayer);
        pausedLayer.hide();
        gameOverLayer.hide();

        gameController.launchLevel(level);

        scene.addTexture(Resources.eyesTexture);
        scene.addTexture(Resources.eyeShadowTexture);
        scene.addTexture(Resources.snapperTexture);
        scene.addTexture(Resources.shadowSnapper);
        scene.addTexture(Resources.bangTexture);
        scene.addTexture(Resources.blastTexture);
        scene.addTexture(Resources.squareButtons);
        scene.addTexture(Resources.menuButtons);
        scene.addTexture(Resources.dialog);
        scene.addTexture(Resources.longDialog);

        return scene;
    }

    @Override
    protected void resume() {
        super.resume();

    }

    @Override
    public void onLoadResources() {
        Resources.loadResources(this);
    }
    
    private void defineMainButtons(){
        int margin = Metrics.screenMargin;

        pauseButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(1), margin, ru.emerginggames.snappers.Resources.squareButtonPause, ru.emerginggames.snappers.Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onPauseBtn();
                super.onTouchEvent(scene, shape, motionEvent, localX, localY);
                return true;
            }
        };
        pauseButton.addToLayer(mainBtnLayer);

        hintButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(2), margin, ru.emerginggames.snappers.Resources.squareButtonHint, ru.emerginggames.snappers.Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    onHintBtn();
                super.onTouchEvent(scene, shape, motionEvent, localX, localY);
                return true;
            }
        };
        hintButton.addToLayer(mainBtnLayer);
    }

    public int getSquareButtonPosX(int n){
        return Metrics.screenWidth - (Metrics.squareButtonSize + Metrics.screenMargin) * n;
    }

    private void showGameWonMenu(){
        LevelPackTable.setLevelSolved(gameController.getLevel(), this);
        showBtnLayer(gameOverLayer);
        gameOverLayer.showGameWonMenu();
    }
    private void  showGameLostMenu(){
        showBtnLayer(gameOverLayer);
        gameOverLayer.showGameLostMenu();
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

    @Override
    public void onShopBtn(){

    }

    @Override
    public void onNextBtn(){
        Level level = LevelTable.getNextLevel(this, gameController.getLevel());
        if (level == null)
            finish();

        gameController.launchLevel(level);
        showBtnLayer(mainBtnLayer);
    }

    @Override
    public void onRestartBtn(){
        showBtnLayer(mainBtnLayer);
        gameController.restartLevel();
    }

    @Override
    public void onMenuBtn(){
        finish();
    }

    @Override
    public void onHelpBtn() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onResumeBtn(){
        showBtnLayer(mainBtnLayer);
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
}
