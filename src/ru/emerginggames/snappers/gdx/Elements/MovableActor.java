package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.gdx.helper.IAnimationFunction;
import ru.emerginggames.snappers.gdx.helper.IPositionAnimationListener;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.04.12
 * Time: 1:08
 */
public abstract class MovableActor extends Actor {
    public float sourceX, sourceY, timePassed, animationTime, dx, dy;
    IPositionAnimationListener listener;
    IAnimationFunction animFn;
    boolean animationIdle = true;

    public void moveAct(float delta){
        if (animationIdle)
            return;
        if (timePassed >= animationTime){
            animationIdle = true;
            sourceX += dx;
            sourceY += dy;
            listener.onAnimationEnd(this);
        }
        else{
            timePassed += delta;
            animatePosition();
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {}

    @Override
    public Actor hit(float x, float y) {
        return null;
    }

    public abstract void setPosition (float x, float y);

    public float getAnimationTimeNorm(){
        return timePassed < animationTime ? timePassed/animationTime: 1;
    }

    public void setAll(float sourceX, float sourceY, float destX, float destY, float animationTime) {
        animationIdle = false;
        this.sourceX = sourceX;
        dx = destX - sourceX;
        this.sourceY = sourceY;
        dy = destY - sourceY;
        this.animationTime = animationTime;
        this.timePassed = 0;
        setPosition(sourceX, sourceY);
    }

    public void setListener(IPositionAnimationListener listener) {
        this.listener = listener;
    }

    public void setNext(float destX, float destY, float animationTime) {
        animationIdle = false;
        dx = destX - sourceX;
        dy = destY - sourceY;
        this.animationTime = animationTime;
        this.timePassed = 0;
    }

    public void setAnimFn(IAnimationFunction fn) {
        this.animFn = fn;
    }

    private void animatePosition(){
        if (animFn == null)
            return;
        float mult = animFn.getMult(getAnimationTimeNorm());

        float x = sourceX + dx * mult;
        float y = sourceY + dy * mult;
        setPosition(x, y);
    }
}
