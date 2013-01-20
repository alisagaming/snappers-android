package com.emerginggames.bestpuzzlegame.gdx.Elements;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.emerginggames.bestpuzzlegame.Metrics;
import com.emerginggames.bestpuzzlegame.logic.GameLogic;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 19:09
 */
public class SnapperView extends MovableActor {
    private static final float EYE_FRAME_TIME = 0.04f;
    private static final float EYE_FRAME_TIME_DEVIATION = 0.3f;
    private static final float SCALE_DURATION = 0.3f;
    private static final float EXPLODE_DURATION = 0.1f;
    protected static final float BANG_FRAME_DURATION = 0.12f;

    public static int halfSize;
    public int state;
    public int i;
    public int j;
    float yEyeShift;
    public float scale;
    float scaleTimer;
    float sourceScale;
    float targetScale;
    public Sprite snapper;
    public AnimatedSprite eyes;
    static OvershootInterpolator scaleInterpolator;
    static DecelerateInterpolator explodeInterpolator;
    SnapperFreeListener freeListener;
    AnimatedSprite bang;

    GameLogic logic;

    public SnapperView(GameLogic logic, SnapperFreeListener freeListener) {
        this.logic = logic;
        snapper = new Sprite(Resources.snapperBack[1]);
        float frameTime = EYE_FRAME_TIME * (1 + (float) Math.random() * EYE_FRAME_TIME_DEVIATION * 2 - EYE_FRAME_TIME_DEVIATION);
        eyes = new AnimatedSprite(Resources.eyeFrames, frameTime, true);
        height = width = Resources.eyeFrames[0].originalWidth;
        halfSize = Resources.eyeFrames[0].originalWidth / 2;

        if (scaleInterpolator == null)
            scaleInterpolator = new OvershootInterpolator(10);
        if (explodeInterpolator == null)
            explodeInterpolator = new DecelerateInterpolator(3);

        this.freeListener = freeListener;
        bang = new AnimatedSprite(Resources.bangFrames, BANG_FRAME_DURATION, bangListener);
        bang.setOrigin(bang.getWidth()/2, bang.getHeight()/2);
        bang.setScale(Metrics.snapperMult0);
    }

    public void set(int i, int j, int state) {
        this.i = i;
        this.j = j;
        targetScale = 0;
        setState(state);
        setPosition();
    }

    public void setPosition() {
        int x = logic.getSnapperXPosision(i) - halfSize;
        int y = logic.getSnapperYPosision(j) - halfSize;
        this.x = x;
        this.y = y;
    }

    @Override
    public void setPosition(float x, float y) {
        x -= halfSize;
        y -= halfSize;
        snapper.setPosition(x, y);
        eyes.setPosition(x, y + yEyeShift);
    }

    public void touch() {
        state--;
        setState(state);
    }

    public void setState(int state) {
        this.state = state;
        if (state > -1) {
            scaleTimer = 0;
            sourceScale = targetScale;
            targetScale = getScale();
            if (sourceScale == 0) {
                scale = sourceScale = targetScale;
                scaleTimer = SCALE_DURATION;
                snapper.setScale(scale);
                eyes.setScale(scale);
            }
            snapper.setRegion(Resources.snapperBack[state]);
        } else {
            bang.restartAnimation();
            bang.setPosition(logic.getSnapperXPosision(i) - bang.getWidth() / 2, logic.getSnapperYPosision(j) - bang.getHeight() / 2);
        }

    }

    private float getScale() {
        switch (state) {
            case 0:
                return Metrics.snapperMult0;
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
        if (state > 0) {
            super.act(delta);
            eyes.act(delta);
            if (scaleTimer < SCALE_DURATION) {
                scaleTimer += delta;
                scale = sourceScale + (targetScale - sourceScale) * scaleInterpolator.getInterpolation(Math.min(scaleTimer / SCALE_DURATION, 1));
                snapper.setScale(scale);
                eyes.setScale(scale);
            }
        } else if (state == 0){
            eyes.act(delta);
            if (scaleTimer < EXPLODE_DURATION) {
                scaleTimer += delta;
                scale = sourceScale + (targetScale - sourceScale) * explodeInterpolator.getInterpolation(Math.min(scaleTimer / EXPLODE_DURATION, 1));
                snapper.setScale(scale);
                eyes.setScale(scale);
            } else
                setState(-1);
        } else {
            bang.act(delta);
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if (state > -1) {
            snapper.draw(batch, parentAlpha);
            eyes.draw(batch, parentAlpha);
        } else
            bang.draw(batch, parentAlpha);
    }

    public void draw(SpriteBatch batch) {
        if (state > -1) {
            snapper.draw(batch);
            eyes.draw(batch);
        } else {
            bang.draw(batch);
        }
    }

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
        return state > 0 && x > 0 && x < width && y > 0 && y < height ? this : null;
    }

    @Override
    public void setNext(float destX, float destY, float animationTime) {
        super.setNext(destX, destY, animationTime);
    }

    public void setRandomStart(int minX, int minY, int maxX, int maxY, float animationTime) {
        float x = (float) (minX + Math.random() * (maxX - minX));
        float y = (float) (minY + Math.random() * (maxY - minY));
        setAll(x, y, this.x + halfSize, this.y + halfSize, animationTime);
    }

    public void shiftRandom(float time) {
        float shiftY = ((float) (Math.random() * halfSize / 12) + halfSize / 14) * Math.signum(dy);
        /*float shiftX = (float)(Math.random() * halfSize/12) + halfSize/16;
        int rnd =  (int)(Math.random()*3);
        if (rnd %2 == 0)
            shiftX *= -Math.signum(dx);
        if (rnd>0)
            shiftY *= -Math.signum(dy);*/

        setNext(this.x + halfSize, this.y + halfSize - shiftY, time);
    }

    public interface SnapperFreeListener {
        void onSnapperFree(SnapperView view);
    }

    IAnimationListener bangListener = new IAnimationListener() {
        @Override
        public void onAnimationEnd(AnimatedSprite sprite) {
            markToRemove(true);
            freeListener.onSnapperFree(SnapperView.this);
        }
    };
}
