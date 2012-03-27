package ru.emerginggames.snappers.gdx;

import android.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.IOnEventListener;
import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SimpleButton;
import ru.emerginggames.snappers.gdx.android.OutlinedTextSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 3:58
 */
public class GameOverStage extends Stage{
    protected GameLogic logic;
    protected boolean isWon = false;
    protected SimpleButton nextButton;
    protected SimpleButton restartButton;
    protected SimpleButton shopButton;
    protected SimpleButton menuButton;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;

    public GameOverStage(int width, int height, final IGameEventListener listener, GameLogic logic) {
        super(width, height, true);
        this.logic = logic;

        nextButton = new SimpleButton(Resources.squareButtonFrames[3], Resources.squareButtonFrames[0], new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onNextBtn();
            }
        });

        restartButton = new SimpleButton(Resources.squareButtonFrames[4], Resources.squareButtonFrames[0], new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onRestartBtn();
            }
        });

        shopButton = new SimpleButton(Resources.squareButtonFrames[5], Resources.squareButtonFrames[0], new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onShopBtn();
            }
        });

        menuButton = new SimpleButton(Resources.squareButtonFrames[6], Resources.squareButtonFrames[0], new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onMenuBtn();
            }
        });
        addActor(nextButton);
        addActor(restartButton);
        addActor(shopButton);
        addActor(menuButton);

        wonText = new OutlinedTextSprite("Huzzah!", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        lostText = new OutlinedTextSprite("Level failed", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        scoreText = new OutlinedTextSprite("", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        shopButton.positionRelative(width, height, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
        nextButton.positionRelative(shopButton, IPositionable.Dir.LEFT, Metrics.screenMargin);

        int textPos = Math.round(height * 0.6f);
        wonText.setPosition((width - wonText.getWidth())/2, textPos);
        lostText.setPosition((width - lostText.getWidth())/2, textPos);
        scoreText.positionRelative(wonText, IPositionable.Dir.DOWN, 5);
    }

    public void setWon(boolean isWon){
        this.isWon = isWon;

        if (isWon){
            restartButton.positionRelative(nextButton, IPositionable.Dir.LEFT, Metrics.screenMargin);
            scoreText.setText(String.format("Score: %d", logic.getScore()));
        }
        else{
            restartButton.positionRelative(width, height, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
            scoreText.setText(String.format("Possible in %d touches.", logic.level.tapsCount));
        }

        scoreText.setPosition((width - scoreText.getWidth())/2, scoreText.getY());

        nextButton.visible = nextButton.touchable = shopButton.visible = shopButton.touchable = isWon;
        menuButton.positionRelative(restartButton, IPositionable.Dir.LEFT, Metrics.screenMargin);

    }

    @Override
    public void draw() {
        super.draw();
        batch.begin();

        if (isWon)
            wonText.draw(batch);
        else
            lostText.draw(batch);
        scoreText.draw(batch);


        batch.end();
    }
}
