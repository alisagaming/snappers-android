package com.e3roid.drawable;

import com.e3roid.drawable.texture.Texture;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.03.12
 * Time: 20:11
 */
public class SpriteCopy extends Sprite{
    protected Sprite sourceSprite;

    public SpriteCopy(int x, int y, Sprite sourceSprite) {
        this.texture = sourceSprite.texture;
        this.sourceSprite = sourceSprite;
        setSize(sourceSprite.width, sourceSprite.height);
        setPosition(sourceSprite.x, sourceSprite.y);
        useDefaultRotationAndScaleCenter();
        coordBuffer = sourceSprite.coordBuffer;
        vertexBuffer = sourceSprite.vertexBuffer;
        indiceBuffer = sourceSprite.indiceBuffer;
        move(x,y);
    }


}
