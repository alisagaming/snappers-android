package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.ShapeEventListener;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.03.12
 * Time: 9:24
 */
public class ButtonView extends AnimatedSprite{
    private AnimatedSprite dimSprite;
    private boolean hover = false;
    public ButtonView(TiledTexture texture, int x, int y, ArrayList<Frame> frames, ArrayList<Frame> dimFrames) {
        super(texture, x, y);
        animate(0, 1, frames);

        dimSprite = new AnimatedSprite(texture, x, y);
        dimSprite.animate(0, 1, dimFrames);
        dimSprite.hide();
        addChild(dimSprite);
    }

    public void addToLayer(HideableLayer layer){
        layer.add(this);
        layer.addEventListener(this);
    }

    @Override
    public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                dimSprite.show();
                hover = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dimSprite.hide();
                hover = false;
                break;
        }
        return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
    }

    @Override
    public void onDraw(GL10 _gl) {
        super.onDraw(_gl);
        dimSprite.onDraw(_gl);
    }

    @Override
    public boolean onSceneTouchEvent(E3Scene scene, MotionEvent motionEvent) {
        if (isRemoved() || isTransparent() || !isVisible()) return false;

        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            int globalX = scene.getEngine().getContext().getTouchEventX(scene, motionEvent, i);
            int globalY = scene.getEngine().getContext().getTouchEventY(scene, motionEvent, i);
            int localX = globalX - getRealX();
            int localY = globalY - getRealY();

            if (contains(globalX, globalY)) {
                return sendTouchEventToListeners(scene, this, motionEvent, localX, localY);
            }else if (hover && motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                boolean consumed = sendTouchEventToListeners(scene, this, motionEvent, localX, localY);
                motionEvent.setAction(MotionEvent.ACTION_MOVE);
                return consumed;
            }
        }

        return false;
    }

    private boolean sendTouchEventToListeners(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY){
        boolean consumed = false;
        consumed |= this.onTouchEvent(scene, this, motionEvent, localX, localY);
        for (ShapeEventListener listener : listeners) {
            consumed |= listener.onTouchEvent(scene, this, motionEvent, localX, localY);
        }
        return consumed;

    }

    @Override
    public void show() {
        super.show();
        dimSprite.hide();
    }
}
