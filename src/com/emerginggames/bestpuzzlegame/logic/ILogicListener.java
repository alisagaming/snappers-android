package com.emerginggames.bestpuzzlegame.logic;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 23:59
 */
public interface ILogicListener {
    public void snapperHit(int i, int j);
    public void tap();
    public boolean canTap(int i, int j);
}
