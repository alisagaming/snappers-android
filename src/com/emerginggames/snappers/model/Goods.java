package com.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 14:33
 */
public enum Goods {
    HintPack1, HintPack10,
    LevelPack2, LevelPack3, LevelPack4, LevelPack5, LevelPack6, LevelPack7,
    PremiumLevelPack1, PremiumLevelPack2, PremiumLevelPack3, PremiumLevelPack4,
    AdFree, AdFree6hours;

    public static enum Type {Hint, LevelPack, adFree}

    public static final String LEVEL_PACK_2 = "level-pack-2";

    public static final String LEVEL_PACK_3 = "level-pack-3";

    public static final String LEVEL_PACK_4 = "level-pack-4";

    public static final String LEVEL_PACK_5 = "level-pack-5";

    public static final String LEVEL_PACK_6 = "level-pack-6";

    public static final String LEVEL_PACK_7 = "level-pack-7";

    public static final String PREMIUM_LEVEL_PACK_1 = "premium-level-pack-1";

    public static final String PREMIUM_LEVEL_PACK_2 = "premium-level-pack-2";

    public static final String PREMIUM_LEVEL_PACK_3 = "premium-level-pack-3";

    public static final String PREMIUM_LEVEL_PACK_4 = "premium-level-pack-4";

    public static Goods getGoodsByLevelPack(LevelPack pack) {
        if (LEVEL_PACK_2.equals(pack.name))
            return Goods.LevelPack2;

        if (LEVEL_PACK_3.equals(pack.name))
            return Goods.LevelPack3;

        if (LEVEL_PACK_4.equals(pack.name))
            return Goods.LevelPack4;

        if (LEVEL_PACK_5.equals(pack.name))
            return Goods.LevelPack5;

        if (LEVEL_PACK_6.equals(pack.name))
            return Goods.LevelPack6;

        if (LEVEL_PACK_7.equals(pack.name))
            return Goods.LevelPack7;

        if (PREMIUM_LEVEL_PACK_1.equals(pack.name))
            return Goods.PremiumLevelPack1;

        if (PREMIUM_LEVEL_PACK_2.equals(pack.name))
            return Goods.PremiumLevelPack2;

        if (PREMIUM_LEVEL_PACK_3.equals(pack.name))
            return Goods.PremiumLevelPack3;

        if (PREMIUM_LEVEL_PACK_4.equals(pack.name))
            return Goods.PremiumLevelPack4;

        return null;
    }

    public String getLevelPackName(){
        switch (this){
            case LevelPack2:
                return LEVEL_PACK_2;
            case LevelPack3:
                return LEVEL_PACK_3;
            case LevelPack4:
                return LEVEL_PACK_4;
            case LevelPack5:
                return LEVEL_PACK_5;
            case LevelPack6:
                return LEVEL_PACK_6;
            case LevelPack7:
                return LEVEL_PACK_7;
            case PremiumLevelPack1:
                return PREMIUM_LEVEL_PACK_1;
            case PremiumLevelPack2:
                return PREMIUM_LEVEL_PACK_2;
            case PremiumLevelPack3:
                return PREMIUM_LEVEL_PACK_3;
            case PremiumLevelPack4:
                return PREMIUM_LEVEL_PACK_4;
        }
        return null;
    }

    public Type getType() {
        switch (this) {
            case AdFree:
            case AdFree6hours:
                return Type.adFree;
            case HintPack1:
            case HintPack10:
                return Type.Hint;
            case LevelPack2:
            case LevelPack3:
            case LevelPack4:
            case LevelPack5:
            case LevelPack6:
            case LevelPack7:
            case PremiumLevelPack1:
            case PremiumLevelPack2:
            case PremiumLevelPack3:
            case PremiumLevelPack4:
                return Type.LevelPack;
            default:
                return null;
        }
    }

    public boolean isPermanent(){
        Type t = getType();
        switch (t){
            case Hint:
                return false;
            case LevelPack:
                return true;
            case adFree:
                if (this == AdFree)
                    return true;
                if (this == AdFree6hours)
                    return false;
        }
        return false;
    }
}
