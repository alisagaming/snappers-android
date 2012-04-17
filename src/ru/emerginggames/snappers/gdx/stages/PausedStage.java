package ru.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.gdx.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.IOnEventListener;
import ru.emerginggames.snappers.gdx.helper.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SimpleButton;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.core.OutlinedTextSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 3:58
 */
public class PausedStage extends MenuStage{
    public static final String GAME_PAUSED = "Game paused";
    protected SimpleButton resumeBtn;
    protected SimpleButton restartBtn;
    protected SimpleButton menuBtn;
    protected SimpleButton storeBtn;
    protected OutlinedTextSprite titleText;
    IGameEventListener listener;
    com.badlogic.gdx.graphics.Color color;

    public PausedStage(int width, int height, IGameEventListener listener, SpriteBatch batch) {
        super(width, height, true, batch);
        this.listener = listener;
        color = new com.badlogic.gdx.graphics.Color(1, 1, 1, 1);
        setMenuSize(Metrics.menuWidth, Metrics.menuHeight);
        if (width>0)
            setViewport(width, height);
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);
    }

    @Override
    public void draw() {
        if (resumeBtn == null)
            createItems();
        super.draw();
        batch.begin();
        titleText.draw(batch, getOpacity());
        batch.end();
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return super.touchUp(x, y, pointer, button);
    }

    protected void createItems(){
        titleText = new OutlinedTextSprite(GAME_PAUSED, Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 3, Resources.font);

        resumeBtn = new SimpleButton("resumelong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onResumeBtn();
            }
        });


        restartBtn = new SimpleButton("restartlong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onRestartBtn();
            }
        });


        menuBtn = new SimpleButton("menulong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onMenuBtn();
            }
        });


        storeBtn = new SimpleButton("storelong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onShopBtn();
            }
        });

        titleText.positionRelative(width/2, menuY + menuHeight * 0.97f, IPositionable.Dir.DOWN, 0);
        resumeBtn.setPosition((width - resumeBtn.getWidth())/2, titleText.getY() - resumeBtn.getHeight() - Metrics.screenMargin);
        restartBtn.positionRelative(resumeBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);
        menuBtn.positionRelative(restartBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);
        storeBtn.positionRelative(menuBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);

        addActor(resumeBtn);
        addActor(restartBtn);
        addActor(menuBtn);
        addActor(storeBtn);
    }
}
