package com.emerginggames.snappers;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.gdx.Splash;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.utils.OnlineSettings;
import com.emerginggames.snappers.utils.WorkerThread;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;
import com.tapjoy.TapjoyConnect;
import com.emerginggames.snappers.data.DbCopyOpenHelper;

import net.hockeyapp.android.UpdateManager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.03.12
 * Time: 23:49
 */
public class SplashGdxActivity extends AndroidApplication {
    private static final int SPLASH_TIME = 2000;
    //AsyncTask<Integer, Integer, Integer> loadTask;
    volatile boolean initTaskRan;

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
        initTaskRan = false;
    }


    void setupBackPosition(){
        ImageView back = (ImageView)findViewById(R.id.bgImage);
        Drawable bgImage = back.getDrawable();
        Rect r = new Rect();
        back.getWindowVisibleDisplayFrame(r);
        if (bgImage instanceof BitmapDrawable)
        {
            int imgW, imgH;
            imgW = bgImage.getIntrinsicWidth();
            imgH = bgImage.getIntrinsicHeight();
            float scale = (float)r.height()/imgH ;

            Matrix m = new Matrix();
            m.setScale(scale, scale);
            m.postTranslate(-(imgW * scale - r.width())/2, 0);
            back.setImageMatrix(m);
        }
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
        setupBackPosition();

        if (initTaskRan)
            return;

        WorkerThread.getInstance().post(init);
    }

    private void checkForUpdates() {
        UpdateManager.register(this, Settings.APP_ID);
    }

    Runnable init = new Runnable() {
        @Override
        public void run() {
            initTaskRan = true;
            long startTime = System.currentTimeMillis();
            new DbCopyOpenHelper(SplashGdxActivity.this).initializeDataBase();

            OnlineSettings.update(getApplicationContext());

            String tjSecretKey = Settings.getTapJoyAppId(getApplicationContext());

            TapjoyConnect.requestTapjoyConnect(getApplicationContext(), tjSecretKey, tjSecretKey);

            UserPreferences.getInstance(SplashGdxActivity.this);
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

            runOnUiThread(finish);
            Resources.preload();
        }
    };

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            finish();
            startActivity(new Intent(SplashGdxActivity.this, MainScreenActivity.class));
        }
    };
}
