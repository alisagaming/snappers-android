package com.emerginggames.bestpuzzlegame.gdx;

import com.emerginggames.bestpuzzlegame.model.Level;
import com.emerginggames.bestpuzzlegame.model.LevelPack;

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
    public Level getNextLevel(Level currentLevel);
    public void onInitDone();
    public void showPaused();
    public void showHintMenu();
    public void showGameLost(Level level);
    public void hideGameOverMenu();
    public void updateLevelInfo(Level level);
    public void updateTapsLeft(int n);
    public void onStageChanged(Game.Stages newStage, Game.Stages oldStage);
    public void onCloseGame();
}
