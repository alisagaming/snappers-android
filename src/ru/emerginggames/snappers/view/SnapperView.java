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
import ru.emerginggames.snappers.controller.GameLogic;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:35
 */
public class SnapperView implements ShapeEventListener{
    private static final int EYE_ANIMATION_DELAY = 50;
    private static final int BLAST_DELAY = 50;
    public int i;
    public int j;
    public int x;
    public int y;
    public int state;
    private AnimatedSprite eyesSprite;
    private Sprite shadow;
    private Sprite back;
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
    }

    public void addToScene(E3Scene scene){
        Layer layer = scene.getTopLayer();

        shadow = new Sprite(GameActivity.Resources.shadowSnapper, x, y);
        newBackSprite();
        newEyesSprite();


        layer.add(shadow);
        layer.add(back);
        layer.add(eyesSprite);
    }

    public void tap(){
        if (state <= 0)
            return;
        state--;
        Layer layer = controller.scene.getTopLayer();
        layer.remove(back);
        layer.remove(eyesSprite);
        if (state == 0){
            layer.remove(shadow);
            addBang(controller.scene);
        }
        else {
            newBackSprite();
            newEyesSprite();
            layer.add(back);
            layer.add(eyesSprite);
        }
    }

    private void addBang(final E3Scene scene){
        blastSprite = new AnimatedSprite(GameActivity.Resources.bangTexture, x,y);
        blastSprite.animate(BLAST_DELAY, 1, GameActivity.Resources.bangFrames);
        blastSprite.setEventListener(new AnimationEventListener() {
            @Override
            public void animationStarted(AnimatedSprite sprite) {
            }

            @Override
            public void animationFinished(AnimatedSprite sprite) {
                scene.getTopLayer().remove(blastSprite);
            }
        });
        scene.getTopLayer().add(blastSprite);
    }
    
    private void newBackSprite(){
        switch (state){
            case 0:
                return;
            case 1:
                back = new Sprite(GameActivity.Resources.redSnapper, x, y);
                break;
            case 2:
                back = new Sprite(GameActivity.Resources.yellowSnapper, x, y);
                break;
            case 3:
                back = new Sprite(GameActivity.Resources.greenSnapper, x, y);
                break;
            case 4:
                back = new Sprite(GameActivity.Resources.blueSnapper, x, y);
                break;
        }
    }

    private void newEyesSprite(){
        eyesSprite = new AnimatedSprite(GameActivity.Resources.eyesTexture, x, y);
        eyesSprite.animate(EYE_ANIMATION_DELAY, GameActivity.Resources.eyeFrames);
        eyesSprite.addListener(this);
    }

    @Override
    public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {

        return false;
    }

}
