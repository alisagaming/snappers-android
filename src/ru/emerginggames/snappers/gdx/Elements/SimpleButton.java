package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.helper.IPositionable;
import ru.emerginggames.snappers.gdx.helper.PositionHelper;
import ru.emerginggames.snappers.gdx.core.PrepareableTextureAtlas.AtlasRegion;

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

    public SimpleButton(String textureName, Sound sound, IOnEventListener listener){
        /*button = doSprite(Resources.getBtnRegion(textureName));
        TextureRegion down = Resources.getBtnRegion(textureName + "-tap");
        if (down != null)
            buttonDown = doSprite(down);
        width = button.getWidth();
        height = button.getHeight();
        this.listener = listener;
        this.sound = sound;*/
    }

    public SimpleButton(String textureName, float scale, Sound sound, IOnEventListener listener){
        /*button = doSprite(Resources.getBtnRegion(textureName));
        button.setScale(scale);
        button.setOrigin(0, 0);
        TextureRegion down = Resources.getBtnRegion(textureName + "-tap");
        if (down != null){
            buttonDown = doSprite(down);
            buttonDown.setScale(scale);
            buttonDown.setOrigin(0, 0);
        }
        width = button.getWidth() * scale;
        height = button.getHeight() * scale;
        this.listener = listener;
        this.sound = sound;*/
    }

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
        button = doSprite(normal);
        button.setScale(scale);
        button.setOrigin(0, 0);
        if (down != null){
            buttonDown = doSprite(down);
            buttonDown.setScale(scale);
            buttonDown.setOrigin(0, 0);
        }
        width = normal.getRegionWidth() * scale;
        height = normal.getRegionHeight() * scale;
        this.listener = listener;
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

    public void unTouch(){
        isDown = false;
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
        return width;
    }

    @Override
    public float getHeight() {
        return height;
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
