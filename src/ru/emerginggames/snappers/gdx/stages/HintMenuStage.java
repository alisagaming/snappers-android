package ru.emerginggames.snappers.gdx.stages;

import android.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.model.Goods;
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
 * Date: 06.04.12
 * Time: 11:27
 */
public class HintMenuStage extends MenuStage {
    private static enum Mode {Use, Buy, GetOnline}
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
    SimpleButton freeHintsButton;
    OutlinedTextSprite line1;
    OutlinedTextSprite line2;
    OutlinedTextSprite line3;
    boolean showLine3 = false;
    IGameEventListener mGame;
    Mode mode;



    public HintMenuStage(int width, int height, final IGameEventListener mGame, SpriteBatch batch) {
        super(width, height, true, batch);
        this.mGame = mGame;
        createElements();
        setMenuSize(Metrics.menuWidth, Metrics.menuHeight);
        if (width> 0)
            setViewport(width, height);
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);
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
        if (mode == Mode.Buy || mode == Mode.Use)
            freeHintsButton.positionRelative(cancelButton, IPositionable.Dir.UP, marg/2);
            freeHintsButton.visible = freeHintsButton.touchable = true;
        if (mode == Mode.Buy){
            buy10Button.positionRelative(freeHintsButton, IPositionable.Dir.UP, marg/2);
            buy1Button.positionRelative(buy10Button, IPositionable.Dir.UP, marg/2);
            buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable = true;
        } else if (mode == Mode.Use)
            useButton.positionRelative(freeHintsButton, IPositionable.Dir.UP, marg/2);

        IPositionable topButton = cancelButton;
        switch (mode){
            case Buy:
                topButton = buy1Button;
                break;
            case Use:
                topButton = useButton;
                break;
        }

        if (showLine3){
            line3.positionRelative(topButton.getX(), topButton.getTop() + marg*4, IPositionable.Dir.UPRIGHT, 0);
            line2.positionRelative(line3.getX(), line3.getTop() + marg, IPositionable.Dir.UPRIGHT, 0);
        }
        else
            line2.positionRelative(topButton.getX(), topButton.getTop() + marg * 4, IPositionable.Dir.UPRIGHT, 0);
        line1.positionRelative(line2.getX(), line2.getTop() + marg, IPositionable.Dir.UPRIGHT, 0);
    }

    private void setVisibility(){
        useButton.visible = useButton.touchable = buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable =
            freeHintsButton.visible = freeHintsButton.touchable = false;
        switch (mode){
            case Buy:
                freeHintsButton.visible = freeHintsButton.touchable = buy1Button.visible = buy1Button.touchable = buy10Button.visible = buy10Button.touchable = true;
                break;
            case Use:
                freeHintsButton.visible = freeHintsButton.touchable = useButton.visible = useButton.touchable = true;
                break;
            case GetOnline:
                break;
        }
    }

    public int calcContentHeight(){
        int height = Metrics.menuButtonHeight * 2 + Metrics.screenMargin/2 + Metrics.screenMargin*5 + line1.getTextHeight() * 2;
        if (mode == Mode.Buy)
            height+= (Metrics.menuButtonHeight  + Metrics.screenMargin/2);
        if (showLine3)
            height += (line3.getHeight() + Metrics.screenMargin);
        if (mode == Mode.Buy || mode == Mode.Use)
            height+= (Metrics.menuButtonHeight  + Metrics.screenMargin/2);
        return height;
    }

    public void createElements(){
        cancelButton = new SimpleButton("cancellong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                mGame.onResumeBtn();
            }
        });
        useButton = new SimpleButton("useahintlong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                mGame.useHint();
            }
        });
        buy1Button = new SimpleButton("buy1hint", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                buyHints(Goods.HintPack1);
            }
        });
        buy10Button = new SimpleButton("buyhintslong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                buyHints(Goods.HintPack10);
            }
        });
        freeHintsButton = new SimpleButton("freehintslong", Resources.buttonSound, new IOnEventListener() {
            @Override
            public void onEvent() {
                mGame.getAppListener().freeHintsPressed();
            }
        });
        addActor(cancelButton);
        addActor(useButton);
        addActor(buy1Button);
        addActor(buy10Button);
        addActor(freeHintsButton);

        int btnWidth = (Math.round(cancelButton.getWidth()));
        OutlinedTextSprite.FontStyle style = new OutlinedTextSprite.FontStyle(Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        line1 = new OutlinedTextSprite(btnWidth, style);
        line2 = new OutlinedTextSprite(btnWidth, style);
        line3 = new OutlinedTextSprite(btnWidth, style);
    }

    @Override
    public void onShow() {
        super.onShow();
        showLine3 = false;
        int hintsLeft = mGame.getAppListener().getHintsLeft();
        if (hintsLeft > 0)
            showUseHintMenu(hintsLeft);
        else if (mGame.getAppListener().isOnline())
                showBuyMenu();
        else
            showGetOnline();
        setInnerMenuHeight(calcContentHeight());
        positionItems();
        setVisibility();
    }

    private void showUseHintMenu(int hintsLeft){
        mode = Mode.Use;
        if (hintsLeft == 1)
            line1.setText(YOU_HAVE_1_HINT);
        else
            line1.setText(String.format(YOU_HAVE_D_HINTS, hintsLeft));
        line2.setText(USE_IT);
    }

    private void showGetOnline(){
        mode = Mode.GetOnline;
        line1.setText(YOU_HAVE_NO_HINTS);
        if (line2.measureTextWidth(GET_ONLINE_TO_GET_SOME) < getInnerWidth())
            line2.setText(GET_ONLINE_TO_GET_SOME);
        else{
            line2.setText(GET_ONLINE_TO_GET);
            line3.setText(SOME);
            showLine3 = true;
        }
    }

    private void showBuyMenu(){
        mode = Mode.Buy;
        line1.setText(YOU_HAVE_NO_HINTS);
        line2.setText(BUY_SOME);
    }

    private void buyHints(Goods goods){
        mGame.getAppListener().buy(goods);
    }
}
