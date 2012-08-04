package com.emerginggames.snappers2.gdx;

import android.util.FloatMath;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emerginggames.snappers2.gdx.stages.MainStage;
import com.emerginggames.snappers2.gdx.stages.MyStage;
import com.emerginggames.snappers2.Settings;
import com.emerginggames.snappers2.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener{
    public enum Stages{MainStage, GameOverStage, HelpStage, HintMenu}
    public Stages currentStageE;
    private static final float RAYS_SPEED =  - 360 / 10;
    private static final float RAYS_OPACITY =  0.8f;


    int width;
    int height;
    private SpriteBatch batch;
    protected Level level;
    protected InputProcessor currentInputProcessor;
    protected MyStage currentStage;
    protected MainStage mainStage;
    protected Sprite bg;
    boolean isPaused;
    public IAppGameListener mGameListener;
    protected Sprite rays;

    protected boolean objectsCreated = false;
    public static boolean isSoundEnabled;
    public boolean initDone = false;

    boolean showRays = false;

    public Game(Level level, IAppGameListener gameListener) {
        this.level = level;
        mGameListener = gameListener;
    }

    @Override
    public void create() {
        Resources.loadSounds();
        isSoundEnabled = mGameListener.isSoundEnabled();
    }

    protected void createObjects(){
        Resources.loadTextures(level.pack.isGold);

        batch = new SpriteBatch();
        mainStage = new MainStage(width, height, gameListener);
        setStage(Stages.MainStage);

        mainStage.setLevel(level);

        if (Resources.loadBg(level.pack.background))
            bg = new Sprite(Resources.bg);

        rays = new Sprite(Resources.rays);
        float screenDiag = FloatMath.sqrt(width * width + height * height);
        float w = rays.getWidth();
        float h = rays.getHeight();

        float scale = screenDiag / h;
        rays.setScale(scale);
        rays.setOrigin(w/2, h/2);
        rays.setPosition(width/2 - w/2, height/2 - h/2);

        objectsCreated = true;
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;
        if (!objectsCreated)
            createObjects();

        if (bg != null)
            bg.setSize(width, height);
        initDone = true;
        mGameListener.onInitDone();
    }



    @Override
    public void render() {
        if (currentInputProcessor == null)
            Gdx.input.setInputProcessor(currentInputProcessor = currentStage);
        Gdx.gl.glClearColor(0,1,1,1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        if (bg != null){
            batch.begin();
            batch.disableBlending();
            bg.draw(batch);
            batch.enableBlending();
            batch.end();
        }

        float delta = Gdx.graphics.getDeltaTime();
        /*if (delta > 1)
            delta = 0.01f;*/
        if (!isPaused)
            mainStage.act(delta);
        if (currentStageE != Stages.HelpStage)
            mainStage.draw();

        if (currentStage != mainStage && (currentStageE != Stages.HelpStage)) {
            currentStage.act(delta);
            currentStage.draw();
        }

        batch.begin();
        if (showRays){
            rays.rotate(delta * RAYS_SPEED);
            rays.draw(batch, RAYS_OPACITY);

        }
        if (Settings.DEBUG)
            Resources.fnt1.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, Resources.fnt1.getLineHeight() + mainStage.marginBottom);
        batch.end();
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void restartLevel(){
        mainStage.restartLevel();
    }

    public void nextLevel(){
        mainStage.nextLevel();
    }

    public void useHint() {
        setStage(Stages.MainStage);
        mainStage.showHints(false);
    }

    public boolean isHinting(){
        return mainStage.isHinting;
    }

    @Override
    public void pause() {
        if (currentStage != null)
            currentStage.unfocusAll();
        Gdx.input.setInputProcessor(currentInputProcessor = null);
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        mainStage.dispose();
    }

    public Level getLevel(){
        if (mainStage == null)
            return level;
        else
            return mainStage.getLogic().level;
    }

    public void showRays(boolean isShow){
        showRays = isShow;
    }

    private void setStage(MyStage stage){
        if (stage == currentStage)
            return;

        if (stage == mainStage)
            mainStage.setDrawButtons(true);
        else if (currentStage == mainStage)
            mainStage.setDrawButtons(false);

        if (currentStage != null){
            currentStage.onHide();
            currentStage.unfocusAll();
        }
        Gdx.input.setInputProcessor(currentInputProcessor = currentStage = stage);
        stage.onShow();
    }

    public void setStage(Stages stage){
        if (currentStageE == stage)
            return;

        mGameListener.onStageChanged(stage, currentStageE);

        currentStageE = stage;
        switch (stage){
            case MainStage:
                setStage(mainStage);
                break;

            case GameOverStage:
                if (mainStage.getLogic().isGameLost())
                    mGameListener.showGameLost(mainStage.getLogic().level);
                else{
                    int score = mainStage.getLogic().getScore(mGameListener.isLevelSolved(mainStage.getLogic().level));
                    mGameListener.levelSolved(mainStage.getLogic().level, score);
                }
                break;
        }
    }

    public Stages getStage() {
        return currentStageE;
    }

    public int getMarginBottom(){
        return mainStage.marginBottom;
    }

    public int getMaxMarginBottom(){
        return mainStage.maxMarginBottom;
    }

    public void resizeMarginBottom(int newMarginBottom){
        newMarginBottom = Math.min(mainStage.maxMarginBottom, (int)(newMarginBottom * 1.1f));
        mainStage.resizeGameRect(newMarginBottom);
    }

    public void backButtonPressed(){
        switch (currentStageE){
            case MainStage:
                mGameListener.showPaused();
                break;
            case GameOverStage:
                Gdx.app.exit();
                break;
            case HelpStage:
                setStage(Stages.GameOverStage);
                break;
            case HintMenu:
                setStage(Stages.MainStage);
                break;
        }
    }

    public boolean isFreeHint(){
        return mainStage.getLogic().level.number < 4 && mainStage.getLogic().level.packNumber == 1;
    }

    private IGameEventListener gameListener = new IGameEventListener() {

        @Override
        public void gameWon() {
            setStage(Stages.GameOverStage);
            if (isSoundEnabled)
                Resources.winSound.play();
        }

        @Override
        public void gameLost() {
            setStage(Stages.GameOverStage);
        }

        @Override
        public void levelPackWon() {
            mGameListener.levelPackWon(level.pack);
        }

        @Override
        public IAppGameListener getAppListener() {
            return mGameListener;
        }
    };


}
