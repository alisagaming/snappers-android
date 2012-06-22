package com.emerginggames.snappers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.data.DbCopyOpenHelper;
import com.emerginggames.snappers.gdx.Splash;

import com.tapjoy.TapjoyConnect;
import net.hockeyapp.android.UpdateManager;
import com.emerginggames.snappers.utils.OnlineSettings;
import com.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:49
 */
public class SplashGdxActivity extends AndroidApplication {
    private static final int SPLASH_TIME = 2000;
    AsyncTask<Integer, Integer, Integer> loadTask;

    public void onCreate(Bundle savedInstanceState) {
        DbSettings.ENABLE_ALL_LEVELS = Settings.ENABLE_ALL_LEVELS;
        super.onCreate(savedInstanceState);
        Resources.context = getApplicationContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;

        View gameView = initializeForView(new Splash(this), config);
        RelativeLayout rootLayout = new RelativeLayout(this);
        rootLayout.addView(gameView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        rootLayout.addView(getLayoutInflater().inflate(R.layout.splash, null), lp);

        setContentView(rootLayout);
        OutlinedTextView textView = (OutlinedTextView)rootLayout.findViewById(R.id.message);
        textView.setTypeface(Resources.getFont(this));

        if (Settings.CRASH_REPORTER == Settings.CrashReporter.HockeyApp)
            checkForUpdates();


    }

    public void gotSize(int width, int height) {
        if (width > height){
            int scrWidth = getWindowManager().getDefaultDisplay().getWidth();
            int scrHeight = getWindowManager().getDefaultDisplay().getHeight();
            int panelHeight = scrHeight - height;
            width = scrHeight;
            height = scrWidth - panelHeight;
        }

        Metrics.setSize(width, height, getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (loadTask != null)
            return;

        loadTask = new AsyncTask<Integer, Integer, Integer>(){
            @Override
            protected Integer doInBackground(Integer... params) {
                long startTime = System.currentTimeMillis();
                new DbCopyOpenHelper(SplashGdxActivity.this).initializeDataBase();
                TapjoyConnect.requestTapjoyConnect(getApplicationContext(), Settings.getTapJoyAppId(getApplicationContext()), Settings.getTapJoySecretKey(getApplicationContext()));

                UserPreferences.getInstance(SplashGdxActivity.this);
                OnlineSettings.update(getApplicationContext());
                GInAppStore.getInstance(getApplicationContext());

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
                startActivity(new Intent(SplashGdxActivity.this, MainScreenActivity.class));
            }

            @Override
            protected void onPostExecute(Integer integer) {
                SplashGdxActivity.this.loadTask = null;
            }
        };
        loadTask.execute();
    }

    private void checkForUpdates() {
        UpdateManager.register(this, Settings.APP_ID);
    }
}
