package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.event.AnimationEventListener;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.Resources;
import ru.emerginggames.snappers.controller.GameController;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 3:35
 */
public class SnapperView {
    private static final int EYE_ANIMATION_DELAY = 10;
    private static final int BLAST_DELAY = 60;
    private static final float SHADOW_MULT = 1.125f;
    private static final float SHADOW_OPACITY = 0.7f;
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
        int sizeShift = Metrics.snapperSize / 2;
        shadow = new Sprite(Resources.shadowSnapper, x - sizeShift, y - sizeShift);
        back = new AnimatedSprite(Resources.snapperTexture, x - sizeShift, y - sizeShift);
        eyes = new AnimatedSprite(Resources.eyesTexture, x - sizeShift, y - sizeShift){
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    controller.tapSnapper(SnapperView.this);
                return true;
            }
        };
        eyes.animate(EYE_ANIMATION_DELAY, Resources.eyeFrames);
        eyeShadow = new AnimatedSprite(Resources.eyeShadowTexture, x - sizeShift, y - sizeShift);
        eyeShadow.animate(EYE_ANIMATION_DELAY, Resources.eyeFrames);
        eyeShadow.setAlpha(SHADOW_OPACITY);
        shadow.setAlpha(SHADOW_OPACITY);

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

        addBang();
    }

    public void tap(){
        if (state <= 0)
            return;
        state--;
        setViewState();
        if (state == 0)
            showBang();
    }

    private void addBang(){
        int sizeShift = Metrics.snapperSize / 2;
        blastSprite = new AnimatedSprite(Resources.bangTexture, x - sizeShift, y - sizeShift);
        blastSprite.setEventListener(new AnimationEventListener() {
            @Override
            public void animationStarted(AnimatedSprite sprite) {
            }

            @Override
            public void animationFinished(AnimatedSprite sprite) {
                controller.layer.remove(blastSprite);
            }
        });
        blastSprite.hide();
        controller.layer.add(blastSprite);
    }

    private void showBang(){
        blastSprite.show();
        blastSprite.animate(BLAST_DELAY, 1, Resources.bangFrames);
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
                return Resources.snapper1Frames;
            case 2:
                return Resources.snapper2Frames;
            case 3:
                return Resources.snapper3Frames;
            case 4:
                return Resources.snapper4Frames;
            default:
                return null;
        }
    }

    private float getScale(){
        switch (state){
            case 1:
                return Metrics.snapperMult1;
            case 2:
                return Metrics.snapperMult2;
            case 3:
                return Metrics.snapperMult3;
            case 4:
                return Metrics.snapperMult4;
            default:
                return 1;
        }
    }

}
