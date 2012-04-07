package ru.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.IOnEventListener;
import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SimpleButton;
import ru.emerginggames.snappers.gdx.IAppGameListener;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.android.OutlinedTextSprite;

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
    protected SimpleButton nextButton;
    protected SimpleButton restartButton;
    protected SimpleButton shopButton;
    protected SimpleButton menuButton;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;

    public GameOverStage(int width, int height, final IGameEventListener listener, GameLogic logic, SpriteBatch batch) {
        super(width, height, true, batch);
        this.logic = logic;

        nextButton = new SimpleButton(Resources.squareButtonFrames[4], Resources.squareButtonFrames[5], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onNextBtn();
            }
        });

        restartButton = new SimpleButton(Resources.squareButtonFrames[6], Resources.squareButtonFrames[7], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onRestartBtn();
            }
        });

        shopButton = new SimpleButton(Resources.squareButtonFrames[8], Resources.squareButtonFrames[9], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onShopBtn();
            }
        });

        menuButton = new SimpleButton(Resources.squareButtonFrames[10], Resources.squareButtonFrames[11], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onMenuBtn();
            }
        });
        addActor(nextButton);
        addActor(restartButton);
        addActor(shopButton);
        addActor(menuButton);

        wonText = new OutlinedTextSprite(winMessages[0], Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        lostText = new OutlinedTextSprite(LEVEL_FAILED, Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        scoreText = new OutlinedTextSprite(String.format(POSSIBLE_IN_TOUCHES, 99), Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);

        if (width > 0)
            setViewport(width, height);
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);
        shopButton.positionRelative(width, height, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
        nextButton.positionRelative(shopButton, IPositionable.Dir.LEFT, Metrics.screenMargin/2);

        int textPos = Math.round(height * 0.6f);
        wonText.setPosition(Math.round((width - wonText.getWidth())/2), textPos);
        lostText.setPosition(Math.round((width - lostText.getWidth())/2), textPos);
        scoreText.positionRelative(wonText, IPositionable.Dir.DOWN, 5);
    }

    public void setWon(boolean isWon){
        this.isWon = isWon;

        if (isWon){
            int score = logic.getScore(((IAppGameListener) Gdx.app).isLevelSolved(logic.level));
            restartButton.positionRelative(nextButton, IPositionable.Dir.LEFT, Metrics.screenMargin /2);
            scoreText.setText(String.format(SCORE, score));
            wonText.setText(winMessages[(int)(Math.random()*winMessages.length)]);
            wonText.positionRelative(width/2, wonText.getY(), IPositionable.Dir.UP, 0);
            //wonText.setPosition((width - wonText.getWidth())/2, wonText.getY());
            ((IAppGameListener)Gdx.app).addScore(score);
        }
        else{
            restartButton.positionRelative(width, height, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
            scoreText.setText(String.format(POSSIBLE_IN_TOUCHES, logic.level.tapsCount));
        }

        scoreText.positionRelative(width/2, scoreText.getY(), IPositionable.Dir.UP, 0);


        nextButton.visible = nextButton.touchable = shopButton.visible = shopButton.touchable = isWon;
        menuButton.positionRelative(restartButton, IPositionable.Dir.LEFT, Metrics.screenMargin/2);
        show();
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
