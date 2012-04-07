package ru.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 14:33
 */
public class GoodsToShop {
    public enum Goods {HintPack1, HintPack10,
        LevelPack2, LevelPack3, LevelPack4, LevelPack5, LevelPack6, LevelPack7,
        PremiumLevelPack1, PremiumLevelPack2, PremiumLevelPack3, PremiumLevelPack4}

    public static Goods getGoodsByLevelPack(LevelPack pack){
        if ("level-pack-2".equals(pack.name))
            return Goods.LevelPack2;

        if ("level-pack-3".equals(pack.name))
            return Goods.LevelPack3;

        if ("level-pack-4".equals(pack.name))
            return Goods.LevelPack4;

        if ("level-pack-5".equals(pack.name))
            return Goods.LevelPack5;

        if ("level-pack-6".equals(pack.name))
            return Goods.LevelPack6;

        if ("level-pack-7".equals(pack.name))
            return Goods.LevelPack7;

        if ("premium-level-pack-1".equals(pack.name))
            return Goods.PremiumLevelPack1;

        if ("premium-level-pack-2".equals(pack.name))
            return Goods.PremiumLevelPack2;

        if ("premium-level-pack-3".equals(pack.name))
            return Goods.PremiumLevelPack3;

        if ("premium-level-pack-4".equals(pack.name))
            return Goods.PremiumLevelPack4;

        return null;
    }
}
