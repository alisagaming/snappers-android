package com.emerginggames.bestpuzzlegame.utils;

import com.emerginggames.bestpuzzlegame.model.Goods;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.04.12
 * Time: 3:16
 */
public interface IStoreListener {
    public void onItemBought(Goods item);
    public void onItemRefunded(Goods item);
    public void onBillingSupported(boolean supported);


}
