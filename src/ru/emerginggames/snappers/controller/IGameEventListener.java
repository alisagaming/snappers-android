package ru.emerginggames.snappers.controller;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.03.12
 * Time: 19:05
 */
public interface IGameEventListener {

    public void onShopBtn();
    public void onNextBtn();
    public void onRestartBtn();
    public void onResumeBtn();
    public void onMenuBtn();
    public void onHintBtn();
    public void onPauseBtn();
    public void gameWon();
    public void gameLost();
    public void levelPackWon();
}
