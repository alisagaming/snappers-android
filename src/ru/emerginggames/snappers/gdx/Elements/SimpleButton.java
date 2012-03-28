package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.emerginggames.snappers.gdx.helper.PositionHelper;

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

    public SimpleButton(TextureRegion normal, TextureRegion down, IOnEventListener listener){
        button = new Sprite(normal);
        buttonDown = new Sprite(down);
        width = button.getWidth();
        height = button.getHeight();
        this.listener = listener;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        button.draw(batch, parentAlpha);
        if (isDown)
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
        buttonDown.setPosition(x, y);
    }
}
