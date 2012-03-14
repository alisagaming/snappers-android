package ru.emerginggames.snappers.view;

import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Shape;
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
public class BlastView implements ModifierEventListener {
    private static final int BLAST_ANIMATION_DELAY = 50;
    private static final int BLAST_CELL_FLIGHT_TIME = 500;
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
        layer.add(sprite);
        sprite.hide();
    }

    public void hide(){
        sprite.hide();
    }

    public void show(){
        sprite.show();
    }

    public void flyToNext(){
        sprite.addModifier(new ProgressModifier(
                new MoveModifier(
                        blast.x, blast.x, blast.destX,
                        blast.y, blast.y, blast.destY),
                BLAST_CELL_FLIGHT_TIME, Linear.getInstance(), this));
    }

    @Override
    public void onModifierStart(ShapeModifier modifer, Shape shape) {}

    @Override
    public void onModifierFinished(ShapeModifier modifier, Shape shape) {
        controller.blastHit(this);
    }
}
