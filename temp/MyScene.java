package ru.emerginggames.snappers.view;

import com.e3roid.E3Scene;
import com.e3roid.drawable.texture.Texture;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 21.03.12
 * Time: 2:28
 */
public class MyScene extends E3Scene{
    protected boolean texturesLost = false;
    protected boolean firstRun = true;
    protected List<Texture> texturesList;

    public MyScene() {
        super();
        texturesList = new ArrayList<Texture>();
    }

    @Override
    public void onDraw(GL10 gl) {
        if (texturesLost){
            reloadTextures(gl);
            texturesLost = false;
        }
        super.onDraw(gl);
    }

    @Override
    public void onResume() {
        if (!firstRun)
            texturesLost = true;
        else
            firstRun = false;

        super.onResume();
    }

    public void addTexture(Texture texture){
        texturesList.add(texture);
    }

    public void removetexture(Texture texture){
        texturesList.remove(texture);
    }

    protected void reloadTextures(GL10 gl){
        Texture texture;
        for (int i=0; i<texturesList.size(); i++){
            texture = texturesList.get(i);
            texture.loadTexture(gl, true);
        }
    }
}
