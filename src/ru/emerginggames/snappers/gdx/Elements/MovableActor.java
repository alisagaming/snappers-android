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
    public float sourceX, sourceY, destX, destY, timePassed, animationTime;
    IPositionAnimationListener listener;
    IAnimationFunction animFn;
    boolean animationIdle = true;

    public void moveAct(float delta){
        if (animationIdle)
            return;
        if (timePassed >= animationTime){
            animationIdle = true;
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
        this.destX = destX;
        this.sourceY = sourceY;
        this.destY = destY;
        this.animationTime = animationTime;
        this.timePassed = 0;
        setPosition(sourceX, sourceY);
    }

    public void setListener(IPositionAnimationListener listener) {
        this.listener = listener;
    }

    public void setNext(float destX, float destY, float animationTime) {
        animationIdle = false;
        this.destX = destX;
        this.destY = destY;
        this.animationTime = animationTime;
        this.timePassed = 0;
        setPosition(sourceX, sourceY);
    }

    public void setAnimFn(IAnimationFunction fn) {
        this.animFn = fn;
    }

    private void animatePosition(){
            float mult = animFn.getMult(getAnimationTimeNorm());
            float x = sourceX + (destX - sourceX) * mult;
            float y = sourceY + (destY - sourceY) * mult;
            setPosition(x, y);
    }
}
