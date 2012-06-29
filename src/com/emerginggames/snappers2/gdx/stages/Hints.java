package com.emerginggames.snappers2.gdx.stages;

import android.util.FloatMath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emerginggames.snappers2.gdx.Elements.AnimatedSprite;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.gdx.helper.IPositionable;
import com.emerginggames.snappers2.logic.GameLogic;
import com.emerginggames.snappers2.model.Snappers;
import com.emerginggames.snappers2.model.Level;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 9:38
 */
public class Hints{
    private static final float HINT_ANIMATION_TIME = 0.06f;
    private static final float BACK_ANIMATION_PERIOD = 0.5f;
    GameLogic logic;
    int[] hints;
    int currentHint;
    AnimatedSprite hintSprite;
    Sprite backSprite;
    float animationTime;
    float backMaxScale;


    public Hints(GameLogic logic) {
        this.logic = logic;
        hintSprite = new AnimatedSprite(Resources.hintFrames, HINT_ANIMATION_TIME, true);

        backSprite = new Sprite(Resources.hintCircle);
        backMaxScale = Resources.hintFrames[0].originalWidth / Resources.hintCircle.getRegionWidth();
        backSprite.setScale(backMaxScale);

        updateLevel();
    }

    public void updateLevel(){
        decodeSolution(logic.level);
        currentHint = -1;
        updateHint();
    }

    private void decodeSolution(Level level){
        hints = new int[level.tapsCount];
        String[] chunks = level.solutions.split("[,;]");
        for (int i=0; i< level.tapsCount; i++)
            hints[i] = Integer.parseInt(chunks[i]);
    }

    public void updateHint(){
        currentHint++;
        if (currentHint >= hints.length)
            return;
        int pos = hints[currentHint];
        int i = pos% Snappers.WIDTH;
        int j = pos/Snappers.WIDTH;
        int x = logic.getSnapperXPosision(i);
        int y = logic.getSnapperYPosision(j);
        hintSprite.positionRelative(x, y, IPositionable.Dir.CENTER, 0);
        hintSprite.restartAnimation();
        animationTime = 0;
        backSprite.setPosition(x - backSprite.getRegionWidth()/2, y - backSprite.getRegionHeight()/2);
        backSprite.setOrigin(backSprite.getRegionWidth()/2, backSprite.getRegionHeight()/2);
    }

    public void draw(SpriteBatch batch){
        hintSprite.draw(batch);
    }

    public void drawBack(SpriteBatch batch){
        float a = FloatMath.sin(3.14f * animationTime / BACK_ANIMATION_PERIOD) * 0.2f + 0.5f;

        backSprite.draw(batch, a);
    }

    public void act(float delta){
        hintSprite.act(delta);
        animationTime += delta;
    }
}
