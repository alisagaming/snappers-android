package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 07.04.12
 * Time: 19:05
 */
public class MainScreenActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_main);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();

        int defPadding = width/40;
        
        //findViewById(R.id.root).setPadding(defPadding, defPadding, defPadding, defPadding);
        LayoutParams lp = findViewById(R.id.shopButton).getLayoutParams();
        lp.width = Math.round(width* 0.6f);
        findViewById(R.id.shopButton).setLayoutParams(lp);

        lp = findViewById(R.id.playButton).getLayoutParams();
        int playSize = lp.width = lp.height = Math.round(width * 0.4f);
        findViewById(R.id.playButton).setLayoutParams(lp);

        lp = findViewById(R.id.logoCont).getLayoutParams();
        lp.height = (height - playSize)/2;
        lp.height = Math.round(lp.height * 0.8f);
        findViewById(R.id.logoCont).setLayoutParams(lp);

        findViewById(R.id.options).setVisibility(View.GONE);

        UserPreferences prefs = UserPreferences.getInstance(this);
        ((CheckBox)findViewById(R.id.soundCheck)).setChecked(prefs.getSound());
        ((CheckBox)findViewById(R.id.musicCheck)).setChecked(prefs.getMusic());
    }

    public void onMusicButtonClick(View v){
        UserPreferences.getInstance(this).setMusic(((CheckBox)v).isChecked());
    }

    public void onSoundButtonClick(View v){
        UserPreferences.getInstance(this).setSound(((CheckBox) v).isChecked());
    }

    public void settingsButtonClick(View v){
        View optCont = findViewById(R.id.options);
        optCont.setVisibility(optCont.getVisibility() == View.GONE? View.VISIBLE : View.GONE);
    }

    public void onStoreButtonClick(View v){
        startActivity(new Intent(this, StoreActivity.class));
    }

    public void onPlayButtonClick(View v){
        startActivity(new Intent(this, SelectPackActivity.class));
    }


}