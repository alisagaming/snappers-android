package com.emerginggames.snappers.utils;

import android.content.Context;
import com.amazon.inapp.purchasing.BasePurchasingObserver;
import com.amazon.inapp.purchasing.PurchaseResponse;
import com.amazon.inapp.purchasing.PurchasingManager;
import com.emerginggames.snappers.model.Goods;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 25.07.12
 * Time: 3:27
 * To change this template use File | Settings | File Templates.
 */
public class AmazonStore extends Store {

    private static final String ID_ONE_HINT = "com.emerginggames.snappers.onehintpack";
    private static final String ID_TEN_HINTS = "com.emerginggames.snappers.tenhintspack";
    private static final String ID_AD_FREE = "com.emerginggames.snappers.adfree";
    private static final String ID_PREMIUM_PACK1 = "com.emerginggames.snappers.premiumpack1";
    private static final String ID_PREMIUM_PACK2 = "com.emerginggames.snappers.premiumpack2";
    private static final String ID_PREMIUM_PACK3 = "com.emerginggames.snappers.premiumpack3";
    private static final String ID_PREMIUM_PACK4 = "com.emerginggames.snappers.premiumpack4";

    AmazonPurchasingObserver amazonPurchasingObserver;

    private static AmazonStore instance;

    public static AmazonStore getInstance(Context context){
        if (instance == null)
            instance = new AmazonStore(context);
        return instance;
    }

    public AmazonStore(Context context) {
        super(context);
        init();
    }

    public void init() {
        amazonPurchasingObserver = new AmazonPurchasingObserver(context.getApplicationContext());

        PurchasingManager.registerObserver(amazonPurchasingObserver);
    }

    @Override
    public boolean buy(Goods item) {
        String sku = goodsToId(item);
        PurchasingManager.initiatePurchaseRequest(sku);
        return true;
    }

    private class AmazonPurchasingObserver extends BasePurchasingObserver {

        public AmazonPurchasingObserver(Context context) {
            super(context);
        }

        public void onPurchaseResponse(PurchaseResponse purchaseResponse) {
            System.out.println("onPurchaseResponse" + purchaseResponse);
            if (purchaseResponse.getPurchaseRequestStatus() == PurchaseResponse.PurchaseRequestStatus.SUCCESSFUL ||
                    purchaseResponse.getPurchaseRequestStatus() == PurchaseResponse.PurchaseRequestStatus.ALREADY_ENTITLED) {
                Goods item = idToGoods(purchaseResponse.getReceipt().getSku());
                itemBought(item, 1);
            }
        }
    }

    Goods idToGoods(String itemId) {
        if (ID_ONE_HINT.equals(itemId))
            return Goods.HintPack1;
        else if (ID_TEN_HINTS.equals(itemId))
            return Goods.HintPack10;
        else if (ID_AD_FREE.equals(itemId))
            return Goods.AdFree;
        else if (ID_PREMIUM_PACK1.equals(itemId))
            return Goods.PremiumLevelPack1;
        else if (ID_PREMIUM_PACK2.equals(itemId))
            return Goods.PremiumLevelPack2;
        else if (ID_PREMIUM_PACK3.equals(itemId))
            return Goods.PremiumLevelPack3;
        else if (ID_PREMIUM_PACK4.equals(itemId))
            return Goods.PremiumLevelPack4;

        throw new RuntimeException("Item id not found: " + itemId);
    }

    String goodsToId(Goods item) {
        switch (item) {
            case AdFree:
                return ID_AD_FREE;
            case HintPack1:
                return ID_ONE_HINT;
            case HintPack10:
                return ID_TEN_HINTS;
            case PremiumLevelPack1:
                return ID_PREMIUM_PACK1;
            case PremiumLevelPack2:
                return ID_PREMIUM_PACK2;
            case PremiumLevelPack3:
                return ID_PREMIUM_PACK3;
            case PremiumLevelPack4:
                return ID_PREMIUM_PACK4;
        }
        throw new RuntimeException("Goods item not found: " + item.toString());
    }
}
