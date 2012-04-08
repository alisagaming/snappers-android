package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.ColorRect;
import ru.emerginggames.snappers.gdx.stages.GameOverStage;
import ru.emerginggames.snappers.gdx.stages.HintMenuStage;
import ru.emerginggames.snappers.gdx.stages.MainStage;
import ru.emerginggames.snappers.gdx.stages.PausedStage;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener, IGameEventListener {
    int width;
    int height;

    private SpriteBatch batch;
    private MainStage snappersStage;
    protected Level level;
    protected Stage currentStage;
    protected GameOverStage gameOverStage;
    protected PausedStage pausedStage;
    protected HintMenuStage hintMenu;
    protected LevelPack levelPack;
    protected Sprite bg;
    FPSLogger logger;
    protected ColorRect tempRect;
    
    protected boolean objectsCreated = false;
    public static boolean isSoundEnabled;

    @Override
    public void create() {
        Resources.loadSounds();
        isSoundEnabled = ((IAppGameListener)Gdx.app).isSoundEnabled();
    }

    protected void createObjects(){
        if (objectsCreated)
            return;
        Resources.loadTextures(levelPack.isGold);

        batch = new SpriteBatch();
        snappersStage = new MainStage(width, height, this);
        gameOverStage = new GameOverStage(width,height,this, snappersStage.getLogic(), batch);
        pausedStage = new PausedStage(width, height, this, batch);
        hintMenu = new HintMenuStage(width, height, this, batch);
        currentStage = snappersStage;
        Gdx.input.setInputProcessor(snappersStage);

        if (level != null)
            snappersStage.setLevel(level);

        objectsCreated = true;
        if (Resources.loadBg(levelPack.background))
            bg = new Sprite(Resources.bg);

        logger = new FPSLogger();

        tempRect = new ColorRect(50, 50, 100, 100);
        tempRect.setColor(0,0,0,0.5f);
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;
        Metrics.setSize(width, height);

        createObjects();

        if (bg != null)
            bg.setSize(width, height);
    }

    @Override
    public void render() {
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
        if (currentStage != pausedStage)
            snappersStage.act(delta);
        snappersStage.draw();

        if (currentStage != snappersStage){
            currentStage.act(delta);
            currentStage.draw();
        }

        batch.begin();

        Resources.fnt1.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, Resources.fnt1.getLineHeight());
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        Texture.invalidateAllTextures(Gdx.app);
    }

    @Override
    public void dispose() {
        snappersStage.dispose();
        gameOverStage.dispose();
        pausedStage.dispose();
        hintMenu.dispose();
        Resources.disposeTextures();
    }

    public void setStartLevel(Level level, LevelPack pack){
        this.level = level;
        levelPack = pack;
    }

    @Override
    public void onShopBtn() {
        ((IAppGameListener)Gdx.app).launchStore();
    }

    @Override
    public void onNextBtn() {
        snappersStage.nextLevel();
        Gdx.input.setInputProcessor(currentStage = snappersStage);
    }

    @Override
    public void onRestartBtn() {
        snappersStage.restartLevel();
        Gdx.input.setInputProcessor(currentStage = snappersStage);
    }

    @Override
    public void onResumeBtn() {
        Gdx.input.setInputProcessor(currentStage = snappersStage);
    }

    @Override
    public void onMenuBtn() {
        Gdx.app.exit();
    }

    @Override
    public void onHintBtn() {
        Level level = snappersStage.getLogic().level;
        if (level.number < 4 && level.packNumber == 1){
            snappersStage.showHints(true);
            return;
        }
        hintMenu.show();
        Gdx.input.setInputProcessor(currentStage = hintMenu);
    }

    @Override
    public void useHint() {
        Gdx.input.setInputProcessor(currentStage = snappersStage);
        ((IAppGameListener)Gdx.app).useHint();
        snappersStage.showHints(false);
        if (snappersStage.areSnappersTouched())
            snappersStage.restartLevel();
    }

    @Override
    public void gameWon() {
        gameOverStage.setWon(true);
        if (isSoundEnabled)
            Resources.winSound.play(0.6f);
        Gdx.input.setInputProcessor(currentStage = gameOverStage);
        ((IAppGameListener)Gdx.app).levelSolved(snappersStage.getLogic().level);
    }

    @Override
    public void gameLost() {
        gameOverStage.setWon(false);
        Gdx.input.setInputProcessor(currentStage = gameOverStage);
    }

    @Override
    public void onPauseBtn() {
        pausedStage.show();
        Gdx.input.setInputProcessor(currentStage = pausedStage);
    }

    @Override
    public void levelPackWon() {
        ((IAppGameListener)Gdx.app).levelPackWon(levelPack);
    }

    public Level getLevel(){
        return snappersStage.getLogic().level;
    }

    public LevelPack getLevelPack(){
        return levelPack;
    }


}
