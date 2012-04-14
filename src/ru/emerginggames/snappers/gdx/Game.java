package ru.emerginggames.snappers.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.gdx.Elements.ColorRect;
import ru.emerginggames.snappers.gdx.stages.*;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:25
 */
public class Game implements ApplicationListener, IGameEventListener {
    int width;
    int height;

    private SpriteBatch batch;

    protected Level level;
    protected MyStage currentStage;
    protected MainStage mainStage;
    protected GameOverStage gameOverStage;
    protected PausedStage pausedStage;
    protected HintMenuStage hintMenu;
    protected HelpStage helpStage;
    protected LevelPack levelPack;
    protected Sprite bg;
    FPSLogger logger;
    protected ColorRect tempRect;

    
    protected boolean objectsCreated = false;
    public static boolean isSoundEnabled;
    public boolean initDone = false;

    @Override
    public void create() {
        Resources.loadSounds();
        isSoundEnabled = ((IAppGameListener)Gdx.app).isSoundEnabled();
    }

    protected void createObjects(){
        if (objectsCreated)
            return;
        Resources.loadTextures(levelPack.isGold);

        batch = new SpriteBatch();
        mainStage = new MainStage(width, height, this);
        gameOverStage = new GameOverStage(width,height,this, mainStage.getLogic(), batch);
        pausedStage = new PausedStage(width, height, this, batch);
        hintMenu = new HintMenuStage(width, height, this, batch);
        setStage(mainStage);

        if (level != null)
            mainStage.setLevel(level);

        objectsCreated = true;
        if (Resources.loadBg(levelPack.background))
            bg = new Sprite(Resources.bg);

        logger = new FPSLogger();

        tempRect = new ColorRect(50, 50, 100, 100);
        tempRect.setColor(0,0,0,0.5f);
    }

    @Override
    public void resize(int i, int i1) {
        width = i;
        height = i1;

        ((IAppGameListener)Gdx.app).gotScreenSize(width, height);

        createObjects();

        if (bg != null)
            bg.setSize(width, height);
        initDone = true;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0,1,1,1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        if (bg != null){
            batch.begin();
            batch.disableBlending();
            bg.draw(batch);
            batch.enableBlending();
            batch.end();
        }

        float delta = Gdx.graphics.getDeltaTime();
        /*if (delta > 1)
            delta = 0.01f;*/
        if (currentStage != pausedStage)
            mainStage.act(delta);
        if (currentStage != helpStage)
            mainStage.draw();

        if (currentStage != mainStage){
            currentStage.act(delta);
            currentStage.draw();
        }

        batch.begin();

        Resources.fnt1.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 0, Resources.fnt1.getLineHeight());
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        Texture.invalidateAllTextures(Gdx.app);
        if (currentStage == hintMenu)
            hintMenu.onShow();
    }

    @Override
    public void dispose() {
        mainStage.dispose();
        gameOverStage.dispose();
        pausedStage.dispose();
        hintMenu.dispose();
        if (helpStage != null)
            helpStage.dispose();
    }

    public void setStartLevel(Level level){
        this.level = level;
        levelPack = level.pack;
    }

    @Override
    public void onShopBtn() {
        ((IAppGameListener)Gdx.app).launchStore();
    }

    @Override
    public void onNextBtn() {
        mainStage.nextLevel();
        setStage(mainStage);
    }

    @Override
    public void onRestartBtn() {
        mainStage.restartLevel();
        setStage(mainStage);
    }

    @Override
    public void onResumeBtn() {
        setStage(mainStage);
    }

    @Override
    public void onMenuBtn() {
        Gdx.app.exit();
    }

    @Override
    public void onHintBtn() {
        Level level = mainStage.getLogic().level;
        if (level.number < 4 && level.packNumber == 1){
            mainStage.showHints(true);
            return;
        }
        setStage(hintMenu);
    }

    @Override
    public void useHint() {
        setStage(mainStage);
        ((IAppGameListener)Gdx.app).useHint();
        mainStage.showHints(false);
    }

    @Override
    public void gameWon() {
        ((IAppGameListener)Gdx.app).levelSolved(mainStage.getLogic().level);
        setStage(gameOverStage);
        gameOverStage.show(true, ((IAppGameListener) Gdx.app).getAdHeight());
        if (isSoundEnabled)
            Resources.winSound.play(0.6f);
    }

    @Override
    public void gameLost() {
        setStage(gameOverStage);
        gameOverStage.show(false, ((IAppGameListener) Gdx.app).getAdHeight());

    }

    @Override
    public void onPauseBtn() {
        setStage(pausedStage);
    }

    @Override
    public void onHelp() {
        if (helpStage == null)
            helpStage = new HelpStage(width, height, batch, this);
        setStage(helpStage);
    }

    @Override
    public void levelPackWon() {
        ((IAppGameListener)Gdx.app).levelPackWon(levelPack);
    }

    @Override
    public void onHelpDone() {
        setStage(gameOverStage);
    }

    public Level getLevel(){
        return mainStage.getLogic().level;
    }

    public LevelPack getLevelPack(){
        return levelPack;
    }

    private void setStage(MyStage stage){
        if (stage == currentStage)
            return;

        if (stage == gameOverStage)
            ((IAppGameListener)Gdx.app).showAd();
        else if (currentStage == gameOverStage)
            ((IAppGameListener)Gdx.app).hideAd();

        if (stage == mainStage)
            mainStage.setDrawButtons(true);
        else if (currentStage == mainStage && stage != hintMenu)
            mainStage.setDrawButtons(false);

        if (currentStage != null)
            currentStage.onHide();
        Gdx.input.setInputProcessor(currentStage = stage);
        stage.onShow();
    }

    public void setAdHeight(int height){
        if (currentStage == gameOverStage)
            gameOverStage.setAdHeight(height);
    }

    public void backButtonPressed(){
        if (currentStage == gameOverStage)
            Gdx.app.exit();
        else if (currentStage == helpStage)
            setStage(gameOverStage);
        else if (currentStage == pausedStage)
            Gdx.app.exit();
        else if (currentStage == hintMenu)
            setStage(mainStage);
        else if (currentStage == mainStage)
            setStage(pausedStage);
    }


}
