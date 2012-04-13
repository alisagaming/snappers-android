package ru.emerginggames.snappers.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 20:06
 */
public class Level implements Serializable{
    public int id;
    public int number;
    public int complexity;
    public String zappers;
    public String solutions;
    public int tapsCount;
    public int packNumber;
    public LevelPack pack;
}
