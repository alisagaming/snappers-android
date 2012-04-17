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
    protected SimpleButton nextButton;
    protected SimpleButton restartButton;
    protected SimpleButton shopButton;
    protected SimpleButton menuButton;
    protected SimpleButton helpButton;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;
    IGameEventListener mGame;

    public GameOverStage(int width, int height, final IGameEventListener listener, GameLogic logic, SpriteBatch batch) {
        super(width, height, true, batch);
        this.logic = logic;
        fadeinTime = 0.7f;
        mGame = listener;

        nextButton = new SimpleButton(Resources.squareButtonFrames[4], Resources.squareButtonFrames[5], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onNextBtn();
            }
        });

        restartButton = new SimpleButton(Resources.squareButtonFrames[6], Resources.squareButtonFrames[7], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onRestartBtn();
            }
        });

        shopButton = new SimpleButton(Resources.squareButtonFrames[8], Resources.squareButtonFrames[9], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onShopBtn();
            }
        });

        menuButton = new SimpleButton(Resources.squareButtonFrames[10], Resources.squareButtonFrames[11], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onMenuBtn();
            }
        });
        helpButton = new SimpleButton("help", Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onHelp();
            }
        });
        addActor(nextButton);
        addActor(restartButton);
        addActor(shopButton);
        addActor(menuButton);
        addActor(helpButton);

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
        helpButton.positionRelative(0, height, IPositionable.Dir.DOWNRIGHT, Metrics.screenMargin);

        int textPos = Math.round(height * 0.55f);
        wonText.positionRelative(width/2, textPos, IPositionable.Dir.UP, 0);
        lostText.setPosition(Math.round((width - lostText.getWidth())/2), textPos);
        scoreText.positionRelative(wonText, IPositionable.Dir.DOWN, 5);
    }

    public void show(boolean isWon, int adHeight){
        this.isWon = isWon;

        if (isWon){
            int score = logic.getScore(mGame.getAppListener().isLevelSolved(logic.level));
            scoreText.setText(String.format(SCORE, score));
            mGame.getAppListener().addScore(score);
        }
        else
            scoreText.setText(String.format(POSSIBLE_IN_TOUCHES, logic.level.tapsCount));

        setAdHeight(adHeight);

        nextButton.visible = nextButton.touchable = shopButton.visible = shopButton.touchable = isWon;
        helpButton.visible = helpButton.touchable = !isWon;

        onShow();
    }

    @Override
    public void onHide() {
        if (isWon)
            wonText.setText(winMessages[(int)(Math.random()*winMessages.length)]);
    }

    public void setAdHeight(int adHeight){
        if (isWon){
            shopButton.positionRelative(width, height - adHeight, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
            nextButton.positionRelative(shopButton, IPositionable.Dir.LEFT, Metrics.screenMargin/2);
            restartButton.positionRelative(nextButton, IPositionable.Dir.LEFT, Metrics.screenMargin /2);
        }
        else{
            restartButton.positionRelative(width, height - adHeight, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
            helpButton.positionRelative(0, height-adHeight, IPositionable.Dir.DOWNRIGHT, Metrics.screenMargin);
        }

        menuButton.positionRelative(restartButton, IPositionable.Dir.LEFT, Metrics.screenMargin/2);
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
