package ru.emerginggames.snappers.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 1:14
 */
public class LevelPack implements Serializable{
    public int id;
    public String background;
    public boolean shadows;
    public String title;
    public String name;
    public String levelIcon;
    public boolean isGold;
    public boolean isPremium;
    public int levelCount;
}
