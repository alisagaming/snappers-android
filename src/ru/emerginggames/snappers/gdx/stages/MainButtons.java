package ru.emerginggames.snappers.gdx.stages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.gdx.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.IOnEventListener;
import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SimpleButton;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 23:59
 */
public class MainButtons extends Group {
    protected SimpleButton pauseButton;
    protected SimpleButton hintButton;


    public MainButtons(final IGameEventListener listener) {
        pauseButton = new SimpleButton(Resources.squareButtonFrames[2], Resources.squareButtonFrames[3], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onPauseBtn();
            }
        });
        hintButton = new SimpleButton(Resources.squareButtonFrames[0], Resources.squareButtonFrames[1], Metrics.squareButtonScale, Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onHintBtn();
            }
        });
        addActor(pauseButton);
        addActor(hintButton);
    }

    public void setViewport(int width, int height) {
        pauseButton.positionRelative(width, height, IPositionable.Dir.DOWNLEFT, Metrics.screenMargin);
        hintButton.positionRelative(pauseButton, IPositionable.Dir.LEFT, Metrics.screenMargin / 2);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }


}
