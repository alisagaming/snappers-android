package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener {
    int width;
    int height;

    private SpriteBatch batch;
    private MainStage snappersStage;
    protected Level level;

    public Game() {
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        snappersStage = new MainStage(0, 0);
        Gdx.input.setInputProcessor(snappersStage);
        if (level != null)
            snappersStage.setLevel(level);
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;
        Metrics.setSize(width, height);
        Resources.loadTextures(false);
        snappersStage.setViewport(width, height);

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0,1,1,1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        snappersStage.act(Gdx.graphics.getDeltaTime());
        snappersStage.draw();
        

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
    }




    public void setStartLevel(Level level){
        this.level = level;



    }
}
