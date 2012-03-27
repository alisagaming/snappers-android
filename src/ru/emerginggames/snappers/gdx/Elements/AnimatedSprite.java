package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 23:15
 */
public class AnimatedSprite {
    public int x;
    public int y;
    public Animation animation;
    public Sprite sprite;
    public boolean looping;
    private float animationTime;

    public AnimatedSprite(TextureRegion[] sprites, float frameTime, boolean looping) {
        animation = new Animation(frameTime, sprites);
        sprite = new Sprite(sprites[0]);
        this.looping = looping;
        animationTime = 0;
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
        sprite.setPosition(x, y);
    }

    public void setScale(float scale){
        sprite.setScale(scale);
    }

    public void setOpacity(float opacity){
        sprite.setColor(1,1,1, opacity);
    }

    public void rotate(float angle){
        sprite.setRotation(angle);
    }

    public void update(){
        animationTime += Gdx.graphics.getDeltaTime();
        sprite.setRegion(animation.getKeyFrame(animationTime, looping));
    }

    public void draw(SpriteBatch batch){
        update();
        sprite.draw(batch);
    }
}
