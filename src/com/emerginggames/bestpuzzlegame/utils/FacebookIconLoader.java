package com.emerginggames.bestpuzzlegame.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.emerginggames.bestpuzzlegame.Settings;
import com.emrg.view.ImageView;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 04.08.12
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class FacebookIconLoader extends CachedFile implements CachedFile.CachedFileListener {
    private static final String URL_TEMPLATE_MED = "http://graph.facebook.com/%d/picture?type=normal";
    private static final String FILE_TEMPLATE = "fbicon_%d.png";
    ImageView view;
    long uid;
    Activity activity;

    public FacebookIconLoader(Activity activity, long uid, ImageView view) {
        super(activity.getApplicationContext());
        this.uid = uid;
        this.view = view;
        this.activity = activity;
        setData(String.format(FILE_TEMPLATE, uid), String.format(URL_TEMPLATE_MED, uid));
        setListener(this);
    }

    @Override
    public void onGotFile(final File file) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream input = new FileInputStream(file);
                    Bitmap mIcon1 = BitmapFactory.decodeStream(input);
                    view.setImageBitmap(mIcon1);
                    input.close();

                } catch (FileNotFoundException e) {
                    onError(e);
                } catch (IOException e) {
                    onError(e);
                }
            }
        });
    }

    @Override
    public void onUpdateFile(File file) {
        onGotFile(file);
    }

    @Override
    public void onError(Throwable t) {
        Log.e(Settings.TAG, t.getMessage(), t);
    }
}
