package com.emerginggames.snappers.model;

import com.emerginggames.snappers.R;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 02.04.12
 * Time: 9:51
 */
public class ImagePaginatorParam {
    public static final int LOCKED = -1;
    public static final int COMING_SOON = -2;
    public static final int HINT_PACK = -3;
    public static final int ADFREE = -4;
    public ImageDrawInfo[] images;
    public int retParam;

    public ImagePaginatorParam(ImageDrawInfo[] images, int retParam) {
        this.images = images;
        this.retParam = retParam;
    }

    public ImagePaginatorParam(int id, int retParam) {
        this.retParam = retParam;
        this.images = getLevelImageIds(id);
    }

    public static ImageDrawInfo[] getLevelImageIds(int id){
        switch (id){
            case LOCKED:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.locked, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case COMING_SOON:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.coming, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case HINT_PACK:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.hints_cover, true, true)};
            case ADFREE:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.adfree_cover, true, true)};
            case 1:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack1, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 2:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack2, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 3:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack3, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 4:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack4, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 5:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack5, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 6:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack6, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 7:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack7, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 8:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack1, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 9:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack2, true, true)
                };
            case 10:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack3, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 11:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack4, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
        }

        return null;
    }
}
