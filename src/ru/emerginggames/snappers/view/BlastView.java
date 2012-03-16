package ru.emerginggames.snappers.view;

import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.modifier.AxisMoveModifier;
import com.e3roid.drawable.modifier.MoveModifier;
import com.e3roid.drawable.modifier.ProgressModifier;
import com.e3roid.drawable.modifier.ShapeModifier;
import com.e3roid.drawable.modifier.function.Linear;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.event.ModifierEventListener;
import ru.emerginggames.snappers.GameActivity;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.model.Blast;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 15:41
 */
public class BlastView{
    private static final int BLAST_ANIMATION_DELAY = 50;
    private static final int BLAST_CELL_FLIGHT_TIME = 300;
    private static int shift;
    public Blast blast;
    private AnimatedSprite sprite;
    private GameController controller;

    public BlastView(Blast blast, GameController controller) {
        this.controller = controller;
        this.blast = blast;
    }

    public void addToScene(E3Scene scene){
        Layer layer = scene.getTopLayer();

        sprite = new AnimatedSprite(GameActivity.Resources.blastTexture, Math.round(blast.x), Math.round(blast.y));
        sprite.animate(BLAST_ANIMATION_DELAY, 1, GameActivity.Resources.blastFrames);
        shift = sprite.getWidth()/2;
        layer.add(sprite);
        sprite.hide();
    }

    public void hide(){
        sprite.hide();
        //sprite.rotate(- getDirectionRotation());
    }

    public void show(){
        sprite.move(Math.round(blast.x - shift), Math.round(blast.y - shift));
        sprite.rotate(getDirectionRotation());
        sprite.show();
    }

    public void advance(){
        sprite.move(Math.round(blast.x - shift), Math.round(blast.y - shift));
    }
    
    private int getDirectionRotation(){
        switch (blast.direction){
            case Right:
                return 90;
            case Up:
                return 180;
            case Left:
                return -90;
            default:
                return 0;
        }
    }
}
