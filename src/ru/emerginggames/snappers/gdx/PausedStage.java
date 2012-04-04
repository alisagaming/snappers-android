package ru.emerginggames.snappers.gdx;

import android.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.gdx.Elements.ColorRect;
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
public class PausedStage extends Stage{
    protected Sprite menuBack;
    protected SimpleButton resumeBtn;
    protected SimpleButton restartBtn;
    protected SimpleButton menuBtn;
    protected SimpleButton storeBtn;
    protected OutlinedTextSprite titleText;
    protected ColorRect dimRect;
    IGameEventListener listener;

    public PausedStage(int width, int height, IGameEventListener listener) {
        super(width, height, true);
        this.listener = listener;
        dimRect = new ColorRect(0,0,0,0);
        dimRect.setColor(0, 0, 0, 0.5f);
    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        dimRect.setPosition(0,0,width, height);
    }

    @Override
    public void draw() {
        batch.begin();
        batch.end();
        dimRect.draw();
        if (menuBack == null)
            createItems();
        batch.begin();
        menuBack.draw(batch);
        titleText.draw(batch);
        batch.end();
        super.draw();
    }

    protected void createItems(){
        menuBack = new Sprite(Resources.longDialog);
        menuBack.setPosition((width - menuBack.getWidth())/2, (height - menuBack.getHeight())/2);

        titleText = new OutlinedTextSprite("Game paused", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        titleText.setPosition((width - titleText.getWidth())/2, menuBack.getY() + menuBack.getHeight() * 0.95f - titleText.getHeight());

        resumeBtn = new SimpleButton(Resources.menuButtonFrames[10], Resources.menuButtonFrames[11], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onResumeBtn();
            }
        });
        resumeBtn.setPosition((width - resumeBtn.getWidth())/2, titleText.getY() - resumeBtn.getHeight() - Metrics.screenMargin);
        addActor(resumeBtn);

        restartBtn = new SimpleButton(Resources.menuButtonFrames[8], Resources.menuButtonFrames[9], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onRestartBtn();
            }
        });
        restartBtn.positionRelative(resumeBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);
        addActor(restartBtn);

        menuBtn = new SimpleButton(Resources.menuButtonFrames[6], Resources.menuButtonFrames[7], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onMenuBtn();
            }
        });
        menuBtn.positionRelative(restartBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);
        addActor(menuBtn);

        storeBtn = new SimpleButton(Resources.menuButtonFrames[12], Resources.menuButtonFrames[13], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onShopBtn();
            }
        });
        storeBtn.positionRelative(menuBtn, IPositionable.Dir.DOWN, Metrics.screenMargin);
        addActor(storeBtn);
    }

    public void resume(){
        if (titleText!= null)
            titleText.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (titleText != null)
            titleText.dispose();
        dimRect.dispose();
    }
}
