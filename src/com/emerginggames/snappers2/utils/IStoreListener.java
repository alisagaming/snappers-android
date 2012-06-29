package com.emerginggames.snappers2.utils;

import com.emerginggames.snappers2.model.Goods;

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
