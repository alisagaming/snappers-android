package ru.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.model.GoodsToShop;
import ru.emerginggames.snappers.Metrics;
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
 * Date: 06.04.12
 * Time: 11:27
 */
public class HintMenuStage extends MenuStage {
    public static final String YOU_HAVE_1_HINT = "You have 1 hint.";
    public static final String YOU_HAVE_D_HINTS = "You have %d hints";
    public static final String USE_IT = "Use it?";
    public static final String YOU_HAVE_NO_HINTS = "You have no hints.";
    public static final String BUY_SOME = "Buy some!";
    public static final String GET_ONLINE_TO_GET = "Get online to get";
    public static final String SOME = "some.";
    public static final String GET_ONLINE_TO_GET_SOME = "Get online to get some.";
    SimpleButton cancelButton;
    SimpleButton useButton;
    SimpleButton buy1Button;
    SimpleButton buy10Button;
    OutlinedTextSprite line1;
    OutlinedTextSprite line2;
    OutlinedTextSprite line3;
    boolean showLine3 = false;
    IGameEventListener listener;


    public HintMenuStage(int width, int height, final IGameEventListener listener, SpriteBatch batch) {
        super(width, height, true, batch);
        this.listener = listener;
        createElements();
        setMenuSize(Metrics.menuWidth, Metrics.menuHeight);
        if (width> 0)
            setViewport(width, height);
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);
        positionItems();
    }

    @Override
    public void draw() {
        super.draw();
        batch.begin();
        line1.draw(batch, getOpacity());
        line2.draw(batch, getOpacity());
        if (showLine3)
            line3.draw(batch, getOpacity());
        batch.end();
    }

    private void  positionItems(){
        int marg = Metrics.screenMargin;
        cancelButton.positionRelative(width/2, getMenuBottom(), IPositionable.Dir.UP, 0);
        buy10Button.positionRelative(cancelButton, IPositionable.Dir.UP, marg/2);
        buy1Button.positionRelative(buy10Button, IPositionable.Dir.UP, marg/2);
        useButton.positionRelative(cancelButton, IPositionable.Dir.UP, marg/2);

        IPositionable topButton = buy1Button.visible ? buy1Button : useButton;
        if (showLine3){
            line3.positionRelative(topButton.getX(), topButton.getTop() + marg*4, IPositionable.Dir.UPRIGHT, 0);
            line2.positionRelative(line3.getX(), line3.getTop() + marg, IPositionable.Dir.UPRIGHT, 0);
        }
        else
            line2.positionRelative(topButton.getX(), topButton.getTop() + marg * 4, IPositionable.Dir.UPRIGHT, 0);
        line1.positionRelative(line2.getX(), line2.getTop() + marg, IPositionable.Dir.UPRIGHT, 0);
    }

    public int calcContentHeight(){
        int height = Metrics.menuButtonHeight * 2 + Metrics.screenMargin/2 + Metrics.screenMargin*5 + line1.getTextHeight() * 2;
        if (buy10Button.visible)
            height+= (Metrics.menuButtonHeight  + Metrics.screenMargin/2);
        if (showLine3)
            height += (line3.getHeight() + Metrics.screenMargin);
        return height;
    }

    public void createElements(){
        cancelButton = new SimpleButton(Resources.menuButtonFrames[4], Resources.menuButtonFrames[5], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.onResumeBtn();
            }
        });
        useButton = new SimpleButton(Resources.menuButtonFrames[14], Resources.menuButtonFrames[15], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                listener.useHint();
            }
        });
        buy1Button = new SimpleButton(Resources.menuButtonFrames[0], Resources.menuButtonFrames[1], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                buyHints(GoodsToShop.Goods.HintPack1);
            }
        });
        buy10Button = new SimpleButton(Resources.menuButtonFrames[2], Resources.menuButtonFrames[3], Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                buyHints(GoodsToShop.Goods.HintPack10);
            }
        });
        addActor(cancelButton);
        addActor(useButton);
        addActor(buy1Button);
        addActor(buy10Button);

        line1 = new OutlinedTextSprite(Metrics.menuButtonWidth, Metrics.fontSize, Color.WHITE, Color.BLACK, 2, Resources.font);
        line2 = new OutlinedTextSprite(Metrics.menuButtonWidth, Metrics.fontSize, Color.WHITE, Color.BLACK, 2, Resources.font);
        line3 = new OutlinedTextSprite(Metrics.menuButtonWidth, Metrics.fontSize, Color.WHITE, Color.BLACK, 2, Resources.font);
    }

    @Override
    public void show() {
        super.show();
        showLine3 = false;
        int hintsLeft = ((IAppGameListener)Gdx.app).getHintsLeft();
        if (hintsLeft > 0)
            showUseHintMenu(hintsLeft);
        else if (((IAppGameListener)Gdx.app).isOnline())
                showBuyMenu();
        else
            showGetOnline();
        setInnerMenuHeight(calcContentHeight());
        positionItems();
    }

    private void showUseHintMenu(int hintsLeft){
        if (hintsLeft == 1)
            line1.setText(YOU_HAVE_1_HINT);
        else
            line1.setText(String.format(YOU_HAVE_D_HINTS, hintsLeft));
        line2.setText(USE_IT);
        useButton.visible = useButton.touchable = true;
        buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable = false;
    }

    private void showGetOnline(){
        line1.setText(YOU_HAVE_NO_HINTS);
        if (line2.measureTextWidth(GET_ONLINE_TO_GET_SOME) < getInnerWidth())
            line2.setText(GET_ONLINE_TO_GET_SOME);
        else{
            line2.setText(GET_ONLINE_TO_GET);
            line3.setText(SOME);
            showLine3 = true;
        }
        useButton.visible = useButton.touchable = buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable = false;
    }

    private void showBuyMenu(){
        line1.setText(YOU_HAVE_NO_HINTS);
        line2.setText(BUY_SOME);
        useButton.visible = useButton.touchable = false;
        buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable = true;
    }

    private void buyHints(GoodsToShop.Goods goods){
        ((IAppGameListener)Gdx.app).buy(goods);
    }


}
