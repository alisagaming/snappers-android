package ru.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.Resources;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.controller.IGameControlsListener;
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.03.12
 * Time: 18:55
 */
public class GameOverLayer extends HideableLayer{

    private ButtonView nextButton;
    private ButtonView restartButton;
    private ButtonView shopButton;
    private ButtonView menuButton;

    private OutlinedTextSprite wonText;
    private OutlinedTextSprite lostText;
    private OutlinedTextSprite scoreText;

    private final IGameControlsListener listener;
    private GameController gameController;
    private Context context;
    
    private int width;
    private int height;

    private Shape blackout;

    public GameOverLayer(int width, int height, IGameControlsListener listener, Context context) {
        super(true);
        this.width = width;
        this.height = height;
        this.listener = listener;
        this.context = context;

        defineContents();
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    private void defineContents(){
        int margin = Metrics.screenMargin;

        blackout = new Shape(0,0, width, height);
        blackout.setColor(0,0,0, 0.5f);        
        add(blackout);

        shopButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(1), margin, Resources.squareButtonShop, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onShopBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        shopButton.addToLayer(this);

        nextButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(2), margin, Resources.squareButtonForward, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onNextBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        nextButton.addToLayer(this);

        restartButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(3), margin, Resources.squareButtonRestart, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onRestartBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        restartButton.addToLayer(this);

        menuButton = new ButtonView(ru.emerginggames.snappers.Resources.squareButtons, getSquareButtonPosX(4), margin, Resources.squareButtonMenu, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                listener.onMenuBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuButton.addToLayer(this);

        wonText = new OutlinedTextSprite("Huzzah!", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);
        lostText = new OutlinedTextSprite("Level failed", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);
        scoreText = new OutlinedTextSprite("", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);

        int textPos = Math.round(height * 0.4f);
        wonText.move((width - wonText.getWidth())/2, textPos);
        lostText.move((width - lostText.getWidth())/2, textPos);
        scoreText.move(width, textPos + wonText.getTextHeight());
        add(wonText);
        add(lostText);
        add(scoreText);
    }

    public int getSquareButtonPosX(int n){
        return width - (Metrics.squareButtonSize + Metrics.screenMargin) * n;
    }

    public void showGameLostMenu() {
        wonText.hide();
        lostText.show();
        shopButton.moveX(getSquareButtonPosX(0));
        nextButton.moveX(getSquareButtonPosX(0));
        restartButton.moveX(getSquareButtonPosX(1));
        menuButton.moveX(getSquareButtonPosX(2));
        scoreText.setText(String.format("Possible in %d touches.", gameController.getLevel().tapsCount));
        scoreText.moveX((width - scoreText.getWidth())/2);

    }

    public void showGameWonMenu(){
        wonText.show();
        lostText.hide();
        scoreText.setText(String.format("Score: %d", gameController.getScore()));
        scoreText.moveX((width - scoreText.getWidth()) / 2);
        shopButton.moveX(getSquareButtonPosX(1));
        nextButton.moveX(getSquareButtonPosX(2));
        restartButton.moveX(getSquareButtonPosX(3));
        menuButton.moveX(getSquareButtonPosX(4));

    }
}
