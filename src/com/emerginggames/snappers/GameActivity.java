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
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emerginggames.snappers.model.Goods;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.utils.Store;
import com.emerginggames.snappers.utils.TapjoyPointsListener;
import com.emerginggames.snappers.view.*;
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
    boolean wentTapjoy = false;
    boolean wentShop = false;
    LevelTable levelTable;
    Store mStore;
    GameListener gameListener;
    AdController adController;
    Game game;
    GameDialog dlg;
    UserPreferences prefs;
    TopButtonController topButtons;
    GameOverMessageController gameOverMessageController;
    LevelInfo levelInfo;
    View helpView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.getFont(this);
        prefs = UserPreferences.getInstance(getApplicationContext());
        prefs.setHintChangedListener(hintChangedListener);

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

        gameListener = new GameListener();
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

        Rect rect = new Rect();
        rootLayout.getWindowVisibleDisplayFrame(rect);
        Metrics.setSize(rect.width(), rect.height(), getApplicationContext());

        levelInfo = new LevelInfo(rootLayout);
        topButtons = new TopButtonController(rootLayout);
        topButtons.showMainButtons();
        gameOverMessageController = new GameOverMessageController();

        if (!prefs.isAdFree())
            adController = new AdController(this, game, rootLayout);

        setContentView(rootLayout);

        levelTable = new LevelTable(this);
        levelTable.open(false);

        if (Settings.GoogleInAppEnabled)
            mStore = GInAppStore.getInstance(getApplicationContext());

        if (TapjoyConnect.getTapjoyConnectInstance() == null)
            TapjoyConnect.requestTapjoyConnect(getApplicationContext(), Settings.getTapJoyAppId(getApplicationContext()), Settings.getTapJoySecretKey(getApplicationContext()));
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
            isFinished = true;
            if (adController != null)
                adController.finish();

            levelTable.close();
            prefs.setHintChangedListener(null);
        }
        if (wentShop || wentTapjoy)
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


        if (wentTapjoy) {
            TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyPointsListener(getApplicationContext()));
            wentTapjoy = false;
        }
        wentShop = false;

        ((SnappersApplication) getApplication()).activityResumed(this);
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

    public TopButtonController getTopButtons(){
        return topButtons;
    }

    private boolean checkNetworkType(ConnectivityManager conMgr, int type) {
        NetworkInfo netInfo = conMgr.getNetworkInfo(type);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void launchStore() {
        Intent intent = new Intent(GameActivity.this, StoreActivity.class);
        startActivity(intent);
    }

    void showHelp(){
        helpView = getLayoutInflater().inflate(R.layout.partial_help, null);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeView(v);
                game.setStage(Game.Stages.GameOverStage);
                helpView = null;
                SoundManager.getInstance(GameActivity.this).playButtonSound();
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rootLayout.addView(helpView, lp);
        game.setStage(Game.Stages.HelpStage);
        topButtons.hideAll();
        levelInfo.hideText();
    }

    Runnable showPausedDialog = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else
                dlg.clear();
            dlg.setTitle(R.string.game_paused);
            dlg.addButton(R.drawable.resumelong, R.drawable.resumelong_tap);
            dlg.addButton(R.drawable.restartlong, R.drawable.restartlong_tap);
            dlg.addButton(R.drawable.menulong, R.drawable.menulong_tap);
            dlg.addButton(R.drawable.storelong, R.drawable.storelong_tap);
            dlg.show();
        }
    };

    Runnable showHintMenu = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else{
                if (dlg.isShowing())
                    dlg.hide();
                dlg.clear();
            }

            int hintsLeft = prefs.getHintsRemaining();
            if (hintsLeft > 0)
                fillUseHintMenu(hintsLeft);
            else if (checkNetworkStatus())
                showBuyHintMenu();
            else
                showGetOnlineMenu();
            dlg.show();
        }

        void fillUseHintMenu(int hintsLeft){
            if (hintsLeft == 1)
                dlg.setMessage(R.string.youHaveOneHint, Metrics.fontSize);
            else
                dlg.setMessage(getResources().getString(R.string.youHave_n_Hints, hintsLeft), Metrics.fontSize);

            dlg.addButton(R.drawable.useahintlong, R.drawable.useahintlong_tap);
            if (prefs.isTapjoyEnabled())
                dlg.addButton(R.drawable.freehintslong, R.drawable.freehintslong_tap);
            dlg.addButton(R.drawable.cancellong, R.drawable.cancellong_tap);

        }

        void showBuyHintMenu(){
            android.content.res.Resources res = getResources();
            StringBuilder msg = new StringBuilder();
            msg.append(res.getString(R.string.youHaveNoHints)).append("\n").append(res.getString(R.string.buySome));
            dlg.setMessage(msg, Metrics.fontSize);
            dlg.addButton(R.drawable.buy1hint, R.drawable.buy1hint_tap);
            dlg.addButton(R.drawable.buyhintslong, R.drawable.buyhintslong_tap);
            if (prefs.isTapjoyEnabled())
                dlg.addButton(R.drawable.freehintslong, R.drawable.freehintslong_tap);
            dlg.addButton(R.drawable.cancellong, R.drawable.cancellong_tap);
        }

        void showGetOnlineMenu(){
            android.content.res.Resources res = getResources();
            StringBuilder msg = new StringBuilder();
            msg.append(res.getString(R.string.youHaveNoHints)).append("\n").append(res.getString(R.string.getOnline));
            dlg.setMessage(msg, Metrics.fontSize);
            dlg.addButton(R.drawable.cancellong, R.drawable.cancellong_tap);
        }
    };

    void initDialog(){
        dlg = new GameDialog(GameActivity.this);
        dlg.setWidth(Math.min(Metrics.menuWidth, Metrics.screenWidth * 9 / 10));
        dlg.setBtnClickListener(dialogButtonListener);
        dlg.setItemSpacing(Metrics.screenMargin);
        dlg.setTypeface(Resources.getFont(this));
    }

    GameDialog.OnDialogEventListener dialogButtonListener = new GameDialog.OnDialogEventListener() {
        @Override
        public void onButtonClick(int unpressedDrawableId) {
            switch (unpressedDrawableId){
                case R.drawable.cancellong:
                case R.drawable.resumelong:
                    dlg.hide();
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.restartlong:
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    dlg.hide();
                    game.restartLevel();
                    break;

                case R.drawable.menulong:
                    dlg.hide();
                    finish();
                    break;

                case R.drawable.storelong:
                    launchStore();
                    break;

                case R.drawable.useahintlong:
                    prefs.useHint();
                    game.useHint();
                    dlg.hide();
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.freehintslong:
                    wentTapjoy = true;
                    TapjoyConnect.getTapjoyConnectInstance().showOffers();
                    break;

                case R.drawable.buy1hint:
                    buy(Goods.HintPack1);
                    break;

                case R.drawable.buyhintslong:
                    buy(Goods.HintPack10);
                    break;
            }
        }

        @Override
        public void onCancel() {
            game.setStage(Game.Stages.MainStage);
            game.setPaused(false);
        }

        public void buy(Goods goods) {
            if (mStore != null) {
                wentShop = true;
                mStore.buy(goods);
            }
        }
    };

    class GameListener implements IAppGameListener {

        @Override
        public void showPaused() {
            runOnUiThread(showPausedDialog);
        }

        @Override
        public void showHintMenu() {
            runOnUiThread(showHintMenu);
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
            if (helpView != null){
                rootLayout.removeView(helpView);
                helpView = null;
            }
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
                runOnUiThread(showHintMenu);
        }
    };

    public class TopButtonController {
        RelativeLayout layout;
        ImageView pauseBtn;
        ImageView hintBtn;
        ImageView shopBtn;
        ImageView nextBtn;
        ImageView restartBtn;
        ImageView menuBtn;
        ImageView helpBtn;
        RelativeLayout.LayoutParams rlpTop;
        RelativeLayout.LayoutParams rlpUnderView;

        public TopButtonController(RelativeLayout rootLayour){
            layout = (RelativeLayout)getLayoutInflater().inflate(R.layout.partial_topbuttons, null);
            pauseBtn = (ImageView)layout.findViewById(R.id.pauseBtn);
            hintBtn = (ImageView)layout.findViewById(R.id.hintBtn);
            shopBtn = (ImageView)layout.findViewById(R.id.shopBtn);
            nextBtn = (ImageView)layout.findViewById(R.id.nextBtn);
            restartBtn = (ImageView)layout.findViewById(R.id.restartBtn);
            menuBtn = (ImageView)layout.findViewById(R.id.menuBtn);
            helpBtn = (ImageView)layout.findViewById(R.id.helpBtn);

            pauseBtn.setOnClickListener(mainListener);
            hintBtn.setOnClickListener(mainListener);
            shopBtn.setOnClickListener(mainListener);
            nextBtn.setOnClickListener(mainListener);
            restartBtn.setOnClickListener(mainListener);
            menuBtn.setOnClickListener(mainListener);
            helpBtn.setOnClickListener(mainListener);

            rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale));
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlpTop.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);

            rootLayour.addView(layout, rlpTop);
        }

        public void alignTop(){
            layout.setLayoutParams(rlpTop);
        }

        public void alignUnderView(View v){
            if (rlpUnderView == null){
                rlpUnderView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) (Metrics.squareButtonSize * Metrics.squareButtonScale));
                rlpUnderView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlpUnderView.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);
            }
            rlpUnderView.addRule(RelativeLayout.BELOW, v.getId());
            layout.setLayoutParams(rlpUnderView);
        }


        public void showMainButtons(){
            runOnUiThread(showMainButtons);
        }

        public void showGameWonMenu(){
            runOnUiThread(showWonMenu);
        }

        public void showGameLostMenu(){
            runOnUiThread(showLostMenu);
        }

        void hideAll(){
            pauseBtn.setVisibility(View.GONE);
            hintBtn.setVisibility(View.GONE);
            shopBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
            restartBtn.setVisibility(View.GONE);
            menuBtn.setVisibility(View.GONE);
            helpBtn.setVisibility(View.GONE);
        }

        Runnable showMainButtons = new Runnable() {
            @Override
            public void run() {
                hideAll();
                pauseBtn.setVisibility(View.VISIBLE);
                hintBtn.setVisibility(View.VISIBLE);
            }
        };

        Runnable showWonMenu = new Runnable() {
            @Override
            public void run() {
                hideAll();
                shopBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
                menuBtn.setVisibility(View.VISIBLE);
            }
        };

        Runnable showLostMenu = new Runnable() {
            @Override
            public void run() {
                hideAll();
                restartBtn.setVisibility(View.VISIBLE);
                menuBtn.setVisibility(View.VISIBLE);
                helpBtn.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener mainListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.getInstance(GameActivity.this).playButtonSound();
                switch (v.getId()){
                    case R.id.pauseBtn:
                        runOnUiThread(showPausedDialog);
                        game.setPaused(true);
                        break;
                    case R.id.hintBtn:
                        if (game.isHinting())
                            return;

                        if (game.isFreeHint())
                            game.useHint();
                        else
                            runOnUiThread(showHintMenu);
                        break;
                    case R.id.shopBtn:
                        launchStore();
                        break;
                    case R.id.nextBtn:
                        game.nextLevel();
                        game.setStage(Game.Stages.MainStage);
                        break;
                    case R.id.restartBtn:
                        game.restartLevel();
                        game.setStage(Game.Stages.MainStage);
                        break;
                    case R.id.menuBtn:
                        finish();
                        break;
                    case R.id.helpBtn:
                        showHelp();
                        break;
                }
            }
        };
    }

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

    class LevelInfo{
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
}