package ru.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.Resources;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.03.12
 * Time: 19:25
 */
public class MainMenuLayer extends HideableLayer{
    private int width;
    private int height;

    private final IGameEventListener listener;
    private Context context;

    private ButtonView menuResumeButton;
    private ButtonView menuRestartButton;
    private ButtonView menuMenuButton;
    private ButtonView menuStoreButton;

    private Shape blackout;
    private Sprite menuCont;

    private OutlinedTextSprite title;

    public MainMenuLayer(int width, int height, IGameEventListener listener, Context context) {
        super(true);
        this.width = width;
        this.height = height;
        this.listener = listener;
        this.context = context;

        defineContents();
    }

    private void defineContents(){
        blackout = new Shape(0,0, width, height);
        blackout.setColor(0,0,0, 0.5f);
        add(blackout);

        menuCont = new Sprite(Resources.longDialog, 0, 0);
        menuCont.move((width - menuCont.getWidth())/2, (height - menuCont.getHeight())/2);
        add(menuCont);
        int topPos = menuCont.getRealY() + menuCont.getHeight()/20;

        title = new OutlinedTextSprite("Game paused", Metrics.largeFontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);
        title.move((width - title.getWidth())/2, topPos);
        topPos += Math.round(title.getHeight() * 1.3f);
        add(title);

        int posX = (width - Resources.menuButtons.getTileWidth())/2;
        int btnHeight = Resources.menuButtons.getTileHeight();
        int margin = btnHeight/15;


        menuResumeButton = new ButtonView(Resources.menuButtons, posX, topPos, Resources.menuButtonResume, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onResumeBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuResumeButton.addToLayer(this);
        topPos += margin + btnHeight;

        menuRestartButton = new ButtonView(Resources.menuButtons, posX, topPos, Resources.menuButtonRestart, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onRestartBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuRestartButton.addToLayer(this);
        topPos += margin + btnHeight;

        menuMenuButton = new ButtonView(Resources.menuButtons, posX, topPos, Resources.menuButtonMenu, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onMenuBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuMenuButton.addToLayer(this);
        topPos += margin + btnHeight;

        menuStoreButton = new ButtonView(Resources.menuButtons, posX, topPos, Resources.menuButtonStore, Resources.buttonDim){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    listener.onShopBtn();
                return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
            }
        };
        menuStoreButton.addToLayer(this);

    }


}
