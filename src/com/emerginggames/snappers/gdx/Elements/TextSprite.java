package com.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.04.12
 * Time: 20:40
 */
public class TextSprite {
    BitmapFont font;
    float x;
    float y;
    String text;

    public TextSprite(BitmapFont font, String text, int x, int y) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void Draw(SpriteBatch batch){
        font.draw(batch, text, x, y);
    }
}
