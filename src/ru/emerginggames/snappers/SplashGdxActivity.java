package ru.emerginggames.snappers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ru.emerginggames.snappers.data.DbOpenHelper;
import ru.emerginggames.snappers.data.LevelDbLoader;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.Splash;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:49
 */
public class SplashGdxActivity extends AndroidApplication {
    private static final int SPLASH_TIME = 3000;
    public static final String PREFERENCES = "preferences";
    private Thread splashTread;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.font = Typeface.createFromAsset(getAssets(), "shag_lounge.otf");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;

        initialize(new Splash(this), config);
    }

    public void gotSize(int width, int height) {
        if (splashTread == null) {
            Metrics.setSize(width, height);

            splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        long time = System.currentTimeMillis();
                        //LevelDbLoader.checkAndLoad(SplashGdxActivity.this, getSharedPreferences(PREFERENCES, MODE_PRIVATE));
                        DbOpenHelper openHelper = new DbOpenHelper(SplashGdxActivity.this);
                        openHelper.initializeDataBase();

                        synchronized (this) {
                            wait(10);
                        }

                        Resources.preload();
                        time = SPLASH_TIME - (System.currentTimeMillis() - time);
                        if (time < 1)
                            time = 1;

                        synchronized (this) {
                            wait(time);
                        }
                    } catch (InterruptedException e) {
                    } finally {
                        //setSize();
                        //Resources.createFrames();
                        finish();
                        startActivity(new Intent(SplashGdxActivity.this, SelectLevelActivity.class));
                    }
                }
            };
            splashTread.start();
        }
    }
}
