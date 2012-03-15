package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.event.AnimationEventListener;
import com.e3roid.event.ShapeEventListener;
import ru.emerginggames.snappers.GameActivity;
import ru.emerginggames.snappers.controller.GameController;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:35
 */
public class SnapperView {
    private static final int EYE_ANIMATION_DELAY = 20;
    private static final int BLAST_DELAY = 60;
    public int i;
    public int j;
    public int x;
    public int y;
    public int state;
    private AnimatedSprite eyesSprite;
    private Sprite shadow;
    private AnimatedSprite back;
    private AnimatedSprite blastSprite;
    private GameController controller;

    public SnapperView(int i, int j, int state, GameController controller) {
        this.i = i;
        this.j = j;
        this.controller = controller;
        x = Math.round(controller.logic.getSnapperXPosision(i));
        y = Math.round(controller.logic.getSnapperYPosision(j));
        this.state = state;
    }

    public void setState(int state){
        this.state = state;
        setViewState();
    }

    public void addToScene(E3Scene scene){
        Layer layer = scene.getTopLayer();

        int sizeShift = GameActivity.Metrics.snapperSize / 2;
        shadow = new Sprite(GameActivity.Resources.shadowSnapper, x - sizeShift, y - sizeShift);
        back = new AnimatedSprite(GameActivity.Resources.snapperTexture, x - sizeShift, y - sizeShift);
        eyesSprite = new AnimatedSprite(GameActivity.Resources.eyesTexture, x - sizeShift, y - sizeShift){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    controller.tapSnapper(SnapperView.this);
                return true;
            }
        };
        eyesSprite.animate(EYE_ANIMATION_DELAY, GameActivity.Resources.eyeFrames);

        scene.addEventListener(eyesSprite);
        layer.add(shadow);
        layer.add(back);
        layer.add(eyesSprite);
        setViewState();
    }

    public void tap(){
        if (state <= 0)
            return;
        state--;
        setViewState();
        if (state == 0)
            addBang(controller.scene);
    }

    private void addBang(final E3Scene scene){
        int sizeShift = GameActivity.Metrics.bangSize / 2;
        blastSprite = new AnimatedSprite(GameActivity.Resources.bangTexture, x - sizeShift, y - sizeShift);
        blastSprite.animate(BLAST_DELAY, 1, GameActivity.Resources.bangFrames);
        blastSprite.setEventListener(new AnimationEventListener() {
            @Override
            public void animationStarted(AnimatedSprite sprite) {}

            @Override
            public void animationFinished(AnimatedSprite sprite) {
                scene.getTopLayer().remove(blastSprite);
            }
        });
        scene.getTopLayer().add(blastSprite);
    }

    private void setViewState(){
        if (state>0 && state<5){
            back.animate(0, 1, getStateFrame());
            shadow.show();
            back.show();
            eyesSprite.show();
        }
        else {
            shadow.hide();
            back.hide();
            eyesSprite.hide();
        }
    }

    private ArrayList<AnimatedSprite.Frame> getStateFrame(){
        switch (state){
            case 1:
                return GameActivity.Resources.snapperRedFrames;
            case 2:
                return GameActivity.Resources.snapperYellowFrames;
            case 3:
                return GameActivity.Resources.snapperGreenFrames;
            case 4:
                return GameActivity.Resources.snapperBlueFrames;
            default:
                return null;
        }
    }

}
