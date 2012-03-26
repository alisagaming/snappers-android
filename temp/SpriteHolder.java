package ru.emerginggames.snappers.view;

import android.util.SparseArray;
import com.e3roid.drawable.AnimatedSpriteCopyable;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.Resources;

public class SpriteHolder extends HideableLayer{
    public static final int SNAPPER_SHADOW = 5;
    public static final int SNAPPER = 1;
    public static final int SNAPPER_EYES = 6;
    public static final int SNAPPER_EYES_SHADOW = 7;
    public static final int SNAPPER_BANG = 8;
    public static final int BLAST = 9;

    SparseArray<Sprite> sprites = new SparseArray<Sprite>(9);

    public SpriteHolder() {
        super(false);
        visible = true;

        int snapperSizeShift = Metrics.snapperSize / 2;

        addAnimatedSprite(SNAPPER, Resources.snapperTexture, -snapperSizeShift, -snapperSizeShift);
        addSprite(SNAPPER_SHADOW, Resources.shadowSnapper, -snapperSizeShift, -snapperSizeShift);
        addAnimatedSprite(SNAPPER_EYES, Resources.eyesTexture, -snapperSizeShift, -snapperSizeShift);
        addAnimatedSprite(SNAPPER_EYES_SHADOW, Resources.eyeShadowTexture, -snapperSizeShift, -snapperSizeShift);
    }

    public AnimatedSpriteCopyable getAnimatedSprite(int id, int x, int y){
        AnimatedSpriteCopyable copy = new AnimatedSpriteCopyable((AnimatedSpriteCopyable)sprites.get(id), x, y);
        copy.setVisible(true);
        return copy;
    }

    private void addAnimatedSprite(int id, TiledTexture texture, int x, int y){
        Sprite sprite = new AnimatedSpriteCopyable(texture, x, y);
        sprite.hide();
        add(sprite);
        sprites.append(id, sprite);

    }

    private void addSprite(int id, Texture texture, int x, int y){
        Sprite sprite = new Sprite(texture, x, y);
        sprite.hide();
        add(sprite);
        sprites.append(id, sprite);
    }
}
