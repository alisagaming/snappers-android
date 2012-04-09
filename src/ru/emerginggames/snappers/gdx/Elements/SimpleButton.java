package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.core.PrepareableTextureAtlas;
import ru.emerginggames.snappers.gdx.helper.PositionHelper;
import ru.emerginggames.snappers.gdx.core.PrepareableTextureAtlas.AtlasRegion;
import ru.emerginggames.snappers.gdx.core.PrepareableTextureAtlas.AtlasSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 0:00
 */
public class SimpleButton extends Actor implements IPositionable {

    protected IOnEventListener listener;
    protected Sprite button;
    protected Sprite buttonDown;
    protected boolean isDown;
    protected Sound sound;
    protected float scale = 1;

    public SimpleButton(TextureRegion normal, TextureRegion down, Sound sound, IOnEventListener listener){
        button = doSprite(normal);
        if (down != null)
            buttonDown = doSprite(down);
        width = button.getWidth();
        height = button.getHeight();
        this.listener = listener;
        this.sound = sound;
    }

    public SimpleButton(TextureRegion normal, TextureRegion down, float scale, Sound sound,  IOnEventListener listener){
        button = new Sprite(normal);
        button.setScale(scale);
        if (down != null){
            buttonDown = new Sprite(down);
            buttonDown.setScale(scale);
        }
        width = normal.getRegionWidth() * scale;
        height = normal.getRegionHeight() * scale;
        this.listener = listener;
        this.scale = scale;
        this.sound = sound;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        button.draw(batch, parentAlpha);
        if (isDown && buttonDown != null)
            buttonDown.draw(batch, parentAlpha);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer) {
        isDown = true;
        return true;
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
        isDown = false;
        if (sound != null && Game.isSoundEnabled)
            sound.play();
        listener.onEvent();
    }

    @Override
    public void touchDragged(float x, float y, int pointer) {
    }

    @Override
    public Actor hit(float x, float y) {
        return x > 0 && x < width && y > 0 && y < height ? this : null;
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
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return button.getWidth();
    }

    @Override
    public float getHeight() {
        return button.getHeight();
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        button.setPosition(x, y);
        if (buttonDown != null)
            buttonDown.setPosition(x, y);
    }

    @Override
    public float getRight() {
        return getX() + getWidth();
    }

    @Override
    public float getTop() {
        return getY() + getHeight();
    }

    private Sprite doSprite(TextureRegion region){
        Sprite s = new Sprite(region);
        if (region instanceof AtlasRegion && ((AtlasRegion)region).rotate){
            s.setBounds(0, 0, region.getRegionHeight(), region.getRegionWidth());
            s.rotate90(true);
        }

        return s;
    }
}
