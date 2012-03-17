package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.event.AnimationEventListener;
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
    private static final float SHADOW_MULT = 1.125f;
    public int i;
    public int j;
    public int x;
    public int y;
    public int state;
    private AnimatedSprite eyes;
    private AnimatedSprite eyeShadow;
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

    public void addToScene(E3Scene scene, Layer layer){
        int sizeShift = GameActivity.Metrics.snapperSize / 2;
        shadow = new Sprite(GameActivity.Resources.shadowSnapper, x - sizeShift, y - sizeShift);
        back = new AnimatedSprite(GameActivity.Resources.snapperTexture, x - sizeShift, y - sizeShift);
        eyes = new AnimatedSprite(GameActivity.Resources.eyesTexture, x - sizeShift, y - sizeShift){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    controller.tapSnapper(SnapperView.this);
                return true;
            }
        };
        eyes.animate(EYE_ANIMATION_DELAY, GameActivity.Resources.eyeFrames);
        eyeShadow = new AnimatedSprite(GameActivity.Resources.eyeShadowTexture, x - sizeShift, y - sizeShift);
        eyeShadow.animate(EYE_ANIMATION_DELAY, GameActivity.Resources.eyeFrames);
        eyeShadow.setAlpha(0.5f);
        shadow.setAlpha(0.5f);

        shadow.setScaleCenter(sizeShift, sizeShift);
        eyeShadow.setScaleCenter(sizeShift, sizeShift);
        back.setScaleCenter(sizeShift, sizeShift);
        eyes.setScaleCenter(sizeShift, sizeShift);

        scene.addEventListener(eyes);
        layer.add(shadow);
        layer.add(eyeShadow);
        layer.add(back);
        layer.add(eyes);

        setViewState();
    }

    public void tap(){
        if (state <= 0)
            return;
        state--;
        setViewState();
        if (state == 0)
            addBang();
    }

    private void addBang(){
        int sizeShift = GameActivity.Metrics.snapperSize / 2;
        blastSprite = new AnimatedSprite(GameActivity.Resources.bangTexture, x - sizeShift, y - sizeShift);
        blastSprite.animate(BLAST_DELAY, 1, GameActivity.Resources.bangFrames);
        blastSprite.setEventListener(new AnimationEventListener() {
            @Override
            public void animationStarted(AnimatedSprite sprite) {}

            @Override
            public void animationFinished(AnimatedSprite sprite) {
                controller.layer.remove(blastSprite);
            }
        });
        controller.layer.add(blastSprite);
    }

    private void setViewState(){
        if (state>0 && state<5){
            float scale = getScale();
            float shadowScale = scale * SHADOW_MULT;
            back.animate(0, 1, getStateFrame());
            shadow.scale(shadowScale, shadowScale);
            eyeShadow.scale(shadowScale, shadowScale);
            back.scale(scale, scale);
            eyes.scale(scale, scale);

            shadow.show();
            eyeShadow.show();
            back.show();
            eyes.show();
        } else {
            shadow.hide();
            eyeShadow.hide();
            back.hide();
            eyes.hide();
        }
    }

    private ArrayList<AnimatedSprite.Frame> getStateFrame(){
        switch (state){
            case 1:
                return GameActivity.Resources.snapper1Frames;
            case 2:
                return GameActivity.Resources.snapper2Frames;
            case 3:
                return GameActivity.Resources.snapper3Frames;
            case 4:
                return GameActivity.Resources.snapper4Frames;
            default:
                return null;
        }
    }

    private float getScale(){
        switch (state){
            case 1:
                return GameActivity.Metrics.snapperMult1;
            case 2:
                return GameActivity.Metrics.snapperMult2;
            case 3:
                return GameActivity.Metrics.snapperMult3;
            case 4:
                return GameActivity.Metrics.snapperMult4;
            default:
                return 1;
        }
    }

}
