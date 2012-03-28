package ru.emerginggames.snappers.gdx;

import android.content.Context;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.data.LevelPackTable;
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
    protected LevelPack levelPack;
    protected Sprite bg;
    
    protected boolean objectsCreated = false;

    @Override
    public void create() {
        Resources.loadSounds();
    }

    protected void createObjects(){
        if (objectsCreated)
            return;
        Resources.loadTextures(levelPack.isGold);

        batch = new SpriteBatch();
        snappersStage = new MainStage(0, 0, this);
        gameOverStage = new GameOverStage(0,0,this, snappersStage.getLogic());
        pausedStage = new PausedStage(0, 0, this);
        currentStage = snappersStage;
        Gdx.input.setInputProcessor(snappersStage);

        if (level != null)
            snappersStage.setLevel(level);

        objectsCreated = true;
        if (Resources.loadBg(levelPack.background))
            bg = new Sprite(Resources.bg);
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;
        Metrics.setSize(width, height);

        createObjects();

        snappersStage.setViewport(width, height);
        gameOverStage.setViewport(width, height);
        pausedStage.setViewport(width, height);
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
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        Texture.invalidateAllTextures(Gdx.app);
        snappersStage.resume();
        gameOverStage.resume();
        pausedStage.resume();
    }

    @Override
    public void dispose() {
        snappersStage.dispose();
        gameOverStage.dispose();
        pausedStage.dispose();
        Resources.disposeTextures();
    }

    public void setStartLevel(Level level, LevelPack pack){
        this.level = level;
        levelPack = pack;
    }

    @Override
    public void onShopBtn() {
        //To change body of implemented methods use File | Settings | File Templates.
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
        snappersStage.log();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void gameWon() {
        gameOverStage.setWon(true);
        Resources.winSound.play();
        Gdx.input.setInputProcessor(currentStage = gameOverStage);
        level = snappersStage.getLogic().level;
        if (levelPack.levelsUnlocked <= level.number){
            LevelPackTable.setLevelSolved(level, (Context)Gdx.app);
            levelPack.levelsUnlocked = level.number+1;
        }
    }

    @Override
    public void gameLost() {
        gameOverStage.setWon(false);
        Gdx.input.setInputProcessor(currentStage = gameOverStage);
    }

    @Override
    public void onPauseBtn() {
        currentStage = pausedStage;
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void levelPackWon() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
