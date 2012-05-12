package ru.emerginggames.snappers.gdx;

import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 14:01
 */
public interface IAppGameListener {

    public void launchStore();
    public void levelPackWon(LevelPack pack);
    public void useHint();
    public void buy(Goods goods);
    public void levelSolved(Level level);
    public boolean isLevelSolved(Level level);
    public boolean isSoundEnabled();
    public void addScore(int score);
    public void gotScreenSize(int width, int height);
    public Level getNextLevel(Level currentLevel);
    public void onInitDone();
    public void showPaused();
    public void showHintMenu();
    public void showLostMenu();
    public void hideGameOverMenu();
}
