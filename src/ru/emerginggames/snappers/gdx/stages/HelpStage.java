package ru.emerginggames.snappers.gdx.stages;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.emerginggames.snappers.gdx.IGameEventListener;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 09.04.12
 * Time: 9:10
 */
public class HelpStage extends MyStage {
    IGameEventListener listener;

    Sprite helpSprite;

    public HelpStage(float width, float height, SpriteBatch batch, IGameEventListener listener) {
        super(width, height, true, batch);
        helpSprite = new Sprite(Resources.getHelpTexture());

        float aspect = helpSprite.getHeight() / helpSprite.getWidth();
        int newHeight = Math.round(width * aspect);
        helpSprite.setBounds(0, height - newHeight, width, newHeight);
        this.listener = listener;
    }

    @Override
    public void draw() {
        batch.begin();
        helpSprite.draw(batch);
        batch.end();
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        Resources.buttonSound.play();
        listener.onHelpDone();
        return true;
    }
}
