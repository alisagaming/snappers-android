package ru.emerginggames.snappers.utils;

import android.content.Context;
import ru.emerginggames.snappers.UserPreferences;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.04.12
 * Time: 12:34
 */
public class Store {
    Context context;
    static Store store;

    public Store(Context context) {
        this.context = context;
    }

    public static Store getInstance(Context context){
        if (store == null)
            store = new Store(context);
        return store;
    }

    public boolean itemBought(Goods item){
        switch (item.getType()){
            case adFree:
                if (item == Goods.AdFree){
                    UserPreferences.getInstance(context).setAdFree(true);
                    return true;
                }
                break;
            case Hint:
                switch (item){
                    case HintPack1:
                        UserPreferences.getInstance(context).addHints(1);
                        return true;
                    case HintPack10:
                        UserPreferences.getInstance(context).addHints(10);
                        return true;
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
                return true;
        }
        throw new RuntimeException("cant process item: " + item);
    }

    public boolean itemReturned(Goods item){
        switch (item.getType()){
            case adFree:
                if (item == Goods.AdFree){
                    UserPreferences.getInstance(context).setAdFree(false);
                    return true;
                }
                break;
            case Hint:
                switch (item){
                    case HintPack1:
                        UserPreferences.getInstance(context).addHints(-1);
                        return true;
                    case HintPack10:
                        UserPreferences.getInstance(context).addHints(-10);
                        return true;
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
                return true;
        }
        throw new RuntimeException("cant process item: " + item);
    }
}
