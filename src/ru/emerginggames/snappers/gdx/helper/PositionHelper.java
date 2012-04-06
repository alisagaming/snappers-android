package ru.emerginggames.snappers.gdx.helper;

import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.IPositionable.Dir;

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

        item.setPosition(x, y);
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

        item.setPosition(x, y);
    }


}
