package ru.emerginggames.snappers.view;

import android.view.MotionEvent;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;

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
    public ButtonView(TiledTexture texture, int x, int y, ArrayList<Frame> frames, ArrayList<Frame> dimFrames) {
        super(texture, x, y);
        animate(0, 1, frames);

        dimSprite = new AnimatedSprite(texture, x, y);
        dimSprite.animate(0, 1, dimFrames);
        dimSprite.hide();
        addChild(dimSprite);
    }

    public void addToScene(E3Scene scene){
        scene.getTopLayer().add(this);
        scene.addEventListener(this);
    }

    @Override
    public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            dimSprite.show();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            dimSprite.hide();
        }
        return super.onTouchEvent(scene, shape, motionEvent, localX, localY);
    }

    @Override
    public void onDraw(GL10 _gl) {
        super.onDraw(_gl);
        dimSprite.onDraw(_gl);
    }
}
