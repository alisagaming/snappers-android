package com.emerginggames.snappers2.gdx.stages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 3:58
 */
public class DimStage extends DimBackStage{

    public DimStage(int width, int height, SpriteBatch batch) {
        super(width, height, true, batch);
        fadeinTime = 0.7f;

        if (width > 0)
            setViewport(width, height);
    }

    public void show(){
        onShow();
    }

    @Override
    public void draw() {
        drawBack();
        super.draw();
    }
}
