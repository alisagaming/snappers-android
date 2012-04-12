package ru.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.adapters.AdWhirlAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.VGStoreItem;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.IAppGameListener;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.utils.IOnAdShowListener;
import ru.emerginggames.snappers.utils.MyAdWhirlLayout;
import ru.emerginggames.snappers.utils.MyTapjoyStore;
import ru.emerginggames.snappers.utils.TapjoyPointsListener;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:23
 */
public class GameActivity extends AndroidApplication implements IAppGameListener, IOnAdShowListener {
    public static final String LEVEL_PARAM_TAG = "Level";
    public static final String LEVEL_PACK_PARAM_TAG = "Level pack";
    MyAdWhirlLayout adWhirlLayout;
    RelativeLayout rootLayout;
    boolean isShowingAd;
    boolean shouldShowAd;
    boolean canShowAd;
    boolean mayShowAd;
    boolean isFinished = false;
    MyTapjoyStore tapjoyStore;
    boolean wentTapjoy;

    Game game;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.getFont(this);

        Intent intent = getIntent();
        Level level = (Level) intent.getSerializableExtra(LEVEL_PARAM_TAG);
        LevelPack pack = (LevelPack) intent.getSerializableExtra(LEVEL_PACK_PARAM_TAG);
        if (level == null)
            level = (Level) savedInstanceState.getSerializable(LEVEL_PARAM_TAG);
        if (pack == null)
            pack = (LevelPack) savedInstanceState.getSerializable(LEVEL_PACK_PARAM_TAG);
        if (level == null || pack == null) {
            finish();
            return;
        }

        game = new Game();
        game.setStartLevel(level, pack);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = false;

        View gameView = initializeForView(game, config);
        rootLayout = new RelativeLayout(this);
        rootLayout.addView(gameView);

        if (!UserPreferences.getInstance(this).isAdFree()) {
            RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            AdWhirlTargeting.setKeywords("game puzzle");
            AdWhirlAdapter.setGoogleAdSenseExpandDirection("BOTTOM");
            if (Settings.DEBUG)
                AdWhirlTargeting.setTestMode(true);
            MyAdWhirlLayout.setEnforceUpdate(true);
            adWhirlLayout = new MyAdWhirlLayout(this, Settings.getAdwhirlKey(this));
            rootLayout.addView(adWhirlLayout, adParams);
            adWhirlLayout.setVisibility(View.INVISIBLE);
            adWhirlLayout.setAdShowListener(this);
        }

        setContentView(rootLayout);

        isShowingAd = false;
        shouldShowAd = false;
        canShowAd = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LEVEL_PARAM_TAG, game.getLevel());
        outState.putSerializable(LEVEL_PACK_PARAM_TAG, game.getLevelPack());
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance(this).stopMusic();
        if (isFinishing()) {
            if (adWhirlLayout != null){
                adWhirlLayout.setAdShowListener(null);
                MyAdWhirlLayout.setEnforceUpdate(false);
                adWhirlLayout.setVisibility(View.GONE);
            }
            isFinished = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mayShowAd = !UserPreferences.getInstance(this).isAdFree();
        if (!mayShowAd && isShowingAd){
            hideAd();
            if (game.initDone)
                game.setAdHeight(0);
        }
        SoundManager.getInstance(this).startMusicIfShould();
        if (wentTapjoy){
            TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyPointsListener(getApplicationContext()));
            //tapjoyStore.updatePurchasedItems();
            wentTapjoy = false;
        }
    }

    @Override
    public void launchStore() {
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }

    @Override
    public void buy(Goods goods) {
        //TODO:
    }

    @Override
    public void freeHintsPressed() {
//        if (tapjoyStore == null)
//            tapjoyStore = new MyTapjoyStore(getApplicationContext(), null);
        //TapjoyConnect.getTapjoyConnectInstance().setEarnedPointsNotifier(new TapjoyPointsListener(getApplicationContext()));
        //TapjoyConnect.getTapjoyConnectInstance().setUserDefinedColor(0xff808080);

        //TapjoyConnect.getTapjoyConnectInstance().checkForVirtualGoods(tapjoyStore);
        //TapjoyConnect.getTapjoyConnectInstance().showVirtualGoods(tapjoyStore);
        wentTapjoy = true;
        TapjoyConnect.getTapjoyConnectInstance().showOffers();

    }

    @Override
    public void levelPackWon(LevelPack pack) {
        UserPreferences.getInstance(this).unlockNextLevelPack(pack);
        setResult(1);
        finish();
    }

    @Override
    public void levelSolved(Level level) {
        UserPreferences.getInstance(this).unlockNextLevel(level);

    }

    @Override
    public void onBackPressed() {
        if (game.initDone)
            game.backButtonPressed();
    }

    @Override
    public int getHintsLeft() {
        return UserPreferences.getInstance(this).getHintsRemaining();
    }

    @Override
    public void useHint() {
        UserPreferences.getInstance(this).useHint();
    }



    @Override
    public boolean isLevelSolved(Level level) {
        return UserPreferences.getInstance(this).isLevelSolved(level);
    }

    @Override
    public void addScore(int score) {
        UserPreferences.getInstance(this).addScore(score);
    }

    @Override
    public int getAdHeight() {
        return mayShowAd && adWhirlLayout.isAdAvailable() ? adWhirlLayout.getHeight() : 0;
    }

    @Override
    public void showAd() {
        if (!mayShowAd)
            return;
        shouldShowAd = true;
        if (!canShowAd)
            return;
        isShowingAd = true;
        MyAdWhirlLayout.setEnforceUpdate(true);
        runOnUiThread(showAD);
    }

    @Override
    public void hideAd() {
        if (adWhirlLayout == null)
            return;
        shouldShowAd = isShowingAd = false;
        int visibility = adWhirlLayout.getVisibility();
        if (visibility == View.VISIBLE)
            runOnUiThread(hideAD);
    }

    @Override
    public boolean isOnline() {
        //TODO:
        return checkNetworkStatus();
    }



    protected boolean checkNetworkStatus() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (netInfo != null && netInfo.isAvailable())
            return true;

        netInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null && netInfo.isAvailable())
            return true;

        return false;
    }

    @Override
    public void onAdShow() {
        if (isFinished)
            return;
        canShowAd = true;
        if (shouldShowAd) {
            showAd();
            if (game.initDone)
            game.setAdHeight(adWhirlLayout.getHeight());
        }

    }

    @Override
    public void onAdFail() {
        if (isFinished)
            return;
        canShowAd = false;
        if (isShowingAd) {
            hideAd();
            if (game.initDone)
                game.setAdHeight(0);
        }
    }

    @Override
    public void onAdSizeChanged(int width, int height) {
        if (isFinished)
            return;
        if (game.initDone)
            game.setAdHeight(height);
    }

    @Override
    public boolean isSoundEnabled() {
        return UserPreferences.getInstance(this).getSound();
    }

    @Override
    public void gotScreenSize(int width, int height) {
        Metrics.setSize(width, height, this);
    }

    Runnable showAD = new Runnable() {
        @Override
        public void run() {
            if (adWhirlLayout == null)
                return;
            GameActivity.this.adWhirlLayout.setVisibility(View.VISIBLE);
            if (adWhirlLayout.getChildCount() > 0) {
                View v = adWhirlLayout.getChildAt(0);
                adWhirlLayout.removeView(v);
                adWhirlLayout.addView(v);
            }
        }
    };

    Runnable hideAD = new Runnable() {
        @Override
        public void run() {
            if (adWhirlLayout == null)
                return;
            adWhirlLayout.setVisibility(View.INVISIBLE);
        }
    };


}