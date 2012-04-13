package ru.emerginggames.snappers.gdx.stages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 13.04.12
 * Time: 21:06
 */
public class MyStage extends Stage {

    public MyStage(float width, float height, boolean stretch) {
        super(width, height, stretch);
    }

    public MyStage(float width, float height, boolean stretch, SpriteBatch batch) {
        super(width, height, stretch, batch);
    }

    public void onShow(){}

    public void onHide(){}

}
