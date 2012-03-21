package ru.emerginggames.snappers.view;

import com.e3roid.drawable.Layer;
import com.e3roid.drawable.sprite.AnimatedSprite;
import ru.emerginggames.snappers.Resources;
import ru.emerginggames.snappers.controller.GameController;
import ru.emerginggames.snappers.model.Blast;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 15:41
 */
public class BlastView{
    private static final int BLAST_ANIMATION_DELAY = 50;
    private static int shift;
    public Blast blast;
    private AnimatedSprite sprite;
    private GameController controller;

    public BlastView(Blast blast, GameController controller) {
        this.controller = controller;
        this.blast = blast;
    }

    public void addToLayer(Layer layer){

        sprite = new AnimatedSprite(Resources.blastTexture, Math.round(blast.x), Math.round(blast.y));
        sprite.animate(BLAST_ANIMATION_DELAY, 1, Resources.blastFrames);
        shift = sprite.getWidth()/2;
        layer.add(sprite);
        sprite.hide();
    }

    public void hide(){
        sprite.hide();
    }

    public void removeFromLayer(Layer layer){
        layer.remove(sprite);
    }

    public void show(){
        sprite.move(Math.round(blast.x - shift), Math.round(blast.y - shift));
        sprite.rotate(getDirectionRotation());
        sprite.scale(1,1);
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
