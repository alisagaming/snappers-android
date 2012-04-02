package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 15:01
 */
public class PaginatedSelectorActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_selector);

        Resources.loadFont(this);
        OutlinedTextView scoreView = (OutlinedTextView)findViewById(R.id.score);
        scoreView.setStroke(Color.BLACK, 2);
        scoreView.setTypeface(Resources.font);
        int textSize = getWindowManager().getDefaultDisplay().getWidth()/10;
        scoreView.setTextSize(textSize);
        scoreView.setPadding(0, textSize/4, textSize/4, -textSize/2);
        int rootPadding = getWindowManager().getDefaultDisplay().getHeight()/40;
        findViewById(R.id.root).setPadding(0, 0 , 0, rootPadding);

        findViewById(R.id.footer).setPadding(0, rootPadding, 0, 0);

    }

    public void onBackButtonClick(View v){
        finish();
    }

    public void onStoreButtonClick(View v){
        startActivity(new Intent(this, StoreActivity.class));
    }
}