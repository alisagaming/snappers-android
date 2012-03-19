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

public class SplashSimpleActivity extends Activity {
    private static final int SPLASH_TIME = 3000;
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
                        synchronized (this) {
                            wait(SPLASH_TIME);
                        }
                    } catch (InterruptedException e) {
                    } finally {
                        setSize();
                        finish();
                        startActivity(new Intent(SplashSimpleActivity.this, GameActivity.class));
                    }
                }
            };
            splashTread.start();
        }
    }

    private void setSize() {
        View d = getWindow().getDecorView();
        Metrics.screenWidth = findViewById(R.id.mainCont).getWidth();
        Metrics.screenHeight = findViewById(R.id.mainCont).getHeight();



        if (Metrics.screenHeight < Metrics.screenWidth || Metrics.screenHeight ==0 || Metrics.screenWidth == 0) {
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
                Metrics.screenWidth = screenWidth;
                Metrics.screenHeight = screenHeight;
                return;
            }

            if (screenWidth == viewWidth){
                Metrics.screenWidth = viewWidth;
                Metrics.screenHeight = viewHeight;
                return;
            }

            int widthDiff = screenWidth - viewWidth;
            Metrics.screenHeight = screenHeight - widthDiff;
            Metrics.screenWidth = screenWidth;
        }
    }
}