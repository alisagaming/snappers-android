package com.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emerginggames.snappers.model.Goods;
import com.emerginggames.snappers.utils.AmazonStore;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.utils.Store;
import com.emerginggames.snappers.utils.TapjoyPointsListener;
import com.emerginggames.snappers.view.*;
import com.flurry.android.FlurryAgent;
import com.tapjoy.TapjoyConnect;
import com.emerginggames.snappers.data.LevelTable;
import com.emerginggames.snappers.gdx.Game;
import com.emerginggames.snappers.gdx.IAppGameListener;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.Level;
import com.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:23
 */
public class GameActivity extends AndroidApplication {
    public static final String LEVEL_PARAM_TAG = "Level";

    RelativeLayout rootLayout;

    public boolean isFinished = false;
    public boolean wentTemp = false;
    boolean wentShop = false;
    LevelTable levelTable;
    Store mStore;
    GameListener gameListener;
    AdController adController;
    Game game;

    UserPreferences prefs;
    TopButtonController topButtons;
    GameOverMessageController gameOverMessageController;
    LevelInfo levelInfo;
    //View helpView;
    TextView pleaseWaitText;
    View gameView;
    Level startLevel;
    GameDialogController dialogs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.getFont(this);
        prefs = UserPreferences.getInstance(getApplicationContext());
        prefs.setHintChangedListener(hintChangedListener);

        startLevel = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(LEVEL_PARAM_TAG))
            startLevel = (Level) savedInstanceState.getSerializable(LEVEL_PARAM_TAG);
        Intent intent = getIntent();
        if (startLevel == null && intent.hasExtra(LEVEL_PARAM_TAG))
            startLevel = (Level) intent.getSerializableExtra(LEVEL_PARAM_TAG);

        if (startLevel == null || startLevel.pack == null) {
            finish();
            return;
        }

        gameListener = new GameListener();
        game = new Game(startLevel, gameListener);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        Metrics.setSizeByView(getWindow().getDecorView(), getApplicationContext());

        rootLayout = new RelativeLayout(this);

        Rect rect = new Rect();
        rootLayout.getWindowVisibleDisplayFrame(rect);
        if (rect.width() > 0)
            Metrics.setSize(rect.width(), rect.height(), getApplicationContext());


        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = false;
        gameView = initializeForView(game, config);
        rootLayout.addView(gameView);

        addPleaseWait();

        levelInfo = new LevelInfo(rootLayout);
        topButtons = new TopButtonController(rootLayout, this, game);
        topButtons.showMainButtons();
        gameOverMessageController = new GameOverMessageController();

        if (!prefs.isAdFree())
            adController = new AdController(this, game, rootLayout);

        setContentView(rootLayout);

        levelTable = new LevelTable(this);
        levelTable.open(false);

        if (Settings.IS_AMAZON)
            mStore = AmazonStore.getInstance(getApplicationContext());
        else
            mStore = GInAppStore.getInstance(getApplicationContext());

        if (prefs.isTapjoyEnabled() && TapjoyConnect.getTapjoyConnectInstance() == null)
            TapjoyConnect.requestTapjoyConnect(getApplicationContext(), Settings.getTapJoyAppId(getApplicationContext()), Settings.getTapJoySecretKey(getApplicationContext()));
        dialogs = new GameDialogController(this, game);
    }

    public void buy(Goods goods) {
        if (mStore != null) {
            wentTemp = true;
            mStore.buy(goods);
        }
    }

    void addPleaseWait(){
        pleaseWaitText = new TextView(this);
        pleaseWaitText.setText(R.string.pleaseWait);
        pleaseWaitText.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
        pleaseWaitText.setTextColor(Color.WHITE);
        pleaseWaitText.setTypeface(Resources.getFont(this));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rootLayout.addView(pleaseWaitText, lp);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LEVEL_PARAM_TAG, game == null ? startLevel : game.getLevel());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ((SnappersApplication) getApplication()).setSwitchingActivities();
        ((SnappersApplication) getApplication()).activityPaused();
        if (isFinishing()) {
            isFinished = true;
            if (adController != null)
                adController.finish();

            levelTable.close();
            prefs.setHintChangedListener(null);
        }
        if (wentTemp)
            Resources.preloadResourcesInWorker(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adController!= null)
            adController.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adController != null && prefs.isAdFree()) {
            adController.finish();
            adController.destroy();
            adController = null;
        }


        if (TapjoyConnect.getTapjoyConnectInstance() == null)
            TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyPointsListener(getApplicationContext()));

        wentTemp = false;

        ((SnappersApplication) getApplication()).activityResumed(this);
    }

    @Override
    public void onBackPressed() {
        if (game.initDone)
            game.backButtonPressed();
    }

    public boolean checkNetworkStatus() {
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

    public TopButtonController getTopButtons(){
        return topButtons;
    }

    public AdController getAdController(){
        return adController;
    }

    private boolean checkNetworkType(ConnectivityManager conMgr, int type) {
        NetworkInfo netInfo = conMgr.getNetworkInfo(type);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void launchStore() {
        Intent intent = new Intent(GameActivity.this, StoreActivity.class);
        startActivity(intent);
    }

    class GameListener implements IAppGameListener {

        @Override
        public void showPaused() {
            showPausedDialog();
        }

        @Override
        public void showHintMenu() {
            dialogs.showHintMenu();
        }

        @Override
        public void levelPackWon(LevelPack pack) {
            setResult(1);
            finish();
        }

        @Override
        public void levelSolved(Level level, int score) {
            if ((level.number % 50 == 0 || level.number==34) && prefs.getLevelUnlocked(level.pack) == level.number){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppRater.showRateDialog(GameActivity.this);
                    }
                });
            }
            prefs.unlockNextLevel(level);
            topButtons.showGameWonMenu();
            if (adController != null)
                adController.showAdTop();
            prefs.addScore(score);
            gameOverMessageController.show(true, score);
            Level next = getNextLevel(level);
            if (next == null)
                prefs.unlockNextLevelPack(level.pack);
            levelInfo.setDim(true);
            levelInfo.hideText();
            topButtons.hideHelpIfNeeded();

            if (!prefs.isAdFree() &&(level.number > 5 || level.packNumber >1) && prefs.getMoreGameFreq() > Math.random())
                dialogs.showFreeGamesBanner();
        }

        @Override
        public boolean isLevelSolved(Level level) {
            return prefs.isLevelSolved(level);
        }

        @Override
        public void showGameLost(Level level) {
            topButtons.showGameLostMenu();
            if (adController != null)
                adController.showAdTop();
            gameOverMessageController.show(false, level.tapsCount);
            levelInfo.hideText();
            topButtons.hideHelpIfNeeded();
        }

        @Override
        public void hideGameOverMenu() {
            if (adController != null)
                adController.hideAdTop();
            topButtons.showMainButtons();
            gameOverMessageController.hide();
            levelInfo.setDim(false);
            levelInfo.showText();
        }

        @Override
        public boolean isSoundEnabled() {
            return prefs.getSound();
        }

        @Override
        public void gotScreenSize(int width, int height) {
            Metrics.setSize(width, height, GameActivity.this);
        }

        @Override
        public Level getNextLevel(Level currentLevel) {
            return levelTable.getNextLevel(currentLevel);
        }

        @Override
        public void onInitDone() {
            if (adController != null)
                adController.setGameMargins(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pleaseWaitText.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void updateLevelInfo(Level level) {
            levelInfo.setLevel(level);
        }

        @Override
        public void updateTapsLeft(int n) {
            levelInfo.setTapsLeft(n);
        }
    }

    UserPreferences.HintChangedListener hintChangedListener = new UserPreferences.HintChangedListener() {
        @Override
        public void onHintsChanged(int old, int current) {
            if (game.getStage() == Game.Stages.HintMenu)
                dialogs.showHintMenu();
        }
    };



    class GameOverMessageController {
        private static final int WIN_TITLES = 7;
        LinearLayout layout;
        OutlinedTextView title;
        OutlinedTextView message;
        boolean isWon;
        int msgValue;


        public void show(boolean isWon, int msgValue){
            this.isWon = isWon;
            this.msgValue = msgValue;
            runOnUiThread(show);
        }

        public void hide(){
            runOnUiThread(hide);
        }

        int getWinTitleId(){
            int n = (int)(Math.random()*WIN_TITLES) + 1;
            String resourceName = String.format("game_won_%d", n);
            return getResources().getIdentifier(resourceName, "string", getPackageName());
        }

        int getLostTitleId(){
            return R.string.game_lost_1;
        }

        Runnable show = new Runnable() {
            @Override
            public void run() {
                if (layout == null){
                    layout = (LinearLayout)getLayoutInflater().inflate(R.layout.partial_level_result, null);
                    title = (OutlinedTextView)layout.findViewById(R.id.title);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
                    title.setTypeface(Resources.getFont(GameActivity.this));
                    message = (OutlinedTextView)layout.findViewById(R.id.message);
                    message.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
                    message.setTypeface(Resources.getFont(GameActivity.this));
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)title.getLayoutParams();
                    lp.bottomMargin = - Metrics.largeFontSize / 3;
                    title.setLayoutParams(lp);
                }

                int titleId = isWon ? getWinTitleId() : getLostTitleId();
                title.setText2(titleId);
                String msg;
                if (isWon)
                    msg = getResources().getString(R.string.score, msgValue);
                else if (msgValue == 1)
                    msg = getResources().getString(R.string.possible_in_1_touch);
                else
                    msg = getResources().getString(R.string.possible_in_touches, msgValue);
                message.setText2(msg);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lp.addRule(RelativeLayout.ALIGN_LEFT);
                lp.bottomMargin = Metrics.screenHeight / 2;
                if (rootLayout.indexOfChild(layout) >= 0)
                    rootLayout.removeView(layout);
                rootLayout.addView(layout, lp);
            }
        };

       Runnable hide = new Runnable() {
           @Override
           public void run() {
               rootLayout.removeView(layout);
           }
       };
    }

    public class LevelInfo{
        OutlinedTextView levelInfo;
        OutlinedTextView tapsLeft;
        Level level;
        int tapsLeftN;
        int color;

        LevelInfo(RelativeLayout rootLayout) {
            LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.partial_game_info, null);
            levelInfo = (OutlinedTextView)layout.findViewById(R.id.levelInfo);
            tapsLeft = (OutlinedTextView)layout.findViewById(R.id.tapsLeft);

            levelInfo.setTypeface(Resources.getFont(GameActivity.this));
            tapsLeft.setTypeface(Resources.getFont(GameActivity.this));
            levelInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
            tapsLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)levelInfo.getLayoutParams();
            levelInfo.setLayoutParams(lp);


            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlp.leftMargin = rlp.topMargin = Metrics.screenMargin;
            rootLayout.addView(layout, rlp);
        }

        public void setLevel(Level level){
            this.level = level;
            runOnUiThread(setLevel);
        }

        public void setTapsLeft(int n){
            tapsLeftN = n;
            runOnUiThread(setTapsLeft);
        }

        public void setDim(boolean isDim){
            color = isDim ? Color.rgb(128, 128, 128) : Color.rgb(255, 255, 255);
            runOnUiThread(setColor);
        }

        public void showText(){
            runOnUiThread(showText);
        }

        public void hideText(){
            runOnUiThread(hideText);
        }

        Runnable setLevel = new Runnable() {
            @Override
            public void run() {
                levelInfo.setText2(getResources().getString(R.string.level_n, level.pack.id, level.number));
                level = null;
            }
        };

        Runnable setTapsLeft = new Runnable() {
            @Override
            public void run() {
                tapsLeft.setText2(getResources().getString(R.string.taps_left, tapsLeftN));
            }
        };

        Runnable setColor = new Runnable() {
            @Override
            public void run() {
                levelInfo.setTextColor(color);
                tapsLeft.setTextColor(color);
            }
        };

        Runnable hideText = new Runnable() {
            @Override
            public void run() {
                levelInfo.setVisibility(View.INVISIBLE);
                tapsLeft.setVisibility(View.INVISIBLE);
            }
        };

        Runnable showText = new Runnable() {
            @Override
            public void run() {
                levelInfo.setVisibility(View.VISIBLE);
                tapsLeft.setVisibility(View.VISIBLE);
            }
        };
    }

    public void showPausedDialog(){
        dialogs.showPaused();
        game.setPaused(true);
    }

    public void showHintMenu(){
        dialogs.showHintMenu();
    }

    public LevelInfo getLevelInfo(){
        return levelInfo;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Settings.FLURRY_APP_KEY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}