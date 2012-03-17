package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.event.SceneEventListener;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.03.12
 * Time: 1:42
 */
public class HideableLayer extends Layer{
    private boolean visible = true;
    protected boolean blockTouchEvents;
    protected ArrayList<SceneEventListener> eventListeners = new ArrayList<SceneEventListener>();

    public HideableLayer(boolean blockTouchEvents) {
        this.blockTouchEvents = blockTouchEvents;
    }

    public void setVisibility(boolean visibility){
        visible = visibility;
    }

    public void show(){
        visible = true;
    }

    public void hide(){
        visible = false;
    }

    @Override
    public void onDraw(GL10 gl) {
        if (visible)
            super.onDraw(gl);
    }

    /**
     * Add scene event listener
     *
     * @param listener SceneEventListener
     */
    public void addEventListener(SceneEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Remove scene event listener.
     *
     * @param listener SceneEventListener
     */
    public void removeEventListener(SceneEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public boolean onSceneTouchEvent(E3Scene scene, MotionEvent motionEvent) {
        if (!visible)
            return false;
        boolean handled = false;
        for (SceneEventListener listener : eventListeners) {
            handled = listener.onSceneTouchEvent(scene, motionEvent);
            if (handled) break;
        }
        return blockTouchEvents || handled;
    }
}
