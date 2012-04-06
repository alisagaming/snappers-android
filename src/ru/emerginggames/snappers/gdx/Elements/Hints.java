package ru.emerginggames.snappers.gdx.Elements;

import android.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.android.OutlinedTextSprite;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 9:38
 */
public class Hints implements IAnimationListener{
    private static final float HINT_ANIMATION_TIME = 0.1f;
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

        hintText = new OutlinedTextSprite("Tap here", Metrics.fontSize/2, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
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
