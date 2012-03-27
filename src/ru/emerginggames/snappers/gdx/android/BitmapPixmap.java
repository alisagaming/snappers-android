package ru.emerginggames.snappers.gdx.android;

import android.graphics.Bitmap;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.03.12
 * Time: 18:31
 */
public class BitmapPixmap {

    public static Pixmap bitmapToPixmap(Bitmap bitmap){
        int size = bitmap.getWidth() * bitmap.getHeight() * 2;
        ByteArrayOutputStream outStream;
        while (true){
            outStream = new ByteArrayOutputStream(size);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream))
                break;
            size = size * 3 / 2;
        }
        byte[] img = outStream.toByteArray();
        return new Pixmap(img, 0, img.length);
    }
}
