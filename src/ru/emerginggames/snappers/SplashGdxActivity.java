package ru.emerginggames.snappers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ru.emerginggames.snappers.data.DbCopyOpenHelper;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.gdx.Splash;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:49
 */
public class SplashGdxActivity extends AndroidApplication {
    private static final int SPLASH_TIME = 2000;
    AsyncTask<Integer, Integer, Integer> loadThread;

    public void onCreate(Bundle savedInstanceState) {
        DbSettings.ENABLE_ALL_LEVELS = Settings.ENABLE_ALL_LEVELS;
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.font = Typeface.createFromAsset(getAssets(), "shag_lounge.otf");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = false;

        initialize(new Splash(this), config);
    }

    public void gotSize(int width, int height) {
        Metrics.setSize(width, height);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (loadThread != null)
            return;

        loadThread = new AsyncTask<Integer, Integer, Integer>(){
            @Override
            protected Integer doInBackground(Integer... params) {
                long startTime = System.currentTimeMillis();
                new DbCopyOpenHelper(SplashGdxActivity.this).initializeDataBase();
                UserPreferences.getInstance(SplashGdxActivity.this);

                long now = System.currentTimeMillis();
                if (now - startTime < SPLASH_TIME)
                    try{
                        synchronized (this){
                            wait(SPLASH_TIME - (now - startTime));
                        }
                    }
                    catch (InterruptedException ex){}

                while (!Metrics.initDone)
                    try{
                        synchronized (this){
                            wait(100);
                        }
                    }
                    catch (InterruptedException ex){}

                this.publishProgress(1);
                Resources.preload();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                finish();
                startActivity(new Intent(SplashGdxActivity.this, SelectPackActivity.class));
            }

            @Override
            protected void onPostExecute(Integer integer) {
                SplashGdxActivity.this.loadThread = null;
            }
        };
        loadThread.execute();
    }
}
