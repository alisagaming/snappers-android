package com.emerginggames.bestpuzzlegame.gdx.stages;

import android.util.FloatMath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emerginggames.bestpuzzlegame.gdx.Elements.AnimatedSprite;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.emerginggames.bestpuzzlegame.gdx.helper.IPositionable;
import com.emerginggames.bestpuzzlegame.logic.GameLogic;
import com.emerginggames.bestpuzzlegame.model.Snappers;
import com.emerginggames.bestpuzzlegame.model.Level;

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
    boolean hintWaiting;


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
        nextHint();
    }

    private void decodeSolution(Level level){
        hints = new int[level.tapsCount];
        String[] chunks = level.solutions.split("[,;]");
        for (int i=0; i< level.tapsCount; i++)
            hints[i] = Integer.parseInt(chunks[i]);
    }

    public void hintTapped(){
        hintWaiting = true;
    }

    public boolean isHintingSnapper(int i1, int j1){
        if (currentHint == -1)
            return false;
        if (currentHint >= hints.length)
            return false;

        int pos = hints[currentHint];
        int i = pos% Snappers.WIDTH;
        int j = pos/Snappers.WIDTH;
        return (i == i1 && j == j1);
    }

    void nextHint(){
        currentHint++;
        hintWaiting = false;
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
        if (!hintWaiting)
            hintSprite.draw(batch);
    }

    public void drawBack(SpriteBatch batch){
        if (!hintWaiting){
            float a = FloatMath.sin(3.14f * animationTime / BACK_ANIMATION_PERIOD) * 0.2f + 0.5f;
            backSprite.draw(batch, a);
        }
    }

    public void act(float delta){
        hintSprite.act(delta);
        animationTime += delta;
        if (hintWaiting && logic.countBlasts() == 0)
            nextHint();
    }
}
