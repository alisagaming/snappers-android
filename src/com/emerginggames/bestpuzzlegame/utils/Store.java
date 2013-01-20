package com.emerginggames.bestpuzzlegame.utils;

import android.content.Context;
import android.os.Handler;
import com.emerginggames.bestpuzzlegame.UserPreferences;
import com.emerginggames.bestpuzzlegame.data.LevelPackTable;
import com.emerginggames.bestpuzzlegame.model.Goods;
import com.emerginggames.bestpuzzlegame.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.04.12
 * Time: 12:34
 */
public abstract class Store {
    public static enum BillingSupported{YES, NO, UNKNOWN}
    protected static Store instance;
    protected Context context;
    protected BillingSupported isBillingSupported = BillingSupported.UNKNOWN;
    protected IStoreListener mStoreListener;
    protected Handler mHandler;


    public Store(Context context) {
        this.context = context;
    }

    public abstract boolean buy(Goods item);

    public void setListener(IStoreListener itemBoughtListener, Handler uiLoopHandler) {
        this.mStoreListener = itemBoughtListener;
        mHandler = uiLoopHandler;
        if (isBillingSupported != BillingSupported.UNKNOWN)
            itemBoughtListener.onBillingSupported(isBillingSupported == BillingSupported.YES);
    }

    public void unSetListener(){
        mHandler = null;
        mStoreListener = null;
    }

    public BillingSupported isAvailable() {
        return isBillingSupported;
    }

    public boolean itemBought(Goods item, int amount){
        switch (item.getType()){
            case adFree:
                if (item == Goods.AdFree)
                    UserPreferences.getInstance(context).setAdFree(true);
                else
                    throw new RuntimeException("cant process item: " + item);
                break;
            case Hint:
                switch (item){
                    case HintPack1:
                        UserPreferences.getInstance(context).addHints(amount);
                        break;
                    case HintPack10:
                        UserPreferences.getInstance(context).addHints(10 * amount);
                        break;
                    case HintPack3:
                        UserPreferences.getInstance(context).addHints(3 * amount);
                        break;
                    case HintPack25:
                        UserPreferences.getInstance(context).addHints(25 * amount);
                        break;
                    case HintPack75:
                        UserPreferences.getInstance(context).addHints(75 * amount);
                        break;
                    default:
                        throw new RuntimeException("cant process item: " + item);
                }
                break;
            case LevelPack:
                String name = item.getLevelPackName();
                if (name == null)
                    throw new RuntimeException("can't find level pack name by Item: " + item.toString());
                LevelPack pack = LevelPackTable.get(name, context);
                if (pack == null)
                    throw new RuntimeException("can't find level pack name name: " + name);
                if (UserPreferences.getInstance(context).isPackUnlocked(pack))
                    return false;
                UserPreferences.getInstance(context).unlockLevelPack(pack);
                break;
            default:
                throw new RuntimeException("cant process item: " + item);
        }
        runOnItemBought.postMe(mHandler, mStoreListener, item);
        return true;
    }

    public boolean itemReturned(Goods item, int amount){
        switch (item.getType()){
            case adFree:
                if (item == Goods.AdFree)
                    UserPreferences.getInstance(context).setAdFree(false);
                else
                    throw new RuntimeException("cant process item: " + item);
                break;
            case Hint:
                switch (item){
                    case HintPack1:
                        UserPreferences.getInstance(context).addHints(-1 * amount);
                        break;
                    case HintPack10:
                        UserPreferences.getInstance(context).addHints(-10 * amount);
                        break;
                    default:
                        throw new RuntimeException("cant process item: " + item);
                }
                break;
            case LevelPack:
                String name = item.getLevelPackName();
                if (name == null)
                    throw new RuntimeException("can't find level pack name by Item: " + item.toString());
                LevelPack pack = LevelPackTable.get(name, context);
                if (pack == null)
                    throw new RuntimeException("can't find level pack name name: " + name);
                if (!UserPreferences.getInstance(context).isPackUnlocked(pack))
                    return false;
                UserPreferences.getInstance(context).lockLevelPack(pack);
                break;
            default:
                throw new RuntimeException("cant process item: " + item);
        }
        runOnItemRefund.postMe(mHandler, mStoreListener, item);
        return true;
    }

    protected abstract class PostableRunable<T> implements Runnable{
        protected IStoreListener mListener;
        protected T mPayload;
        public void postMe(Handler handler, IStoreListener listener, T payload){
            mListener = listener;
            mPayload = payload;
            if (handler != null && listener != null)
                handler.post(this);
            else if (mListener != null)
                run();
        }
    }

    protected PostableRunable<Boolean> runOnBillingSupported = new PostableRunable<Boolean>() {
        @Override
        public void run() {
            mListener.onBillingSupported(mPayload);
        }
    };

    protected PostableRunable<Goods> runOnItemBought = new PostableRunable<Goods>() {
        @Override
        public void run() {
            mListener.onItemBought(mPayload);
        }
    };

    protected PostableRunable<Goods> runOnItemRefund = new PostableRunable<Goods>() {
        @Override
        public void run() {
            mListener.onItemRefunded(mPayload);
        }
    };
}
