package ru.emerginggames.snappers.gdx;

import android.content.Intent;
import ru.emerginggames.snappers.GoodsToShop;
import ru.emerginggames.snappers.StoreActivity;
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
    public int getHintsLeft();
    public void useHint();
    public void buy(GoodsToShop.Goods goods);
    public boolean isOnline();
    public void levelSolved(Level level);
    public boolean isLevelSolved(Level level);
    public void addScore(int score);
}
