package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.IGameEventListener;
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
    
    protected Mesh dimMesh;

    @Override
    public void create() {
        batch = new SpriteBatch();
        Resources.loadSquareButtonTextures();
        snappersStage = new MainStage(0, 0, this);
        gameOverStage = new GameOverStage(0,0,this, snappersStage.getLogic());
        pausedStage = new PausedStage(0, 0, this);
        currentStage = snappersStage;
        Gdx.input.setInputProcessor(snappersStage);

        if (level != null)
            snappersStage.setLevel(level);
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;
        Metrics.setSize(width, height);
        snappersStage.setViewport(width, height);
        gameOverStage.setViewport(width, height);
        pausedStage.setViewport(width, height);

        if (dimMesh != null)
            dimMesh.dispose();
        dimMesh = new Mesh(true, 4, 4,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        dimMesh.setVertices(new float[] { 0, 0, 0, Color.toFloatBits(0, 0, 0, 64),
                width, 0, 0, Color.toFloatBits(0, 0, 0, 64),
                0, height, 0, Color.toFloatBits(0, 0, 0, 64),
                width, height, 0, Color.toFloatBits(0, 0, 0, 64)});
        dimMesh.setIndices(new short[]{0, 1, 2, 3});

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0,1,1,1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        snappersStage.act(Gdx.graphics.getDeltaTime());
        snappersStage.draw();

        if (currentStage != snappersStage){
            Gdx.gl.glEnable(GL10.GL_BLEND);
            dimMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
            currentStage.draw();
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        snappersStage.dispose();
        gameOverStage.dispose();
        dimMesh.dispose();
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void gameWon() {
        gameOverStage.setWon(true);
        Gdx.input.setInputProcessor(currentStage = gameOverStage);
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
