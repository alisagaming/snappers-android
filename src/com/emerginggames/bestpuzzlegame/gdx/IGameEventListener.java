package com.emerginggames.bestpuzzlegame.gdx;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.03.12
 * Time: 19:05
 */
public interface IGameEventListener {
    public void gameWon();
    public void gameLost();
    public void levelPackWon();

    public IAppGameListener getAppListener();

}
