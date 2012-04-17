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
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.IAppGameListener;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.utils.*;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:23
 */
public class GameActivity extends AndroidApplication{
    public static final String LEVEL_PARAM_TAG = "Level";
    MyAdWhirlLayout adWhirlLayout;
    RelativeLayout rootLayout;
    boolean isShowingAd;
    boolean shouldShowAd;
    boolean canShowAd;
    boolean mayShowAd;
    boolean isFinished = false;
    boolean wentTapjoy = false;
    boolean wentShop = false;
    LevelTable levelTable;
    Store mStore;
    GameListener gameListener;

    Game game;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.getFont(this);

        Level level = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(LEVEL_PARAM_TAG))
            level = (Level) savedInstanceState.getSerializable(LEVEL_PARAM_TAG);
        Intent intent = getIntent();
        if (intent.hasExtra(LEVEL_PARAM_TAG))
            level = (Level) intent.getSerializableExtra(LEVEL_PARAM_TAG);

        if (level == null || level.pack == null) {
            finish();
            return;
        }

        gameListener = new GameListener(UserPreferences.getInstance(getApplicationContext()));
        game = new Game(level, gameListener);

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
            adWhirlLayout.setAdShowListener(adShowListener);
        }

        setContentView(rootLayout);

        isShowingAd = false;
        shouldShowAd = false;
        canShowAd = false;
        levelTable = new LevelTable(this);
        levelTable.open(false);

        if (Settings.GoogleInAppEnabled)
            mStore = GInAppStore.getInstance(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LEVEL_PARAM_TAG, game.getLevel());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ((SnappersApplication)getApplication()).setSwitchingActivities();
        ((SnappersApplication)getApplication()).activityPaused();
        if (isFinishing()) {
            if (adWhirlLayout != null){
                adWhirlLayout.setAdShowListener(null);
                MyAdWhirlLayout.setEnforceUpdate(false);
                adWhirlLayout.setVisibility(View.GONE);
            }
            isFinished = true;
            levelTable.close();
        }
        if (wentShop || wentTapjoy)
            Resources.preloadResourcesInWorker(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mayShowAd = !UserPreferences.getInstance(this).isAdFree();
        if (!mayShowAd && isShowingAd){
            gameListener.hideAd();
            if (game.initDone)
                game.setAdHeight(0);
        }
        ((SnappersApplication)getApplication()).activityResumed(this);

        if (wentTapjoy){
            TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyPointsListener(getApplicationContext()));
            wentTapjoy = false;
        }
        wentShop = false;
    }

    @Override
    public void onBackPressed() {
        if (game.initDone)
            game.backButtonPressed();
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

    class GameListener implements IAppGameListener {
        UserPreferences prefs;

        GameListener(UserPreferences prefs) {
            this.prefs = prefs;
        }

        @Override
        public void launchStore() {
            Intent intent = new Intent(GameActivity.this, StoreActivity.class);
            startActivity(intent);
        }

        @Override
        public void levelPackWon(LevelPack pack) {
            UserPreferences.getInstance(getApplicationContext()).unlockNextLevelPack(pack);
            setResult(1);
            finish();
        }

        @Override
        public int getHintsLeft() {
            return UserPreferences.getInstance(getApplicationContext()).getHintsRemaining();
        }

        @Override
        public void useHint() {
            UserPreferences.getInstance(getApplicationContext()).useHint();
        }

        @Override
        public void buy(Goods goods) {
            if (mStore != null){
                wentShop = true;
                mStore.buy(goods);
            }
        }

        @Override
        public boolean isOnline() {
            //TODO:
            return checkNetworkStatus();
        }

        @Override
        public void levelSolved(Level level) {
            UserPreferences.getInstance(getApplicationContext()).unlockNextLevel(level);
        }

        @Override
        public boolean isLevelSolved(Level level) {
            return UserPreferences.getInstance(getApplicationContext()).isLevelSolved(level);
        }

        @Override
        public boolean isSoundEnabled() {
            return UserPreferences.getInstance(getApplicationContext()).getSound();
        }

        @Override
        public void addScore(int score) {
            UserPreferences.getInstance(getApplicationContext()).addScore(score);
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
        public int getAdHeight() {
            return mayShowAd && adWhirlLayout.isAdAvailable() ? adWhirlLayout.getHeight() : 0;
        }

        @Override
        public void gotScreenSize(int width, int height) {
            Metrics.setSize(width, height, GameActivity.this);
        }

        @Override
        public void freeHintsPressed() {
            wentTapjoy = true;
            TapjoyConnect.getTapjoyConnectInstance().showOffers();
        }

        @Override
        public Level getNextLevel(Level currentLevel) {
            return levelTable.getNextLevel(currentLevel);
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



    IOnAdShowListener adShowListener = new IOnAdShowListener() {
        @Override
        public void onAdShow() {
            if (isFinished)
                return;
            canShowAd = true;
            if (shouldShowAd) {
                gameListener.showAd();
                if (game.initDone)
                    game.setAdHeight(adWhirlLayout.getHeight());
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
        public void onAdFail() {
            if (isFinished)
                return;
            canShowAd = false;
            if (isShowingAd) {
                gameListener.hideAd();
                if (game.initDone)
                    game.setAdHeight(0);
            }
        }
    };

}