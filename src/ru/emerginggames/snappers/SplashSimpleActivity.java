package ru.emerginggames.snappers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 02.03.12
 * Time: 12:24
 */
public class SplashSimpleActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);


    }

    @Override
    protected void onResume() {
        super.onResume();

        View img = findViewById(R.id.splashImage);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Window wnd = getWindow();
                View d =getWindow().getDecorView();
                int i=3;
            }
        });

    }


}