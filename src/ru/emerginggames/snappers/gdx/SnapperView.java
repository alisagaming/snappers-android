package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:09
 */
public class SnapperView extends Actor {
    private static final float SHADOW_MULT = 1.125f;
    private static final float SHADOW_OPACITY = 0.7f;
    private static final float EYE_FRAME_TIME = 0.05f;
    public int state;
    public int i;
    public int j;
    public float scale;
    public float shadowScale;
    
    public Sprite snapper;
    public Sprite shadow;
    public AnimatedSprite eyes;
    public AnimatedSprite eyeShadow;

    GameLogic logic;

    public SnapperView(GameLogic logic) {
        this.logic = logic;
        snapper = new Sprite(Resources.snapperBack[1]);
        shadow = new Sprite(Resources.shadowSnapper);
        shadow.setColor(1, 1, 1, SHADOW_OPACITY);
        eyes = new AnimatedSprite(Resources.eyeFrames, EYE_FRAME_TIME, true);
        eyeShadow = new AnimatedSprite(Resources.eyeShadowFrames, EYE_FRAME_TIME, true);
        eyeShadow.setOpacity(SHADOW_OPACITY);
        width = Metrics.snapperSize;
        height = Metrics.snapperSize;
    }

    public void set(int i, int j, int state){
        this.i = i;
        this.j = j;
        setPosition();
        setState(state);
    }

    public void setPosition(){
        int x = logic.getSnapperXPosision(i) - Metrics.snapperSize/2;
        int y = logic.getSnapperYPosision(j) - Metrics.snapperSize/2;
        this.x = x;
        this.y = y;
        snapper.setPosition(x,y);
        shadow.setPosition(x,y);
        eyes.move(x, y);
        eyeShadow.move(x, y);
    }

    public void touch(){
        state--;
        if (state == 0)
            markToRemove(true);
        else
            setState(state);

    }

    public void setState(int state){
        this.state = state;
        scale = getScale();
        shadowScale = scale * SHADOW_MULT;
        snapper.setRegion(Resources.snapperBack[state]);
        snapper.setScale(scale);
        shadow.setScale(shadowScale);
        eyes.setScale(scale);
        eyeShadow.setScale(shadowScale);
    }

    private float getScale(){
        switch (state){
            case 1:
                return Metrics.snapperMult1;
            case 2:
                return Metrics.snapperMult2;
            case 3:
                return Metrics.snapperMult3;
            case 4:
                return Metrics.snapperMult4;
            default:
                return 1;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {}

    @Override
    public boolean touchDown(float x, float y, int pointer) {
        return true;
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
        logic.touchSnapper2(i,j);
    }

    @Override
    public void touchDragged(float x, float y, int pointer) {}

    @Override
    public Actor hit(float x, float y) {
        return x > 0 && x < width && y > 0 && y < height ? this : null;
    }
}
