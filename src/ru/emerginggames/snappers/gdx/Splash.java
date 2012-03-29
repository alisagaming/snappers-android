package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.w3c.dom.Text;
import ru.emerginggames.snappers.SplashGdxActivity;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:51
 */
public class Splash implements ApplicationListener {
    SplashGdxActivity activity;
    boolean done = false;
    Sprite bg;
    SpriteBatch batch;
    TextureRegion bgTex;

    public Splash(SplashGdxActivity activity) {
        this.activity = activity;
    }

    @Override
    public void create() {

        bgTex =Resources.normLoadTexture("Default.png", null);
        bg = new Sprite(bgTex);
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        if (!done)
            activity.gotSize(width, height);
        done = true;

        bg.setSize(width, height);
    }

    @Override
    public void render() {
        batch.begin();
        bg.draw(batch);
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        bgTex.getTexture().dispose();
    }
}
