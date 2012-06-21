package com.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.logic.GameLogic;
import com.emerginggames.snappers.gdx.Elements.AnimatedSprite;
import com.emerginggames.snappers.gdx.Elements.IAnimationListener;
import com.emerginggames.snappers.gdx.helper.IPositionable;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.gdx.core.OutlinedTextSprite;
import com.emerginggames.snappers.model.Level;
import com.emerginggames.snappers.model.Snappers;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 9:38
 */
public class Hints implements IAnimationListener {
    private static final float HINT_ANIMATION_TIME = 0.1f;
    public static final String TAP_HERE = "Tap here";
    boolean showText;
    boolean isTextVisible;
    GameLogic logic;
    int[] hints;
    int currentHint;
    AnimatedSprite hintSprite;
    OutlinedTextSprite hintText;

    public Hints(GameLogic logic, boolean showText) {
        this.logic = logic;
        hintSprite = new AnimatedSprite(Resources.hintFrames, HINT_ANIMATION_TIME, this);
        updateLevel(showText);
    }

    public void updateLevel(boolean showText){
        this.showText = showText;
        isTextVisible = false;
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
        int i = pos%Snappers.WIDTH;
        int j = pos/Snappers.WIDTH;
        int x = logic.getSnapperXPosision(i);
        int y = logic.getSnapperYPosision(j);
        hintSprite.positionRelative(x, y, IPositionable.Dir.CENTER, 0);
        hintSprite.restartAnimation();
    }

    @Override
    public void onAnimationEnd(AnimatedSprite sprite) {
        if (!showText)
            return;
        if (hintText == null)
            hintText = new OutlinedTextSprite(TAP_HERE, Metrics.fontSize*2/3, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        hintText.positionRelative(hintSprite, IPositionable.Dir.UP, 2);
        isTextVisible = true;
    }

    public void draw(SpriteBatch batch){
        hintSprite.draw(batch);
        if (isTextVisible)
            hintText.draw(batch);
    }

    public void act(float delta){
        hintSprite.act(delta);
    }
}
