package ru.emerginggames.snappers.gdx;

import android.graphics.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 2:18
 */
public class MainStage extends Stage implements ILogicListener {
    private static final float BLAST_ANIMATION_TIME = 0.3f;
    private Animation blastAnimation;
    private final GameLogic logic;

    private Array<SnapperView> activeSnappers ;
    private Array<SnapperView> snappersToRemove;
    private Pool<SnapperView> snapperViewPool;

    public MainStage(float width, float height) {
        super(width, height, true);
        logic = new GameLogic(this);

        snapperViewPool = new Pool<SnapperView>(30, 40) {
            @Override
            protected SnapperView newObject() {
                return new SnapperView(logic);
            }
        };

        activeSnappers = new Array<SnapperView>(false, 30);
        snappersToRemove = new Array<SnapperView>(false, 10);


    }
    
    public void setLevel(Level level){
        logic.startLevel(level);

        if (width != 0)
            defineSnapperViews();

    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        logic.setScreen(width, height, defineGameRect(width, height) );
        defineSnapperViews();
        blastAnimation = new Animation(BLAST_ANIMATION_TIME, Resources.blastFrames);
    }

    protected Rect defineGameRect(int width, int height){
        int marginTop = Math.round(Metrics.squareButtonSize * 1.1f);
        int snapperAreaHeight = height - 2 * marginTop;
        int marginBottom = Math.round(Metrics.squareButtonSize * 0.7f);
        if (snapperAreaHeight < width){
            snapperAreaHeight = width;
            marginBottom = height - marginTop - marginBottom;
            if (marginBottom<0){
                snapperAreaHeight+= marginBottom;
                marginBottom = 0;
            }
        }

        return new Rect(0, height - marginBottom, width, marginTop);
    }

    protected void defineSnapperViews(){
        SnapperView view;
        int i; int j;
        int state;

        snapperViewPool.free(activeSnappers);
        activeSnappers.clear();

        for (i=0; i< Snappers.WIDTH; i++)
            for (j=0; j< Snappers.HEIGHT; j++)
                if ((state = logic.snappers.getSnapper(i, j)) > 0){
                    view = snapperViewPool.obtain();
                    view.set(i, j, state);
                    activeSnappers.add(view);
                    addActor(view);
                }
    }

    @Override
    public void draw() {
        logic.advance2(Gdx.graphics.getDeltaTime());
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawSnappers();
        drawBlasts();
        batch.end();
    }

    protected void drawSnappers(){

        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).shadow.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).eyeShadow.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).snapper.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).eyes.draw(batch);
    }

    protected void drawBlasts(){
        Blast b;
        for (int i=0; i<logic.activeBlasts.size(); i++){
            b = logic.activeBlasts.get(i);
            batch.draw(blastAnimation.getKeyFrame(b.age, false), b.x, b.y);
        }
    }
    
    protected SnapperView findView(int i, int j){
        SnapperView v;
        for (int k=0; k< activeSnappers.size; k++){
            v = activeSnappers.get(k);
            if (v.i == i && v.j == j)
                return v;
        }
        return null;
    }

    @Override
    public void snapperTouched(int i, int j) {
        SnapperView view = findView(i, j);
        view.touch();
        if (view.state <1){
            activeSnappers.removeValue(view, true);
            snapperViewPool.free(view);
        }
    }
}
