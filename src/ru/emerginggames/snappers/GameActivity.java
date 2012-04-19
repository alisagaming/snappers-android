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
public class GameActivity extends AndroidApplication {
    public static final String LEVEL_PARAM_TAG = "Level";

    RelativeLayout rootLayout;

    boolean isFinished = false;
    boolean wentTapjoy = false;
    boolean wentShop = false;
    LevelTable levelTable;
    Store mStore;
    GameListener gameListener;
    AdController adController;
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
            adController = new AdController();
            rootLayout.addView(adController.getAdLayout());
        }

        setContentView(rootLayout);

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
            ((SnappersApplication) getApplication()).setSwitchingActivities();
        ((SnappersApplication) getApplication()).activityPaused();
        if (isFinishing()) {
            if (adController != null)
                adController.finish();
            isFinished = true;
            levelTable.close();
        }
        if (wentShop || wentTapjoy)
            Resources.preloadResourcesInWorker(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adController != null && UserPreferences.getInstance(this).isAdFree()) {
            adController.finish();
            rootLayout.removeView(adController.getAdLayout());
            adController = null;
        }
        ((SnappersApplication) getApplication()).activityResumed(this);

        if (wentTapjoy) {
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
        if (checkNetworkType(conMgr, ConnectivityManager.TYPE_MOBILE))
            return true;

        if (checkNetworkType(conMgr, ConnectivityManager.TYPE_WIFI))
            return true;

        try {
            if (checkNetworkType(conMgr, ConnectivityManager.TYPE_WIMAX))
                return true;
            if (checkNetworkType(conMgr, ConnectivityManager.TYPE_ETHERNET))
                return true;
            if (checkNetworkType(conMgr, ConnectivityManager.TYPE_BLUETOOTH))
                return true;

        } catch (Exception e) {
        }

        return false;
    }

    private boolean checkNetworkType(ConnectivityManager conMgr, int type) {
        NetworkInfo netInfo = conMgr.getNetworkInfo(type);
        return netInfo != null && netInfo.isAvailable();
    }

    class GameListener implements IAppGameListener {
        UserPreferences prefs;

        boolean hintsTouched;

        GameListener(UserPreferences prefs) {
            this.prefs = prefs;
            hintsTouched = prefs.areHintsTouched();

        }

        @Override
        public boolean isTapjoyEnabled() {
            return prefs.isTapjoyEnabled();
        }

        @Override
        public void launchStore() {
            Intent intent = new Intent(GameActivity.this, StoreActivity.class);
            startActivity(intent);
        }

        @Override
        public void levelPackWon(LevelPack pack) {
            prefs.unlockNextLevelPack(pack);
            setResult(1);
            finish();
        }

        @Override
        public int getHintsLeft() {
            return prefs.getHintsRemaining();
        }

        @Override
        public void useHint() {
            prefs.useHint();
            if (!hintsTouched) {
                hintsTouched = true;
                prefs.touchHints();
            }
        }

        @Override
        public void buy(Goods goods) {
            if (mStore != null) {
                wentShop = true;
                mStore.buy(goods);
            }
        }

        @Override
        public boolean isOnline() {
            return checkNetworkStatus();
        }

        @Override
        public void levelSolved(Level level) {
            prefs.unlockNextLevel(level);
        }

        @Override
        public boolean isLevelSolved(Level level) {
            return prefs.isLevelSolved(level);
        }

        @Override
        public boolean isSoundEnabled() {
            return prefs.getSound();
        }

        @Override
        public void addScore(int score) {
            prefs.addScore(score);
        }

        @Override
        public void gameoverStageShown() {
            if (adController != null)
                adController.showAdTop();
        }

        @Override
        public void gameoverStageHidden() {
            if (adController != null)
                adController.hideAdTop();
        }

        @Override
        public int getAdHeight() {
            return adController == null ? 0 : adController.getAdHeight();
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

        @Override
        public void onInitDone() {
            if (adController != null)
                adController.setGameMargins(0);
        }
    }

    class AdController implements IOnAdShowListener {
        MyAdWhirlLayout adWhirlLayout;
        public RelativeLayout.LayoutParams lpUp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        public RelativeLayout.LayoutParams lpDown = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean isShowingAd = false;
        boolean shouldShowAdTop = false;
        boolean canShowAd = false;
        boolean shouldShowIngameAd;
        boolean canShowIngameAd = true;
        UserPreferences prefs;


        public AdController() {
            prefs = UserPreferences.getInstance(getApplicationContext());
            lpUp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lpUp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpDown.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lpDown.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            shouldShowIngameAd = prefs.getIngameAds();

            AdWhirlTargeting.setKeywords("game puzzle");
            AdWhirlAdapter.setGoogleAdSenseExpandDirection("UP");
            if (Settings.DEBUG)
                AdWhirlTargeting.setTestMode(true);

            adWhirlLayout = new MyAdWhirlLayout(GameActivity.this, Settings.getAdwhirlKey(GameActivity.this));
            adWhirlLayout.setLayoutParams(shouldShowIngameAd ? lpDown : lpUp);

            if (!shouldShowIngameAd) {
                adWhirlLayout.setVisibility(View.INVISIBLE);
                MyAdWhirlLayout.setEnforceUpdate(true);
            }
            adWhirlLayout.setAdShowListener(this);
        }

        public MyAdWhirlLayout getAdLayout() {
            return adWhirlLayout;
        }

        public int getAdHeight() {
            return adWhirlLayout.isAdAvailable() ? adWhirlLayout.getHeight() : 0;
        }

        public void finish() {
            if (!isFinishing())
                game.setTopAdHeight(0);
            adWhirlLayout.setAdShowListener(null);
            MyAdWhirlLayout.setEnforceUpdate(false);
            adWhirlLayout.setVisibility(View.GONE);
        }

        public void showAdTop() {
            shouldShowAdTop = true;

            if (!canShowAd)
                return;
            if (shouldShowIngameAd)
                runOnUiThread(moveAdTop);
            if (!isShowingAd)
                runOnUiThread(showAD);
        }

        public void hideAdTop() {
            shouldShowAdTop = false;
            if (shouldShowIngameAd)
                runOnUiThread(moveAdBottom);
            else
                runOnUiThread(hideAD);
        }

        public void setGameMargins(int height){
            if (height == 0)
                height = adWhirlLayout.getHeight();

            if (shouldShowIngameAd) {
                if (height < game.getMarginBottom())
                    canShowIngameAd = true;
                else if (height < game.getMaxMarginBottom()) {
                    canShowIngameAd = true;
                    game.resizeMarginBottom(height);
                }
                else canShowIngameAd = false;
                if (!canShowIngameAd)
                    runOnUiThread(hideAD);
            }
            if (canShowAd)
                game.setTopAdHeight(height);
        }

        @Override
        public void onAdShow() {
            if (isFinished)
                return;
            canShowAd = true;
            if (shouldShowAdTop) {
                showAdTop();
                if (game.initDone)
                    game.setTopAdHeight(adWhirlLayout.getHeight());
            } else if (shouldShowIngameAd && canShowIngameAd) {
                runOnUiThread(moveAdBottom);
                runOnUiThread(showAD);
            }
        }

        @Override
        public void onAdSizeChanged(int width, int height) {
            if (game.initDone && ! isFinished)
                setGameMargins(height);
        }

        @Override
        public void onAdFail() {
            if (isFinished)
                return;
            canShowAd = false;
            if (isShowingAd) {
                runOnUiThread(hideAD);
                if (game.initDone)
                    game.setTopAdHeight(0);
            }
        }

        Runnable showAD = new Runnable() {
            @Override
            public void run() {
                if (isShowingAd)
                    return;

                adWhirlLayout.setVisibility(View.VISIBLE);
/*                if (adWhirlLayout.getChildCount() > 0) {
                    View v = adWhirlLayout.getChildAt(0);
                    adWhirlLayout.removeView(v);
                    adWhirlLayout.addView(v);
                }*/
                rootLayout.removeView(adWhirlLayout);
                rootLayout.addView(adWhirlLayout);
                isShowingAd = true;
            }
        };

        Runnable hideAD = new Runnable() {
            @Override
            public void run() {
                if (!isShowingAd)
                    return;

                adWhirlLayout.setVisibility(View.INVISIBLE);
                isShowingAd = false;
                MyAdWhirlLayout.setEnforceUpdate(true);
            }
        };

        Runnable moveAdBottom = new Runnable() {
            @Override
            public void run() {
                adWhirlLayout.setLayoutParams(lpDown);
                AdWhirlAdapter.setGoogleAdSenseExpandDirection("UP");
            }
        };

        Runnable moveAdTop = new Runnable() {
            @Override
            public void run() {
                adWhirlLayout.setLayoutParams(lpUp);
                AdWhirlAdapter.setGoogleAdSenseExpandDirection("BOTTOM");
            }
        };
    }
}