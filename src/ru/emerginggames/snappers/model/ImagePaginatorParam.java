package ru.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 02.04.12
 * Time: 9:51
 */
public class ImagePaginatorParam {
    public ImageDrawInfo[] images;
    public int retParam;

    public ImagePaginatorParam(ImageDrawInfo[] images, int retParam) {
        this.images = images;
        this.retParam = retParam;
    }
}
