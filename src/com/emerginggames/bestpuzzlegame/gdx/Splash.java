package com.emerginggames.bestpuzzlegame.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.emerginggames.bestpuzzlegame.SplashGdxActivity;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:51
 */
public class Splash implements ApplicationListener {
    SplashGdxActivity activity;
    boolean done = false;

    public Splash(SplashGdxActivity activity) {
        this.activity = activity;
    }

    @Override
    public void create() {
    }

    @Override
    public void resize(int width, int height) {
        if (!done)
            activity.gotSize(width, height);
        done = true;
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
