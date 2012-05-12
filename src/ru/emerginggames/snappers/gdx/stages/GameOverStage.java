package ru.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.logic.GameLogic;
import ru.emerginggames.snappers.gdx.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.IOnEventListener;
import ru.emerginggames.snappers.gdx.helper.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SimpleButton;
import ru.emerginggames.snappers.gdx.IAppGameListener;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.core.OutlinedTextSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 3:58
 */
public class GameOverStage extends DimBackStage{
    private static final String[] winMessages = {"Completed!", "Good job!", "Bravo!", "Cheers!", "Huzzah!", "Yippee!", "Hooray!"};
    private static final String LEVEL_FAILED = "Level failed";
    private static final String POSSIBLE_IN_TOUCHES = "Possible in %d touches.";
    private static final String SCORE = "Score: %d";
    protected GameLogic logic;
    protected boolean isWon = false;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;
    IGameEventListener mGame;

    public GameOverStage(int width, int height, final IGameEventListener listener, GameLogic logic, SpriteBatch batch) {
        super(width, height, true, batch);
        this.logic = logic;
        fadeinTime = 0.7f;
        mGame = listener;

        OutlinedTextSprite.FontStyle largeFont = new OutlinedTextSprite.FontStyle(Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        wonText = new OutlinedTextSprite(winMessages[0], largeFont);
        lostText = new OutlinedTextSprite(LEVEL_FAILED, largeFont);
        scoreText = new OutlinedTextSprite(String.format(POSSIBLE_IN_TOUCHES, 99), Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);

        if (width > 0)
            setViewport(width, height);
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);

        int textPos = Math.round(height * 0.55f);
        wonText.positionRelative(width/2, textPos, IPositionable.Dir.UP, 0);
        lostText.setPosition(Math.round((width - lostText.getWidth())/2), textPos);
        scoreText.positionRelative(wonText, IPositionable.Dir.DOWN, 5);
    }

    public void show(boolean isWon){
        this.isWon = isWon;

        if (isWon){
            int score = logic.getScore(mGame.getAppListener().isLevelSolved(logic.level));
            scoreText.setText(String.format(SCORE, score));
            mGame.getAppListener().addScore(score);
        }
        else
            scoreText.setText(String.format(POSSIBLE_IN_TOUCHES, logic.level.tapsCount));

        onShow();
    }

    @Override
    public void onHide() {
        if (isWon)
            wonText.setText(winMessages[(int)(Math.random()*winMessages.length)]);
    }


    @Override
    public void draw() {
        drawBack();
        super.draw();
        float opacity = getOpacity();
        batch.begin();
        root.draw(batch, opacity);

        if (isWon)
            wonText.draw(batch, opacity);
        else
            lostText.draw(batch, opacity);
        scoreText.draw(batch, opacity);


        batch.end();
    }
}
