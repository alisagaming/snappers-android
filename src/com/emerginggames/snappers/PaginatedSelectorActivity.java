package com.emerginggames.snappers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.view.MyAlertDialog;
import com.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 15:01
 */
public class PaginatedSelectorActivity extends FragmentActivity {
    MyAlertDialog dlg;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_selector);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int defPadding = width/40;

        OutlinedTextView scoreView = (OutlinedTextView)findViewById(R.id.score);
        scoreView.setTypeface(Resources.getFont(this));
        scoreView.setTextSize(width/15);
        scoreView.setPadding(0, defPadding, defPadding, -defPadding);
        String scoreStr = getResources().getString(R.string.score, UserPreferences.getInstance(this).getScore());
        scoreView.setText(scoreStr);

        findViewById(R.id.root).setPadding(0, 0 , 0, defPadding);
        //findViewById(R.id.footer).setPadding(0, 0, 0, 0);
        
        findViewById(R.id.indicator).setPadding(defPadding, defPadding, defPadding, defPadding);

        SoundManager.getInstance(this).setUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OutlinedTextView scoreView = (OutlinedTextView)findViewById(R.id.score);
        String scoreStr = getResources().getString(R.string.score, UserPreferences.getInstance(this).getScore());
        scoreView.setText(scoreStr);
        ((SnappersApplication)getApplication()).activityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ((SnappersApplication)getApplication()).setSwitchingActivities();
        ((SnappersApplication)getApplication()).activityPaused();
    }

    public void onBackButtonClick(View v){
        SoundManager.getInstance(this).playButtonSound();
        finish();
    }

    public void onStoreButtonClick(View v){
        ((SnappersApplication)getApplication()).setSwitchingActivities();
        SoundManager.getInstance(this).playButtonSound();
        startActivity(new Intent(this, StoreActivity.class));
    }

    public void onMoreGamesButtonClick(View v){
        ((SnappersApplication)getApplication()).setSwitchingActivities();
        SoundManager.getInstance(this).playButtonSound();
        startActivity(new Intent(this, MoreGamesActivity.class));
    }
    
    public void showMessageDialog(String message, int[] lineEnds, View.OnClickListener leftListener, View.OnClickListener rightListener){
        if (dlg == null)
            dlg = new MyAlertDialog(this);
        dlg.setLeftButton(R.drawable.unlock_button, leftListener);
        dlg.setRightButton(R.drawable.ok_button, rightListener);
        dlg.setMessage(message, lineEnds);
        dlg.show();
    }
    
    public void showMessage(String msg){
        showMessageDialog(msg, null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageDialog();
            }
        });
    }

    public void hideMessageDialog(){
        dlg.hide();
    }
}