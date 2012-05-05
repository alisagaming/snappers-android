package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.logic.GameLogic;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:09
 */
public class SnapperView extends MovableActor{
    private static final float SHADOW_MULT = 1.125f;
    private static final float EYE_FRAME_TIME = 0.04f;
    private static final float EYE_FRAME_TIME_DEVIATION = 0.3f;
    public static int halfSize;
    public int state;
    public int i;
    public int j;
    float yEyeShift;
    public float scale;
    public float shadowScale;

    public Sprite snapper;
    public AnimatedSprite eyes;

    GameLogic logic;

    public SnapperView(GameLogic logic) {
        this.logic = logic;
        snapper = new Sprite(Resources.snapperBack[1]);
        float frameTime = EYE_FRAME_TIME * ( 1 + (float)Math.random() * EYE_FRAME_TIME_DEVIATION * 2 - EYE_FRAME_TIME_DEVIATION);
        eyes = new AnimatedSprite(Resources.eyeFrames, frameTime, true);
        width = Metrics.snapperSize;
        height = Metrics.snapperSize;
        halfSize = Metrics.snapperSize/2;

    }

    public void set(int i, int j, int state){
        this.i = i;
        this.j = j;
        setState(state);
        setPosition();
    }

    public void setPosition(){
        int x = logic.getSnapperXPosision(i) - halfSize;
        int y = logic.getSnapperYPosision(j) - halfSize;
        this.x = x;
        this.y = y;
    }

    @Override
    public void setPosition (float x, float y) {
        x-= halfSize;
        y-= halfSize;
        snapper.setPosition(x, y);
        eyes.setPosition(x, y + yEyeShift);
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
        snapper.setRegion(Resources.snapperBack[state-1]);
        snapper.setScale(scale);
        eyes.setScale(scale);
        yEyeShift = Metrics.snapperSize/25f * scale;
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
    public void act(float delta) {
        super.act(delta);
        eyes.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {}

    @Override
    public boolean touchDown(float x, float y, int pointer) {
        return true;
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
        logic.tapSnapper(i, j);
    }

    @Override
    public Actor hit(float x, float y) {
        return state>0 && x > 0 && x < width && y > 0 && y < height ? this : null;
    }

    @Override
    public void setNext(float destX, float destY, float animationTime) {
        super.setNext(destX, destY, animationTime);
    }

    public void setRandomStart(int minX, int minY, int maxX, int maxY, float animationTime){
        float x = (float)(minX + Math.random() * (maxX - minX));
        float y = (float)(minY + Math.random() * (maxY - minY));
        setAll(x, y, this.x + halfSize, this.y + halfSize, animationTime);
    }

    public void shiftRandom(float time){
        float shiftY = ((float)(Math.random() * halfSize/12) + halfSize/14) * Math.signum(dy);
        /*float shiftX = (float)(Math.random() * halfSize/12) + halfSize/16;
        int rnd =  (int)(Math.random()*3);
        if (rnd %2 == 0)
            shiftX *= -Math.signum(dx);
        if (rnd>0)
            shiftY *= -Math.signum(dy);*/

        setNext(this.x + halfSize, this.y + halfSize - shiftY, time);
    }


}
