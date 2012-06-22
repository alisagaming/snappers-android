package com.emerginggames.snappers.gdx.helper;

import com.emerginggames.snappers.gdx.helper.IPositionable.Dir;
import com.emerginggames.snappers.gdx.Elements.PositionInfo;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.03.12
 * Time: 1:11
 */
public class PositionHelper {
    public static void Position(IPositionable item, IPositionable other, IPositionable.Dir dir, float margin){
        float x = other.getX();
        float y = other.getY();

        if (dir == Dir.UP || dir == Dir.UPRIGHT || dir == Dir.UPLEFT)
            y+= (other.getHeight() + margin);
        else if (dir == Dir.DOWN || dir == Dir.DOWNRIGHT|| dir == Dir.DOWNLEFT)
            y -= (item.getHeight() + margin);
        else if (dir == Dir.CENTER || dir == Dir.LEFT || dir == Dir.RIGHT)
            y += (other.getHeight() - item.getHeight())/2;

        if (dir == Dir.RIGHT || dir == Dir.UPRIGHT || dir == Dir.DOWNRIGHT)
            x += (other.getWidth() + margin);
        else if (dir == Dir.LEFT || dir == Dir.UPLEFT|| dir == Dir.DOWNLEFT)
            x -= (item.getWidth() + margin );
        else if (dir == Dir.CENTER || dir == Dir.UP || dir == Dir.DOWN)
            x += (other.getWidth() - item.getWidth())/2;

        item.setPosition(Math.round(x), Math.round(y));
    }

    public static void Position(float x, float y, IPositionable item, IPositionable.Dir dir, float margin){
        if (dir == Dir.UP || dir == Dir.UPRIGHT || dir == Dir.UPLEFT)
            y+= margin;
        else if (dir == Dir.DOWN || dir == Dir.DOWNRIGHT|| dir == Dir.DOWNLEFT)
            y -= (item.getHeight() + margin);
        else if (dir == Dir.CENTER || dir == Dir.LEFT || dir == Dir.RIGHT)
            y -= item.getHeight()/2;


        if (dir == Dir.RIGHT || dir == Dir.UPRIGHT || dir == Dir.DOWNRIGHT)
            x += margin;
        else if (dir == Dir.LEFT || dir == Dir.UPLEFT|| dir == Dir.DOWNLEFT)
            x -= (item.getWidth() + margin );
        else if (dir == Dir.CENTER || dir == Dir.UP || dir == Dir.DOWN)
            x -= (item.getWidth()/2 + margin );

        item.setPosition(Math.round(x), Math.round(y));
    }

    public static void Position(IPositionable item, PositionInfo info){
        if (info.other != null)
            Position(item, info.other, info.dir, info.margin);
        else
            Position(info.x, info.y, item, info.dir, info.margin);
    }


}
