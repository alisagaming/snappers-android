package com.emerginggames.snappers2.gdx;

import com.emerginggames.snappers2.model.Level;
import com.emerginggames.snappers2.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 14:01
 */
public interface IAppGameListener {

    public void levelPackWon(LevelPack pack);
    public void levelSolved(Level level, int score);
    public boolean isLevelSolved(Level level);
    public boolean isSoundEnabled();
    public void gotScreenSize(int width, int height);
    public Level getNextLevel(Level currentLevel);
    public void onInitDone();
    public void showPaused();
    public void showHintMenu();
    public void showGameLost(Level level);
    public void hideGameOverMenu();
    public void updateLevelInfo(Level level);
    public void updateTapsLeft(int n);
    public void onStageChanged(Game.Stages newStage, Game.Stages oldStage);
}
