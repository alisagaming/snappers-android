package ru.emerginggames.snappers.gdx;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.SnapperView;
import ru.emerginggames.snappers.gdx.android.OutlinedTextSprite;
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
    private static final float BLAST_ANIMATION_TIME = 0.08f;
    private Animation blastAnimation;
    private final GameLogic logic;

    private Array<SnapperView> activeSnappers ;
    private Pool<SnapperView> snapperViewPool;
    OutlinedTextSprite levelText;
    OutlinedTextSprite tapLeftText;
    IGameEventListener listener;
    protected MainButtons buttons;
    protected boolean gameOverFired;

    public MainStage(float width, float height, IGameEventListener listener) {
        super(width, height, true);
        logic = new GameLogic(this);
        this.listener = listener;

        snapperViewPool = new Pool<SnapperView>(30, 40) {
            @Override
            protected SnapperView newObject() {
                return new SnapperView(logic);
            }
        };

        activeSnappers = new Array<SnapperView>(false, 30);
        levelText = new OutlinedTextSprite("", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        tapLeftText = new OutlinedTextSprite("123", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);

        buttons = new MainButtons(listener);
        addActor(buttons);
    }
    
    public void setLevel(Level level){
        gameOverFired = false;
        logic.startLevel(level);

        if (width != 0)
            defineSnapperViews();
        levelText.setText(String.format("Level: %d-%d", level.packNumber, level.number));
        tap();
    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        logic.setScreen(width, height, defineGameRect(width, height) );
        defineSnapperViews();
        blastAnimation = new Animation(BLAST_ANIMATION_TIME, Resources.blastFrames);

        levelText.positionRelative(0, height, IPositionable.Dir.DOWNRIGHT, Metrics.screenMargin);
        tapLeftText.positionRelative(levelText, IPositionable.Dir.DOWN, 0);
        buttons.setViewport(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        levelText.dispose();
        tapLeftText.dispose();
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

        return new Rect(0, height - marginTop, width, marginBottom);
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
    public void act(float delta) {
        logic.advance2(delta);
        super.act(delta);

        if (logic.isGameOver() && !gameOverFired){
            gameOverFired = true;
            if (logic.isGameLost())
                listener.gameLost();
            else
                listener.gameWon();
        }
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawSnappers();
        drawBlasts();
        levelText.draw(batch);
        tapLeftText.draw(batch);
        buttons.draw(batch, 1);
        batch.end();
    }

    protected void drawSnappers(){

        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).shadow.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).snapper.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).eyeShadow.draw(batch);

        for (int i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).eyes.draw(batch);
    }

    protected void drawBlasts(){
        Blast b;
        int blSize = Metrics.blastSize;
        int blShift = blSize/2;
        for (int i=0; i<logic.activeBlasts.size(); i++){
            b = logic.activeBlasts.get(i);
            batch.draw(blastAnimation.getKeyFrame(b.age, false), b.x - blShift, b.y - blShift, blShift, blShift, blSize, blSize, 1, 1, getBlastRotation(b));
        }
    }
    
    protected int getBlastRotation(Blast blast){
        switch (blast.direction){
            case Down:
                return 180;
            case Left:
                return 90;
            case Right:
                return -90;
            default:
                return 0;
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
    public void snapperHit(int i, int j) {
        SnapperView view = findView(i, j);
        view.touch();
        if (view.state <1){
            activeSnappers.removeValue(view, true);
            snapperViewPool.free(view);
        }
    }

    @Override
    public void tap() {
        tapLeftText.setText(String.format("Taps left: %d", logic.tapRemains));
    }

    public void restartLevel(){
        setLevel(logic.level);
    }

    public void nextLevel(){
        Level next = LevelTable.getNextLevel((Activity)Gdx.app, logic.level);
        if (next == null)
            listener.levelPackWon();
        else
            setLevel(next);
    }

    public GameLogic getLogic(){
        return logic;
    }

    public void resume(){
        levelText.resume();
        tapLeftText.resume();
    }


}
