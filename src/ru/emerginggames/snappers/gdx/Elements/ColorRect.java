package ru.emerginggames.snappers.gdx.Elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 28.03.12
 * Time: 5:44
 */
public class ColorRect {
    float width;
    float height;
    float x;
    float y;
    float r, g, b, a;

    Mesh mesh;

    public ColorRect(float x, float y, float width, float height) {
        mesh = new Mesh(true, 4, 4, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
        mesh.setIndices(new short[]{0, 1, 2, 3});
        setPosition(x, y, width, height);
    }

    public void setSize(float width, float height){
        setPosition(x, y, width, height);
    }

    public void sexPosition(float x, float y){
        setPosition(x, y, width, height);
    }

    public void setPosition(float x, float y, float width, float height){
        this.y = y;
        this.x = x;
        this.height = height;
        this.width = width;
        mesh.setVertices(new float[] { x, y, 0,
                width, y, 0,
                x, height, 0,
                width, height, 0});
    }

    public void setColor(float r, float g, float b, float a){
        this.r=r;
        this.g=g;
        this.b=b;
        this.a=a;
    }

    public void draw(){
        if (a<1)
            Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl10.glColor4f(r,g,b,a);
        mesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public void draw(float parentAlpha){
        if (a<1 || parentAlpha<1)
            Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl10.glColor4f(r,g,b,a * parentAlpha);
        mesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public void dispose(){
        mesh.dispose();
    }
}
