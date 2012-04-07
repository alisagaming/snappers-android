package ru.emerginggames.snappers.gdx.stages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.controller.GameLogic;
import ru.emerginggames.snappers.controller.IGameEventListener;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Elements.*;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.android.OutlinedTextSprite;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;

import javax.microedition.khronos.opengles.GL10;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 2:18
 */
public class MainStage extends Stage implements ILogicListener {
    private static final float BLAST_ANIMATION_TIME = 0.1f;
    protected static final float POP_SOUND_DISTANCE = 0.1f;
    protected static final float BANG_FRAME_DURATION = 0.12f;
    public static final String LEVEL_D_D = "Level: %d-%d";
    public static final String TAPS_LEFT_D = "Taps left: %d";
    private Animation blastAnimation;
    private final GameLogic logic;

    private Array<SnapperView> activeSnappers ;
    private Pool<SnapperView> snapperViewPool;
    private Array<AnimatedSprite> activeBangs ;
    private Pool<AnimatedSprite> bangPool;

    OutlinedTextSprite levelText;
    OutlinedTextSprite tapLeftText;
    IGameEventListener listener;
    protected MainButtons buttons;
    protected boolean gameOverFired;
    protected float sincePopped;
    protected int toPop;

    protected Hints hint;
    public boolean isHinting = false;

    public MainStage(int width, int height, IGameEventListener listener) {
        super(width, height, true);
        logic = new GameLogic(this);
        this.listener = listener;

        setupSnappers();
        setupBangs();

        String str = String.format(LEVEL_D_D, 99, 999);
        levelText = new OutlinedTextSprite(str, Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        str = String.format(TAPS_LEFT_D, 99);
        tapLeftText = new OutlinedTextSprite(str, Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, 2, Resources.font);
        buttons = new MainButtons(listener);
        addActor(buttons);
        if (width != 0)
            setViewport(width, height);
    }

    public void setLevel(Level level){
        gameOverFired = false;
        logic.startLevel(level);

        if (width != 0)
            defineSnapperViews();
        levelText.setText(String.format(LEVEL_D_D, level.packNumber, level.number));
        tap();
    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        logic.setScreen(width, height, defineGameRect(width, height) );
        defineSnapperViews();
        blastAnimation = new Animation(BLAST_ANIMATION_TIME, Resources.blastFrames);

        levelText.positionRelative(0, height, IPositionable.Dir.DOWNRIGHT, Metrics.screenMargin);
        tapLeftText.positionRelative(0, levelText.getY(), IPositionable.Dir.DOWNRIGHT, Metrics.screenMargin);
        buttons.setViewport(width, height);
    }

    protected Rect defineGameRect(int width, int height){
        int marginTop = Math.round(Metrics.squareButtonSize * 1.1f);
        int marginBottom = Math.round(Metrics.squareButtonSize * 0.7f);
        int snapperAreaHeight = height - marginTop - marginBottom;
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

        for (i=0; i<activeSnappers.size; i++)
            removeActor(activeSnappers.get(i));
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
        int acts = logic.advance2(delta);
        super.act(delta);

        if (logic.isGameOver() && !gameOverFired){
            gameOverFired = true;
            if (logic.isGameLost())
                listener.gameLost();
            else
                listener.gameWon();
        }
        sincePopped+=delta;
        addPopSound(acts);
        playPopSound();

        for (int i=0; i< activeBangs.size; i++)
            activeBangs.get(i).act(delta);

        if (isHinting)
            hint.act(delta);
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawSnappers();
        drawBlasts();

        for (int i=0; i< activeBangs.size; i++)
            activeBangs.get(i).draw(batch);

        levelText.draw(batch);
        tapLeftText.draw(batch);
        buttons.draw(batch, 1);
        if (isHinting)
            hint.draw(batch);
        batch.end();
    }

    protected void drawSnappers(){
        int i;
        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

        for (i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).snapper.draw(batch);

        for (i=0; i< activeSnappers.size; i++)
            activeSnappers.get(i).eyes.draw(batch);
    }

    protected void drawBlasts(){
        Blast b;
        int blSize = Metrics.blastSize;
        int blShift = blSize/2;
        List<Blast> blasts = logic.activeBlasts;
        for (int i=0; i<blasts.size(); i++){
            b = blasts.get(i);
            batch.draw(blastAnimation.getKeyFrame(b.age, false), b.x - blShift, b.y - blShift, blShift, blShift, blSize, blSize, 1, 1, getBlastRotation(b));
        }
    }
    
    protected static int getBlastRotation(Blast blast){
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
    
    protected void addBang(int i, int j){
        AnimatedSprite bang = bangPool.obtain();
        bang.restartAnimation();
        bang.setPosition(logic.getSnapperXPosision(i) - bang.getWidth()/2, logic.getSnapperYPosision(j) - bang.getHeight()/2);
        activeBangs.add(bang);
    }

    @Override
    public void snapperHit(int i, int j) {
        SnapperView view = findView(i, j);
        view.touch();
        if (view.state <1){
            activeSnappers.removeValue(view, true);
            snapperViewPool.free(view);
            addBang(i, j);
        }
    }

    @Override
    public void tap() {
        tapLeftText.setText(String.format(TAPS_LEFT_D, logic.tapRemains));
        if (isHinting){
            if (logic.tapRemains > 0)
                hint.updateHint();
            else
                isHinting = false;
        }
    }

    public void showHints(boolean showText){
        if (isHinting)
            return;

        if (hint == null)
            hint = new Hints(logic, showText);
        else
            hint.updateLevel(showText);
        logic.hintUsed = true;
        isHinting = true;
    }

    public void restartLevel(){
        setLevel(logic.level);
        isHinting = false;
    }

    public void nextLevel(){
        Level next = LevelTable.getNextLevel((Context) Gdx.app, logic.level);
        if (next == null)
            listener.levelPackWon();
        else
            setLevel(next);
        isHinting = false;
    }

    public GameLogic getLogic(){
        return logic;
    }

    protected void playPopSound(){
        if (sincePopped < POP_SOUND_DISTANCE)
            return;
        sincePopped = 0;
        if (toPop<1 )
            return;

        toPop--;

        getRandomValue(Resources.popSounds).play();
        sincePopped = 0;
    }

    protected void addPopSound(int acts){
        if (toPop<0)
            toPop = 0;
        if (acts == 0)
            return;
        if (acts >2)
            acts = 2;


        if (toPop < 4)
            toPop+= acts;
    }
    
    public static <T> T getRandomValue(T[] arr){
        return arr[(int)(Math.random()*arr.length)];
    }

    private void setupSnappers(){
        snapperViewPool = new Pool<SnapperView>(30, 40) {
            @Override
            protected SnapperView newObject() {
                SnapperView view = new SnapperView(logic);
                return view;
            }
        };
        activeSnappers = new Array<SnapperView>(false, 30);
    }

    private void setupBangs(){
        final IAnimationListener bangListener = new IAnimationListener() {
            @Override
            public void onAnimationEnd(AnimatedSprite sprite) {
                activeBangs.removeValue(sprite, true);
                bangPool.free(sprite);
            }
        };


        bangPool = new Pool<AnimatedSprite>(10, 30){
            @Override
            protected AnimatedSprite newObject() {
                return new AnimatedSprite(Resources.bangFrames, BANG_FRAME_DURATION, bangListener);
            }
        };

        activeBangs = new Array<AnimatedSprite>(30);
    }

    public boolean areSnappersTouched(){
        return logic.tapRemains != logic.level.tapsCount;
    }
}
