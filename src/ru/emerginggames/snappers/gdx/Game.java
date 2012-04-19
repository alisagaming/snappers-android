package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Settings;
import ru.emerginggames.snappers.gdx.stages.*;
import ru.emerginggames.snappers.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener{
    int width;
    int height;
    private SpriteBatch batch;
    protected Level level;
    protected InputProcessor currentInputProcessor;
    protected MyStage currentStage;
    protected MainStage mainStage;
    protected GameOverStage gameOverStage;
    protected PausedStage pausedStage;
    protected HintMenuStage hintMenu;
    protected HelpStage helpStage;
    protected Sprite bg;
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
        gameOverStage = new GameOverStage(width,height,gameListener, mainStage.getLogic(), batch);
        pausedStage = new PausedStage(width, height, gameListener, batch);
        hintMenu = new HintMenuStage(width, height, gameListener, batch);
        setStage(mainStage);

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
        if (currentStage != pausedStage)
            mainStage.act(delta);
        if (currentStage != helpStage)
            mainStage.draw();

        if (currentStage != mainStage){
            currentStage.act(delta);
            currentStage.draw();
        }

        batch.begin();
        if (Settings.DEBUG)
            Resources.fnt1.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, Resources.fnt1.getLineHeight() + mainStage.marginBottom);
        batch.end();
    }

    @Override
    public void pause() {
        if (currentStage != null)
            currentStage.unfocusAll();
        Gdx.input.setInputProcessor(currentInputProcessor = null);
    }

    @Override
    public void resume() {
        if (currentStage == hintMenu)
            hintMenu.onShow();
    }

    @Override
    public void dispose() {
        mainStage.dispose();
        gameOverStage.dispose();
        pausedStage.dispose();
        hintMenu.dispose();
        if (helpStage != null)
            helpStage.dispose();
    }

    public Level getLevel(){
        return mainStage.getLogic().level;
    }

    private void setStage(MyStage stage){
        if (stage == currentStage)
            return;

        if (stage == gameOverStage)
            mGameListener.gameoverStageShown();
        else if (currentStage == gameOverStage)
            mGameListener.gameoverStageHidden();

        if (stage == mainStage)
            mainStage.setDrawButtons(true);
        else if (currentStage == mainStage && stage != hintMenu)
            mainStage.setDrawButtons(false);

        if (currentStage != null){
            currentStage.onHide();
            currentStage.unfocusAll();
        }
        Gdx.input.setInputProcessor(currentInputProcessor = currentStage = stage);
        stage.onShow();
    }

    public void setTopAdHeight(int height){
        if (currentStage == gameOverStage)
            gameOverStage.setAdHeight(height);
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
        if (currentStage == gameOverStage)
            Gdx.app.exit();
        else if (currentStage == helpStage)
            setStage(gameOverStage);
        else if (currentStage == pausedStage)
            Gdx.app.exit();
        else if (currentStage == hintMenu)
            setStage(mainStage);
        else if (currentStage == mainStage)
            setStage(pausedStage);
    }

    private IGameEventListener gameListener = new IGameEventListener() {
        @Override
        public void onShopBtn() {
            mGameListener.launchStore();
        }

        @Override
        public void onNextBtn() {
            mainStage.nextLevel();
            setStage(mainStage);
        }

        @Override
        public void onRestartBtn() {
            mainStage.restartLevel();
            setStage(mainStage);
        }

        @Override
        public void onResumeBtn() {
            setStage(mainStage);
        }

        @Override
        public void onMenuBtn() {
            Gdx.app.exit();
        }

        @Override
        public void onHintBtn() {
            Level level = mainStage.getLogic().level;
            if (level.number < 4 && level.packNumber == 1){
                mainStage.showHints(true);
                return;
            }
            setStage(hintMenu);
        }

        @Override
        public void onPauseBtn() {
            setStage(pausedStage);
        }

        @Override
        public void gameWon() {
            setStage(gameOverStage);
            gameOverStage.show(true, mGameListener.getAdHeight());
            mGameListener.levelSolved(mainStage.getLogic().level);
            if (isSoundEnabled)
                Resources.winSound.play(0.6f);
        }

        @Override
        public void gameLost() {
            setStage(gameOverStage);
            gameOverStage.show(false, mGameListener.getAdHeight());
        }

        @Override
        public void levelPackWon() {
            mGameListener.levelPackWon(level.pack);
        }

        @Override
        public void useHint() {
            setStage(mainStage);
            mGameListener.useHint();
            mainStage.showHints(false);
        }

        @Override
        public void onHelp() {
            if (helpStage == null)
                helpStage = new HelpStage(width, height, batch, this);
            setStage(helpStage);
        }

        @Override
        public void onHelpDone() {
            setStage(gameOverStage);
        }

        @Override
        public IAppGameListener getAppListener() {
            return mGameListener;
        }
    };


}
