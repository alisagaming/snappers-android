package com.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emerginggames.snappers.Settings;
import com.emerginggames.snappers.gdx.stages.DimStage;
import com.emerginggames.snappers.gdx.stages.MainStage;
import com.emerginggames.snappers.gdx.stages.MyStage;
import com.emerginggames.snappers.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener{
    public enum Stages{MainStage, GameOverStage, HelpStage, PausedStage, HintMenu}
    public Stages currentStageE;

    int width;
    int height;
    private SpriteBatch batch;
    protected Level level;
    protected InputProcessor currentInputProcessor;
    protected MyStage currentStage;
    protected MainStage mainStage;
    protected DimStage gameOverStage;
    protected Sprite bg;
    boolean isPaused;
    public IAppGameListener mGameListener;

    protected boolean objectsCreated = false;
    public static boolean isSoundEnabled;
    public boolean initDone = false;

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
        gameOverStage = new DimStage(width, height, batch);
        setStage(Stages.MainStage);

        mainStage.setLevel(level);

        if (Resources.loadBg(level.pack.background))
            bg = new Sprite(Resources.bg);

        objectsCreated = true;
    }

    @Override
    public void resize(int i, int i1) {
        mGameListener.gotScreenSize(width = i, height = i1);
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
        gameOverStage.dispose();
    }

    public Level getLevel(){
        if (mainStage == null)
            return level;
        else
            return mainStage.getLogic().level;
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

        if (currentStageE == Stages.GameOverStage)
            mGameListener.hideGameOverMenu();

        currentStageE = stage;
        switch (stage){
            case MainStage:
                setStage(mainStage);
                break;
            case HelpStage:
                break;
            case HintMenu:
                mGameListener.showHintMenu();
                break;
            case GameOverStage:
                setStage(gameOverStage);
                if (mainStage.getLogic().isGameLost())
                    mGameListener.showGameLost(mainStage.getLogic().level);
                else{
                    int score = mainStage.getLogic().getScore(mGameListener.isLevelSolved(level));
                    mGameListener.levelSolved(mainStage.getLogic().level, score);
                }
                break;
            case PausedStage:
                mGameListener.showPaused();
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
                setStage(Stages.PausedStage);
                break;
            case PausedStage:
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
            gameOverStage.show();
            if (isSoundEnabled)
                Resources.winSound.play(0.6f);
        }

        @Override
        public void gameLost() {
            setStage(Stages.GameOverStage);
            gameOverStage.show();
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
