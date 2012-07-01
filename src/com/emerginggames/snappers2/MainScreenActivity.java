package com.emerginggames.snappers2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import android.widget.RelativeLayout;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.transport.FacebookTransport;
import com.emerginggames.snappers2.view.GameDialog;
import com.emrg.view.OutlinedTextView;
import net.hockeyapp.android.CrashManager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 07.04.12
 * Time: 19:05
 */
public class MainScreenActivity extends Activity {
    private static final int[] DAILY_BONUS_PADDINGS = {36, 44, 36, 23};
    private static final long DAY_MS = 24 * 60 * 60 * 1000;
    public final Handler handler = new Handler();
    SettingsDialog settingsDialog;
    GameDialog messageDialog;
    UserPreferences prefs;
    boolean isActive;
    private static final int[] BONUS_CHANCES = {60, 20, 10, 5, 5};
    int scrWidth;
    int scrHeight;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_main);

        scrWidth = getWindowManager().getDefaultDisplay().getWidth();
        scrHeight = getWindowManager().getDefaultDisplay().getHeight();
        prefs = UserPreferences.getInstance(this);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.logo).getLayoutParams();
        lp.width = Math.round(scrWidth * 0.8f);
        lp.topMargin = scrWidth / 20;

        lp = (RelativeLayout.LayoutParams) findViewById(R.id.playButtonOnline).getLayoutParams();
        lp.width = Math.round(scrWidth * 0.45f);
        lp.rightMargin = scrWidth / 20;

        lp = (RelativeLayout.LayoutParams) findViewById(R.id.playButtonOffline).getLayoutParams();
        lp.width = (int) (scrWidth * 0.35f);
        lp.rightMargin = scrWidth / 20;
        lp.topMargin = lp.bottomMargin = scrHeight / 20;

        lp = (RelativeLayout.LayoutParams) findViewById(R.id.settingsBtn).getLayoutParams();
        lp.width = lp.height = scrWidth / 5;

        setupDailyBonusCounter();
        SoundManager.getInstance(this).setUp();
    }

    void setupDailyBonusCounter() {
        OutlinedTextView dailyBonus = (OutlinedTextView) findViewById(R.id.dailyBonus);
        dailyBonus.setMaxLines2(1);
        dailyBonus.setBackgroundPaddings(DAILY_BONUS_PADDINGS);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) dailyBonus.getLayoutParams();
        mlp.width = Math.round(scrWidth * 0.45f);
        mlp.height = LayoutParams.WRAP_CONTENT;
        mlp.rightMargin = scrWidth / 20;
        dailyBonus.setLayoutParams(mlp);
        float scale = (float) mlp.width / dailyBonus.getBackground().getIntrinsicWidth();
        mlp.height = (int) (dailyBonus.getBackground().getIntrinsicHeight() * scale);
        dailyBonus.setTypeface(Resources.getFont(this));
        dailyBonus.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
    }

    public void settingsButtonClick(View v) {
        if (settingsDialog == null) {
            settingsDialog = new SettingsDialog(this, getWindow().getDecorView().getWidth() * 95 / 100);
            settingsDialog.setOwnerActivity(this);
        }
        settingsDialog.show();
        findViewById(R.id.settingsBtn).setVisibility(View.INVISIBLE);
    }

    public void onPlayButtonClick(View v) {
        //prefs.addHints(680);
        startActivity(new Intent(this, FacebookActivity.class));
    }

    public void onDailyBonus(View v) {
        long lastUsed = prefs.getLastUsedDailyBonus();
        long now = System.currentTimeMillis();
        long diff = now - lastUsed;
        if (diff > DAY_MS) {
            int ch = (int) (Math.random() * 100);
            int sum = 0;
            for (int i = 0; i < BONUS_CHANCES.length; i++) {
                sum += BONUS_CHANCES[i];
                if (ch < sum) {
                    if (messageDialog == null) {
                        messageDialog = new GameDialog(this, getWindow().getDecorView().getWidth() * 95 / 100);
                        messageDialog.addOkButton();
                        messageDialog.setTypeface(Resources.getFont(this));
                    }

                    prefs.addHints(i + 1);
                    if (i == 0)
                        messageDialog.setMessage(R.string.received_bonus_hint, Metrics.fontSize);
                    else
                        messageDialog.setMessage(getString(R.string.received_bonus_hints, i + 1), Metrics.fontSize);

                    messageDialog.show();
                    break;
                }
            }

            prefs.setLastUsedDailyBonus(System.currentTimeMillis());
            updateDailyBonusCounter.run();
        }
    }

    public void onSettingsDialogClosed() {
        findViewById(R.id.settingsBtn).setVisibility(View.VISIBLE);

        FacebookTransport facebookTransport = new FacebookTransport(this);
        findViewById(R.id.playButtonOffline).setVisibility(facebookTransport.isLoggedIn() ? View.INVISIBLE: View.VISIBLE);
    }

    public void onPlayButtonOfflineClick(View v) {
        SoundManager.getInstance(this).playButtonSound();
        ((SnappersApplication) getApplication()).setSwitchingActivities();
        startActivity(new Intent(this, SelectPackActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.CRASH_REPORTER == Settings.CrashReporter.HockeyApp)
            checkForCrashes();
        ((SnappersApplication) getApplication()).activityResumed(this);
        isActive = true;
        if (Settings.IS_PREMIUM)
            handler.post(updateDailyBonusCounter);
        else
            findViewById(R.id.dailyBonus).setVisibility(View.INVISIBLE);

        FacebookTransport facebookTransport = new FacebookTransport(this);

        findViewById(R.id.playButtonOffline).setVisibility(facebookTransport.isLoggedIn() ? View.INVISIBLE: View.VISIBLE);
    }



    @Override
    protected void onPause(){
        super.onPause();
        ((SnappersApplication) getApplication()).activityPaused();
        isActive = false;
    }

    private void checkForCrashes(){
        CrashManager.register(this, Settings.APP_ID);
    }

    Runnable updateDailyBonusCounter = new Runnable() {
        @Override
        public void run() {
            if (!isActive)
                return;

            long lastUsed = prefs.getLastUsedDailyBonus();
            long now = System.currentTimeMillis();
            OutlinedTextView bonusBtn = (OutlinedTextView) findViewById(R.id.dailyBonus);
            long diff = now - lastUsed;
            if (diff > DAY_MS)
                bonusBtn.setText2(R.string.collect_now);
            else {
                int s, m, h, dt;
                dt = (int) ((DAY_MS - diff) / 1000);
                s = dt % 60;
                dt = dt / 60;
                m = dt % 60;
                dt = dt / 60;
                h = dt % 24;
                bonusBtn.setText2(String.format("%d:%02d:%02d", h, m, s));
                handler.postDelayed(updateDailyBonusCounter, 1000);
            }
        }
    };
}