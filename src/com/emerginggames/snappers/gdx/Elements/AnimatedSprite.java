package com.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emerginggames.snappers.gdx.helper.IPositionable;
import com.emerginggames.snappers.gdx.helper.PositionHelper;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 23:15
 */
public class AnimatedSprite extends Sprite implements IPositionable {
    protected Animation animation;
    protected boolean looping;
    protected float animationTime;
    protected float loopTime;
    protected IAnimationListener animationEndListener;
    protected TextureRegion[] sprites;


    public AnimatedSprite(TextureRegion[] sprites, float frameTime, boolean looping) {
        super(sprites[0]);
        animation = new Animation(frameTime, sprites);
        this.looping = looping;
        animationTime = 0;
        this.sprites = sprites;
    }

    public AnimatedSprite(TextureRegion[] sprites, float frameTime, IAnimationListener animationEndListener) {
        super(sprites[0]);
        animation = new Animation(frameTime, sprites);
        looping = false;
        animationTime = 0;
        loopTime = frameTime * sprites.length;
        this.sprites = sprites;
        this.animationEndListener = animationEndListener;
    }

    public void restartAnimation(){
        animationTime = 0;
        loopTime = animation.frameDuration * sprites.length;
        setRegion(sprites[0]);
    }

    public void setOpacity(float opacity){
        setColor(1,1,1, opacity);
    }

    public void act(float delta){
        animationTime += delta;
        setRegion(animation.getKeyFrame(animationTime, looping));
        if (loopTime>0 && animationTime>loopTime){
            loopTime = 0;
            animationEndListener.onAnimationEnd(this);
        }
    }

    @Override
    public void positionRelative(IPositionable other, Dir dir, float margin) {
        PositionHelper.Position(this, other, dir, margin);
    }

    @Override
    public void positionRelative(float x, float y, Dir dir, float margin) {
        PositionHelper.Position(x, y, this, dir, margin);
    }

    @Override
    public float getRight() {
        return getX() + getWidth();
    }

    @Override
    public float getTop() {
        return getY() + getHeight();
    }
}
