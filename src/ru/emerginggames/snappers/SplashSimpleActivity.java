package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import ru.emerginggames.snappers.data.LevelDbLoader;

public class SplashSimpleActivity extends Activity {
    private static final int SPLASH_TIME = 3000;
    public static final String PREFERENCES = "preferences";
    private Thread splashTread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (splashTread == null) {
            splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        long time = System.currentTimeMillis();
                        LevelDbLoader.checkAndLoad(SplashSimpleActivity.this, getSharedPreferences(PREFERENCES, MODE_PRIVATE));
                        time = System.currentTimeMillis() - time - SPLASH_TIME;
                        if (time < 1)
                            time =1;

                        synchronized (this) {
                            wait(time);
                        }
                    } catch (InterruptedException e) {
                    } finally {
                        setSize();
                        Resources.createFrames();
                        finish();
                        startActivity(new Intent(SplashSimpleActivity.this, SelectLevelActivity.class));
                    }
                }
            };
            splashTread.start();
        }
    }

    private void setSize() {
        View d = getWindow().getDecorView();
        int resultWidth = findViewById(R.id.mainCont).getWidth();
        int resultHeight = findViewById(R.id.mainCont).getHeight();



        if (resultHeight < resultWidth || resultHeight ==0 || resultWidth == 0) {
            Rect rectangle = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int viewWidth = Math.abs(rectangle.width());
            int viewHeight = Math.abs(rectangle.height());
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;
            
            if (screenHeight < screenWidth){
                int i = screenHeight;
                screenHeight = screenWidth;
                screenWidth = i;
                i = viewHeight;
                viewHeight = viewWidth;
                viewWidth = i;
            }

            if (viewHeight == 0 || viewWidth == 0){
                Metrics.setSize(screenWidth, screenHeight);
                return;
            }

            if (screenWidth == viewWidth){
                Metrics.setSize(viewWidth, viewHeight);
                return;
            }

            int widthDiff = screenWidth - viewWidth;
            Metrics.setSize(screenWidth, screenHeight - widthDiff);
        }
        else
            Metrics.setSize(resultWidth, resultHeight);
    }
}