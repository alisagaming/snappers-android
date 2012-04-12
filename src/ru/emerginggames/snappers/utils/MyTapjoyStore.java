package ru.emerginggames.snappers.utils;

import android.content.Context;
import android.util.Log;
import com.tapjoy.TJCVirtualGoods;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.VGStoreItem;
import ru.emerginggames.snappers.UserPreferences;
import ru.emerginggames.snappers.model.Goods;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.04.12
 * Time: 13:32
 */
public class MyTapjoyStore implements TJCVirtualGoods.TapjoyDownloadListener {
    Context context;
    UserPreferences prefs;
    private static final String HINTS_1_TYPE = "hints.1";
    private static final String HINTS_10_TYPE = "hints.10";

    public MyTapjoyStore(Context context, UserPreferences prefs) {
        //this.context = context;
        //this.prefs = prefs != null? prefs : UserPreferences.getInstance(context);


        //processInitialItems();

    }

    @Override
    public void onDownLoad(VGStoreItem downloadItem) {}

    @Override
    public void onDownloadedItems(ArrayList<VGStoreItem> purchasedItems) {
/*        if (this.prefs.isTapjoyInitComplete())
            return;
        for (int i=0; i< purchasedItems.size(); i++){
            VGStoreItem purchasedItem = purchasedItems.get(i);
            Goods item = getItemByType(purchasedItem.getVgStoreItemTypeName());

            if (UserPreferences.getInstance(context).getTapjoyItemBought(item) == 0){
                int amount = purchasedItem.getNumberOwned();
                if (!item.isPermanent() || purchaseItem(item, amount))
                    UserPreferences.getInstance(context).setTapjoyItemBought(item, amount);
            }
        }*/
    }

    private static Goods getItemByType(String type) {
        if (HINTS_1_TYPE.equals(type))
            return Goods.HintPack1;
        else if (HINTS_10_TYPE.equals(type))
            return Goods.HintPack10;
        return null;
    }

    private boolean purchaseItem(Goods item, int amount){
        for (int i=0; i< amount; i++)
            Store.getInstance(context).itemBought(item);
        return true;
    }

    public void updatePurchasedItems(){
/*        ArrayList<VGStoreItem> purchasedItems = TapjoyConnect.getTapjoyConnectInstance().getPurchasedItems();

        UserPreferences prefs = UserPreferences.getInstance(context);
        for (int i = 0; i < purchasedItems.size(); i++) {
            VGStoreItem item = purchasedItems.get(i);
            Goods storeItem = getItemByType(item.getVgStoreItemTypeName());
            int alreadyHave = prefs.getTapjoyItemBought(storeItem);
            int amountNew = item.getNumberOwned() - alreadyHave;
            if (amountNew > 0){
                if (purchaseItem(storeItem, amountNew))
                    prefs.setTapjoyItemBought(storeItem, item.getNumberOwned());
            }
        }*/
    }

    /*public void processInitialItems(){
        if (this.prefs.isTapjoyInitComplete())
            return;
        TapjoyConnect.getTapjoyConnectInstance().checkForVirtualGoods(this);
    }*/


}
