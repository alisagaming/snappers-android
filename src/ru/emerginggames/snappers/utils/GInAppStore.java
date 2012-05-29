package ru.emerginggames.snappers.utils;

import android.content.Context;
import ru.emerginggames.snappers.Consts;
import ru.emerginggames.snappers.UserPreferences;
import ru.emerginggames.snappers.model.Goods;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 17.04.12
 * Time: 2:08
 */
public class GInAppStore extends Store {

    private static final String ID_ONE_HINT = "onehintpack";
    private static final String ID_TEN_HINTS = "tenhintspack";
    private static final String ID_HINTS_3 = "hintspack3";
    private static final String ID_HINTS_25 = "hintspack25";
    private static final String ID_HINTS_75 = "hintspack75";
    private static final String ID_AD_FREE = "adfree";
    private static final String ID_PREMIUM_PACK1 = "premiumpack1";
    private static final String ID_PREMIUM_PACK2 = "premiumpack2";
    private static final String ID_PREMIUM_PACK3 = "premiumpack3";
    private static final String ID_PREMIUM_PACK4 = "premiumpack4";

    BillingService mBillingService;
    GoogleInAppStoreObserver mObserver;

    public static Store getInstance(Context context){
        if (instance == null)
            instance = new GInAppStore(context);
        return instance;
    }

    public GInAppStore(Context context) {
        super(context);
        mBillingService = new BillingService();
        mBillingService.setContext(context);
        mObserver = new GoogleInAppStoreObserver();
        ResponseHandler.register(mObserver);
        if (!mBillingService.checkBillingSupported())
            isBillingSupported = BillingSupported.NO;
    }

    @Override
    public boolean buy(Goods item) {
        return mBillingService.requestPurchase(goodsToId(item), null);
    }

    public void stopObserving() {
        if (mObserver != null)
            ResponseHandler.unregister(mObserver);
    }

    Goods idToGoods(String itemId) {
        if (ID_ONE_HINT.equals(itemId))
            return Goods.HintPack1;
        else if (ID_TEN_HINTS.equals(itemId))
            return Goods.HintPack10;
        else if (ID_HINTS_3.equals(itemId))
            return Goods.HintPack3;
        else if (ID_HINTS_25.equals(itemId))
            return Goods.HintPack25;
        else if (ID_HINTS_75.equals(itemId))
            return Goods.HintPack75;
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
            case HintPack3:
                return ID_HINTS_3;
            case HintPack25:
                return ID_HINTS_25;
            case HintPack75:
                return ID_HINTS_75;
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

    private void restoreDatabase() {
        if (!UserPreferences.getInstance(context).isGInAppInitDone())
            mBillingService.restoreTransactions();
    }

    private class GoogleInAppStoreObserver extends PurchaseObserver {
        private GoogleInAppStoreObserver() {
            super(context);
        }

        @Override
        public void onBillingSupported(final boolean supported) {
            isBillingSupported = supported ? BillingSupported.YES : BillingSupported.NO;
            if (supported)
                restoreDatabase();
            runOnBillingSupported.postMe(mHandler, mStoreListener, supported);
        }

        @Override
        public void onPurchaseStateChange(Consts.PurchaseState purchaseState, String itemId, int totalQuantity, long purchaseTime, String developerPayload) {
            final Goods item = idToGoods(itemId);
            if (purchaseState == Consts.PurchaseState.PURCHASED)
                itemBought(item, 1);
             else if (purchaseState == Consts.PurchaseState.REFUNDED)
                itemReturned(item, 1);
        }

        @Override
        public void onRequestPurchaseResponse(BillingService.RequestPurchase request, Consts.ResponseCode responseCode) {
            //TODO: do ?;
        }

        @Override
        public void onRestoreTransactionsResponse(BillingService.RestoreTransactions request, Consts.ResponseCode responseCode) {
            UserPreferences.getInstance(context).setGInAppInitDone(true);
        }
    }


}
