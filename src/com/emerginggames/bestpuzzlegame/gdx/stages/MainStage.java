package com.emerginggames.bestpuzzlegame.gdx.stages;

import android.graphics.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.emerginggames.bestpuzzlegame.gdx.Elements.MovableActor;
import com.emerginggames.bestpuzzlegame.gdx.Elements.SnapperView;
import com.emerginggames.bestpuzzlegame.gdx.helper.IAnimationFunction;
import com.emerginggames.bestpuzzlegame.gdx.helper.IPositionAnimationListener;
import com.emerginggames.bestpuzzlegame.gdx.helper.PowEasingAnim;
import com.emerginggames.bestpuzzlegame.model.Snappers;
import com.emerginggames.bestpuzzlegame.Metrics;
import com.emerginggames.bestpuzzlegame.logic.GameLogic;
import com.emerginggames.bestpuzzlegame.gdx.IGameEventListener;
import com.emerginggames.bestpuzzlegame.gdx.Game;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.emerginggames.bestpuzzlegame.model.Blast;
import com.emerginggames.bestpuzzlegame.logic.ILogicListener;
import com.emerginggames.bestpuzzlegame.model.Level;

import javax.microedition.khronos.opengles.GL10;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 2:18
 */
public class MainStage extends MyStage {
    private static final float BLAST_ANIMATION_TIME = 0.1f;
    protected static final float POP_SOUND_DISTANCE = 0.1f;
    //protected static final float BANG_FRAME_DURATION = 0.12f;
    protected static final int WAIT_FOR_TUTORIAL = 5000;
    private static final float SNAPPER_WARM_TIME = 0.6f;
    private static final float TIME_TO_WAIT_FOR_WON = 1;

    private final GameLogic logic;

    IGameEventListener mGame;
    protected volatile boolean gameOverFired;

    protected Hints hint;
    public boolean isHinting = false;
    boolean isTutorialAvailable = false;
    boolean drawButtons = true;

    float animDelta;
    //private Bangs bangs;
    private Blasts blasts;
    private Snappers_ snappers;
    private Sounds sounds;
    public int marginBottom;
    public int maxMarginBottom;
    float timeToFireWon = TIME_TO_WAIT_FOR_WON;

    public MainStage(int width, int height, IGameEventListener listener) {
        super(width, height, true);
        logic = new GameLogic(logicListener);
        this.mGame = listener;

        snappers = new Snappers_();
        //bangs = new Bangs();
        blasts = new Blasts();
        sounds = new Sounds();

        if (width != 0)
            setViewport(width, height);
    }

    public void setLevel(Level level){
        gameOverFired = false;
        timeToFireWon = TIME_TO_WAIT_FOR_WON;
        logic.startLevel(level);

        if (width != 0)
            snappers.defineSnapperViews();

        mGame.getAppListener().updateLevelInfo(level);
        isHinting = false;
        logicListener.tap();
        isTutorialAvailable = (level.number < 4 && level.packNumber == 1);
    }

    public void setViewport(int width, int height) {
        super.setViewport(width, height, true);
        logic.setScreen(width, height, defineGameRect(width, height) );
        snappers.defineSnapperViews();
    }

    protected Rect defineGameRect(int width, int height){
        int marginTop = Math.round(Metrics.squareButtonSize * 1.1f);
        int marginBottom = Math.round(Metrics.squareButtonSize * 0.7f);
        int snapperAreaHeight = height - marginTop - marginBottom;
        if (snapperAreaHeight < width){
            snapperAreaHeight = width;
            marginBottom = height - marginTop - snapperAreaHeight;
            if (marginBottom<0){
                snapperAreaHeight+= marginBottom;
                marginBottom = 0;
            }
        }
        this.marginBottom = marginBottom;
        maxMarginBottom = height - marginTop - Math.min(width, (int)(Resources.eyeFrames[0].originalWidth * Metrics.snapperMult1 * 6));
        return new Rect(0, height - marginTop, width, marginBottom);
    }

    public void resizeGameRect(int marginBottom){
        int marginTop = Math.round(Metrics.squareButtonSize * 1.1f);
        Rect gameRect = new Rect(0, (int)height - marginTop, (int)width, marginBottom);
        logic.setScreen((int)width, (int)height, gameRect);
        snappers.updatePositions();
    }

    @Override
    public void act(float delta) {
        int acts = logic.advance2(delta);
        super.act(delta);

        boolean isGameOver = logic.isGameOver();
        if (isGameOver && !gameOverFired){
            if (logic.isGameLost()){
                gameOverFired = true;
                mGame.gameLost();
            }
            else{
                if (timeToFireWon > 0)
                    timeToFireWon -= delta;
                else
                    {
                        gameOverFired = true;
                        mGame.gameWon();
                    }
            }
        } else if (!isGameOver && gameOverFired){
            gameOverFired = false;
            timeToFireWon = TIME_TO_WAIT_FOR_WON;
        }

        sounds.act(delta, acts);
        //bangs.act(delta);
        snappers.act(delta);

        if (isHinting)
            hint.act(delta);

        if (isTutorialAvailable && !isHinting && !areSnappersTouched() && (System.currentTimeMillis() - logic.startTime) > WAIT_FOR_TUTORIAL)
            showHints(true);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        boolean res = super.touchUp(x, y, pointer, button);
        if (!res && isTutorialAvailable && ! isHinting && !areSnappersTouched())
            showHints(true);
        return res;
    }

    @Override
    public void draw() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (isHinting)
            hint.drawBack(batch);
        snappers.draw();
        blasts.draw();
        //bangs.draw();

        if (isHinting)
            hint.draw(batch);
        batch.end();
    }

    public void showHints(boolean showText){
        if (isHinting)
            return;

        if (areSnappersTouched())
            restartLevel();

        if (hint == null)
            hint =  new Hints(logic);
        else
            hint.updateLevel();
        logic.hintUsed = true;
        isHinting = true;
    }

    public void restartLevel(){
        setLevel(logic.level);
    }

    public void nextLevel(){
        Level next = mGame.getAppListener().getNextLevel(logic.level);

        if (next == null)
            mGame.levelPackWon();
        else
            setLevel(next);
    }

    public GameLogic getLogic(){
        return logic;
    }

    public boolean areSnappersTouched(){
        return logic.tapRemains != logic.level.tapsCount;
    }

    public void setDrawButtons(boolean drawButtons) {
        this.drawButtons = drawButtons;
    }

    private ILogicListener logicListener = new ILogicListener() {
        @Override
        public void snapperHit(int i, int j) {
            SnapperView view = snappers.find(i, j);
            view.touch();
            /*if (view.state <1){
                snappers.free(view);
                bangs.add(i, j);
            }*/
        }

        @Override
        public void tap() {
            mGame.getAppListener().updateTapsLeft(logic.tapRemains);
            if (isHinting){
                if (logic.tapRemains > 0)
                    hint.hintTapped();
                else
                    isHinting = false;
            }
        }

        @Override
        public boolean canTap(int i, int j) {
            return !isHinting || hint.isHintingSnapper(i, j);
        }
    };

/*    private class Bangs {
        private Array<AnimatedSprite> activeBangs ;
        private Pool<AnimatedSprite> bangPool;

        public Bangs(){
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

        public void act(float delta) {
            for (int i=0; i< activeBangs.size; i++)
                activeBangs.get(i).act(delta);
        }

        public void draw(){
            for (int i=0; i< activeBangs.size; i++)
                activeBangs.get(i).draw(batch);
        }

        public void add(int i, int j){
            AnimatedSprite bang = bangPool.obtain();
            bang.restartAnimation();
            bang.setPosition(logic.getSnapperXPosision(i) - bang.getWidth()/2, logic.getSnapperYPosision(j) - bang.getHeight()/2);
            activeBangs.add(bang);
        }
    }*/

    private class Blasts{
        private Animation blastAnimation;

        private Blasts() {
            blastAnimation = new Animation(BLAST_ANIMATION_TIME, Resources.blastFrames);
        }

        protected void draw(){
            Blast b;
            int blSize = Resources.blastFrames[0].originalWidth;
            int blShift = blSize/2;
            List<Blast> blasts = logic.getBlasts();
            for (int i=0; i<blasts.size(); i++){
                b = blasts.get(i);
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
    }

    private class Snappers_ implements SnapperView.SnapperFreeListener{
        final private Array<SnapperView> activeSnappers ;
        private Pool<SnapperView> snapperViewPool;
        IAnimationFunction snapperAnimFn = new PowEasingAnim(1.5f);

        public Snappers_(){
            snapperViewPool = new Pool<SnapperView>(30, 40) {
                @Override
                protected SnapperView newObject() {
                    SnapperView view = new SnapperView(logic, Snappers_.this);
                    return view;
                }
            };
            activeSnappers = new Array<SnapperView>(false, 30);
        }

        public void act(float delta){
            if (delta < 1)
                animDelta += delta;
            if (animDelta >= 0.05f){
                synchronized (activeSnappers){
                    for (int i=0; i< activeSnappers.size; i++)
                        activeSnappers.get(i).moveAct(animDelta);
                    animDelta = 0;
                }
            }
        }

        public void updatePositions(){
            synchronized (activeSnappers){
                for (int i=0; i< activeSnappers.size; i++)
                    activeSnappers.get(i).setPosition();
            }
        }

        public void defineSnapperViews(){
            if (logic.level == null)
                return;
            SnapperView view;
            int i; int j;
            int state;
            synchronized (activeSnappers){
                for (i=0; i<activeSnappers.size; i++)
                    removeActor(activeSnappers.get(i));
                snapperViewPool.free(activeSnappers);
                activeSnappers.clear();
                int w = logic.width;
                int h = logic.height;
                for (i=0; i< Snappers.WIDTH; i++)
                    for (j=0; j< Snappers.HEIGHT; j++)
                        if ((state = logic.snappers.getSnapper(i, j)) > 0){
                            view = snapperViewPool.obtain();
                            view.set(i, j, state);
                            view.setRandomStart(0, 0, w, h, SNAPPER_WARM_TIME);
                            activeSnappers.add(view);
                            addActor(view);
                            view.setListener(snapperAnimationListener);
                            view.setAnimFn(snapperAnimFn);

                        }
            }
        }

        public void draw(){
            int i;
            Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

            synchronized (activeSnappers){
                for (i=0; i< activeSnappers.size; i++)
                    activeSnappers.get(i).draw(batch);
            }
        }

        public SnapperView find(int i, int j){
            SnapperView v;
            synchronized (activeSnappers){
                for (int k=0; k< activeSnappers.size; k++){
                    v = activeSnappers.get(k);
                    if (v.i == i && v.j == j)
                        return v;
                }
            }
            return null;
        }

        public void free(SnapperView view){
            synchronized (activeSnappers){
                activeSnappers.removeValue(view, true);
                snapperViewPool.free(view);
            }
        }

        IPositionAnimationListener snapperAnimationListener = new IPositionAnimationListener() {
            @Override
            public void onAnimationEnd(MovableActor item) {
                float time = (float)Math.random() * 2f + 1f;
                ((SnapperView)item).shiftRandom(time);
            }
        };

        @Override
        public void onSnapperFree(SnapperView view) {
            free(view);
        }
    }

    private class Sounds{
        protected float sincePopped;
        protected int toPop;

        public void act(float delta, int acts){
            sincePopped+=delta;
            addPopSound(acts);
            playPopSound();
        }

        protected void playPopSound(){
            if (sincePopped < POP_SOUND_DISTANCE)
                return;
            sincePopped = 0;
            if (toPop<1 )
                return;

            toPop--;

            if (Game.isSoundEnabled)
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

        public <T> T getRandomValue(T[] arr){
            return arr[(int)(Math.random()*arr.length)];
        }

    }
}
