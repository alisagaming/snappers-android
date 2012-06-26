package com.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emerginggames.snappers.gdx.Game;
import com.emerginggames.snappers.gdx.IAppGameListener;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.SyncData;
import com.emerginggames.snappers.transport.FacebookTransport;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.utils.Store;
import com.emerginggames.snappers.utils.TapjoyPointsListener;
import com.emerginggames.snappers.utils.Utils;
import com.emrg.view.*;
import com.tapjoy.TapjoyConnect;
import com.emerginggames.snappers.data.LevelTable;
import com.emerginggames.snappers.model.Level;
import com.emerginggames.snappers.model.LevelPack;


public class GameActivity extends AndroidApplication {
    public static final String LEVEL_PARAM_TAG = "Level";

    RelativeLayout rootLayout;

    public boolean isFinished = false;
    public boolean wentTapjoy = false;
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

    int currentLevel;
    LevelPack pack;
    GameDialogsController gameDialogsController;
    View helpView;
    Level startLevel;
    FacebookTransport facebookTransport;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.getFont(this);
        prefs = UserPreferences.getInstance(getApplicationContext());
        prefs.setHintChangedListener(hintChangedListener);

        startLevel = null;

        Intent intent = getIntent();
        if (intent.hasExtra(LEVEL_PARAM_TAG))
            startLevel = (Level) intent.getSerializableExtra(LEVEL_PARAM_TAG);

        if (savedInstanceState != null && savedInstanceState.containsKey(LEVEL_PARAM_TAG))
            startLevel = (Level) savedInstanceState.getSerializable(LEVEL_PARAM_TAG);

        if (startLevel == null || startLevel.pack == null) {
            finish();
            return;
        }

        pack = startLevel.pack;

        gameListener = new GameListener();
        game = new Game(startLevel, gameListener);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        rootLayout = new RelativeLayout(this);
        Rect rect = new Rect();
        rootLayout.getWindowVisibleDisplayFrame(rect);
        Metrics.setSize(rect.width(), rect.height(), getApplicationContext());

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = false;

        View gameView = initializeForView(game, config);
        rootLayout.addView(gameView);

        setContentView(rootLayout);

        levelTable = new LevelTable(this);
        levelTable.open(false);

        if (Settings.GoogleInAppEnabled)
            mStore = GInAppStore.getInstance(getApplicationContext());
        currentLevel = Settings.getLevel(prefs.getScore());

        gameDialogsController = new GameDialogsController(this);

        if (TapjoyConnect.getTapjoyConnectInstance() == null)
            TapjoyConnect.requestTapjoyConnect(getApplicationContext(), Settings.getTapJoyAppId(getApplicationContext()), Settings.getTapJoySecretKey(getApplicationContext()));

        facebookTransport = new FacebookTransport(this);
        if (!facebookTransport.isLoggedIn())
            facebookTransport = null;
    }

    public void initViews(){
        if (topButtons == null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameOverMessageController = new GameOverMessageController(rootLayout, GameActivity.this);
                    levelInfo = new LevelInfo(rootLayout);
                    topButtons = new TopButtonController(rootLayout, GameActivity.this, game);
                    topButtons.showMainButtons();

                    if (!Settings.IS_PREMIUM && !prefs.isAdFree()) {
                        adController = new AdController(GameActivity.this, rootLayout);
                    }
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LEVEL_PARAM_TAG, game == null || !game.initDone ? startLevel : game.getLevel());
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
            prefs.setHintChangedListener(null);
            gameDialogsController.dismiss();
        }
        if (wentShop || wentTapjoy)
            Resources.preloadResourcesInWorker(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adController != null && prefs.isAdFree()) {
            adController.finish();
            adController = null;
        }


        if (wentTapjoy) {
            TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyPointsListener(getApplicationContext()));
            wentTapjoy = false;
        }
        wentShop = false;

        ((SnappersApplication) getApplication()).activityResumed(this, pack.soundtrack);
    }

    @Override
    public void onBackPressed() {
        if (game.initDone)
            game.backButtonPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU & game.initDone)
            pauseGame();

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adController != null)
            adController.destroy();
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

    public void pauseGame(){
        game.setPaused(true);
        gameDialogsController.showPauseDialog();
    }

    public void showHintMenu(){
        gameDialogsController.showHintMenu();
    }

    private boolean checkNetworkType(ConnectivityManager conMgr, int type) {
        NetworkInfo netInfo = conMgr.getNetworkInfo(type);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showHelp(){
        helpView = getLayoutInflater().inflate(R.layout.partial_help, null);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeView(v);
                game.setStage(Game.Stages.GameOverStage);
                levelInfo.show();
                SoundManager.getInstance(GameActivity.this).playButtonSound();
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rootLayout.addView(helpView, lp);
        game.setStage(Game.Stages.HelpStage);
        topButtons.hideAll();
        levelInfo.hide();
    }

    void syncFacebook(){
        facebookTransport.sync(new FacebookTransport.ResponseListener(){
            @Override
            public void onOk(Object data) {
                SyncData sync = (SyncData) data;
                if (data == null || sync.gifts == null || sync.gifts.length == 0)
                    return;

                GameDialog dialog = new GameDialog(GameActivity.this, Metrics.screenWidth * 95 / 100);
                dialog.setMessage(Utils.getGiftsMessage(GameActivity.this, sync), Metrics.fontSize);
                dialog.addOkButton();
                dialog.show();
            }
        });
    }



    class GameListener implements IAppGameListener {

        @Override
        public void showPaused() {
            pauseGame();
        }

        @Override
        public void showHintMenu() {
            gameDialogsController.showHintMenu();
        }

        @Override
        public void levelPackWon(LevelPack pack) {
            setResult(1);
            finish();
        }

        @Override
        public void levelSolved(Level level, final int score) {
            prefs.unlockNextLevel(level);
            topButtons.showGameWonMenu();
            if (adController != null)
                adController.showAdTop();
            prefs.addScore(score);
            gameOverMessageController.show(true, score);
            Level next = getNextLevel(level);
            if (next == null)
                prefs.unlockNextLevelPack(level.pack);
            levelInfo.hide();

            topButtons.showScore();

            int newLevel = Settings.getLevel(prefs.getScore());
            if (newLevel > currentLevel){
                currentLevel = newLevel;
                gameDialogsController.showNewLevelDialog(newLevel);
            }
            if (helpView != null){
                    rootLayout.removeView(helpView);
                    helpView = null;
            }

            if (facebookTransport != null)
                syncFacebook();
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
            levelInfo.hide();
            if (helpView != null){
                rootLayout.removeView(helpView);
                helpView = null;
            }
        }

        @Override
        public void hideGameOverMenu() {
            if (adController != null)
                adController.hideAdTop();
            topButtons.showMainButtons();
            gameOverMessageController.hide();
            levelInfo.show();
            topButtons.hideScore();
        }

        @Override
        public boolean isSoundEnabled() {
            return prefs.getSound();
        }

        @Override
        public void gotScreenSize(int width, int height) {
            Metrics.setSize(width, height, GameActivity.this);
            initViews();
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
                gameDialogsController.showHintMenu();
            topButtons.updateHints();
        }
    };

    public AdController getAdController(){
        return adController;
    }

    public GameOverMessageController getGameOverMessageController() {
        return gameOverMessageController;
    }

    public Game getGame() {
        return game;
    }

    public TopButtonController getTopButtons() {
        return topButtons;
    }




    class LevelInfo{
        OutlinedTextView levelInfo;
        OutlinedTextView tapsLeft;
        Level level;
        int tapsLeftN;
        int color;
        LinearLayout layout;

        LevelInfo(RelativeLayout rootLayout) {
             layout = (LinearLayout)getLayoutInflater().inflate(R.layout.partial_game_info, null);
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

        public void show(){
            runOnUiThread(show);
        }

        public void hide(){
            runOnUiThread(hide);
        }

        Runnable setLevel = new Runnable() {
            @Override
            public void run() {
                levelInfo.setText2(getString(R.string.level_n, level.pack.id, level.number));
                level = null;
            }
        };

        Runnable setTapsLeft = new Runnable() {
            @Override
            public void run() {
                tapsLeft.setText2(getString(R.string.taps_left, tapsLeftN));
            }
        };

        Runnable setColor = new Runnable() {
            @Override
            public void run() {
                levelInfo.setTextColor(color);
                tapsLeft.setTextColor(color);
            }
        };

        Runnable hide = new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.GONE);
            }
        };

        Runnable show = new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
            }
        };
    }
}