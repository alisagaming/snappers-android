package ru.emerginggames.snappers.gdx.stages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.gdx.Elements.ColorRect;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 11:29
 */
public class DimBackStage extends Stage{
    public static final float FADEIN_TIME = 0.4f;
    private ColorRect dimRect;
    private float sinceShow;

    public DimBackStage(float width, float height, boolean stretch, SpriteBatch batch) {
        super(width, height, stretch, batch);
        dimRect = new ColorRect(0,0,0,0);
        dimRect.setColor(0, 0, 0, 0.5f);

    }

    public void setViewport(float width, float height) {
        super.setViewport(width, height, true);
        dimRect.setPosition(0,0,width, height);
    }

    public void show(){
        sinceShow = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sinceShow+=delta;
    }

    public void drawBack(){
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        dimRect.draw(getOpacity());
    }

    public float getOpacity(){
        float opacity = sinceShow/FADEIN_TIME;
        return opacity>1? 1: opacity;
    }

    @Override
    public void dispose() {
        super.dispose();
        dimRect.dispose();
    }
}
