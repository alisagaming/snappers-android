package com.e3roid.drawable;

import com.e3roid.E3Engine;
import com.e3roid.drawable.Shape;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.03.12
 * Time: 19:26
 */
public class ShapeCopy extends Shape {
    protected Shape sourceShape;

    public ShapeCopy(Shape sourceShape, int x, int y) {
        setPosition(sourceShape.x, sourceShape.y);
        setSize(sourceShape.width, sourceShape.height);
        useDefaultRotationAndScaleCenter();
        this.sourceShape = sourceShape;
        vertexBuffer = sourceShape.vertexBuffer;
        indiceBuffer = sourceShape.indiceBuffer;
        move(x, y);
    }

    @Override
    public void onLoadSurface(GL10 gl) {}

    @Override
    public void onLoadSurface(GL10 gl, boolean force) {}
}
