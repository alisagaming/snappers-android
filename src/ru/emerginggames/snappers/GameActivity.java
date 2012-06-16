package ru.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.adapters.AdWhirlAdapter;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.tapjoy.TapjoyConnect;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.IAppGameListener;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.utils.*;
import ru.emerginggames.snappers.view.*;
import ru.emerginggames.snappers.view.ImageView;


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
    GameDialog dlg;
    UserPreferences prefs;
    TopButtonController topButtons;
    GameOverMessageController gameOverMessageController;
    LevelInfo levelInfo;
    BuyHintsDialog buyHintsDlg;
    NewLevelDialog newLevelDialog;
    int currentLevel;
    LevelPack pack;


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

        pack = level.pack;

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

        setContentView(rootLayout);

        levelTable = new LevelTable(this);
        levelTable.open(false);

        if (Settings.GoogleInAppEnabled)
            mStore = GInAppStore.getInstance(getApplicationContext());
        currentLevel = Settings.getLevel(prefs.getScore());


    }

    public void initViews(){
        if (topButtons == null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameOverMessageController = new GameOverMessageController();
                    levelInfo = new LevelInfo(rootLayout);
                    topButtons = new TopButtonController(rootLayout);
                    topButtons.showMainButtons();

                    if (!Settings.IS_PREMIUM && !prefs.isAdFree()) {
                        adController = new AdController();
                        rootLayout.addView(adController.getAdLayout());
                    }
                }
            });
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
            prefs.setHintChangedListener(null);
            if (dlg != null)
                dlg.dismiss();
        }
        if (wentShop || wentTapjoy)
            Resources.preloadResourcesInWorker(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adController != null && prefs.isAdFree()) {
            adController.finish();
            rootLayout.removeView(adController.getAdLayout());
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
                game.setStage(Game.Stages.PausedStage);

        return super.onKeyUp(keyCode, event);
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
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    void showHelp(){
        final View v = getLayoutInflater().inflate(R.layout.partial_help, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeView(v);
                game.setStage(Game.Stages.GameOverStage);
                levelInfo.show();
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rootLayout.addView(v, lp);
        game.setStage(Game.Stages.HelpStage);
        topButtons.hideAll();
        levelInfo.hide();
    }

    Runnable showPausedDialog = new Runnable() {
        @Override
        public void run() {
            game.setPaused(true);
            if (dlg == null)
                initDialog();
            else
                dlg.clear();
            dlg.setTwoButtonsARow(false);
            dlg.setItemSpacing(Metrics.screenMargin * 2);
            dlg.setTitle(R.string.game_paused);
            dlg.addButton(R.drawable.button_resume_long);
            dlg.addButton(R.drawable.button_restart_long);
            dlg.addButton(R.drawable.button_menu_long);
            dlg.show();
        }
    };

    Runnable showBuyHintsMenu = new Runnable() {
        @Override
        public void run() {
            if (buyHintsDlg== null){
                buyHintsDlg = new BuyHintsDialog(GameActivity.this, Metrics.screenWidth * 95 / 100);
            }

            buyHintsDlg.setTitle(R.string.hints);

            buyHintsDlg.show();
        }
    };

    Runnable showFreeHintsMenu = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else
                dlg.clear();
            dlg.setTwoButtonsARow(true);
            dlg.setItemSpacing(Metrics.screenMargin);
            dlg.setTitle(R.string.free_hints);
            dlg.addButton(R.drawable.button_invite);
            dlg.addButton(R.drawable.button_like);
            dlg.addButton(R.drawable.button_promo);
            dlg.addButton(R.drawable.button_rate);

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
            dlg.setTwoButtonsARow(false);
            dlg.setItemSpacing(Metrics.screenMargin * 2);

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

            dlg.addButton(R.drawable.button_usehint_long);
            dlg.addButton(R.drawable.button_freehints_long);
            dlg.addButton(R.drawable.button_buyhints_long);
        }

        void showBuyHintMenu(){
            android.content.res.Resources res = getResources();
            StringBuilder msg = new StringBuilder();
            msg.append(res.getString(R.string.youHaveNoHints)).append("\n").append(res.getString(R.string.buySome));
            dlg.setMessage(msg, Metrics.fontSize);
            dlg.addButton(R.drawable.button_freehints_long);
            dlg.addButton(R.drawable.button_buyhints_long);
        }

        void showGetOnlineMenu(){
            android.content.res.Resources res = getResources();
            StringBuilder msg = new StringBuilder();
            msg.append(res.getString(R.string.youHaveNoHints)).append("\n").append(res.getString(R.string.getOnline));
            dlg.setMessage(msg, Metrics.fontSize);
            //TODO: add button if needed
        }
    };

    void initDialog(){
        dlg = new GameDialog(GameActivity.this);
        dlg.setWidth(Metrics.screenWidth * 95 / 100);
        dlg.setBtnClickListener(dialogButtonListener);
        dlg.setItemSpacing(Metrics.screenMargin * 2);
        dlg.setTypeface(Resources.getFont(this));
    }

    Runnable showNewLevelDialog = new Runnable() {
        @Override
        public void run() {
            if (newLevelDialog == null){
                newLevelDialog = new NewLevelDialog(GameActivity.this, Metrics.screenWidth * 95 / 100);
            }
            newLevelDialog.setLevel(currentLevel);
            newLevelDialog.show();
            newLevelDialog.setBtnClickListener(new GameDialog.OnDialogEventListener() {
                @Override
                public void onButtonClick(int unpressedDrawableId) {
                    newLevelDialog.hide();
                    gameOverMessageController.hideRays();
                }

                @Override
                public void onCancel() {
                    newLevelDialog.hide();
                    gameOverMessageController.hideRays();
                }
            });

            if (prefs.getSound())
                Resources.fanfareSound.play();
        }
    };

    GameDialog.OnDialogEventListener dialogButtonListener = new GameDialog.OnDialogEventListener() {
        @Override
        public void onButtonClick(int unpressedDrawableId) {
            switch (unpressedDrawableId){
                case R.drawable.button_resume_long:
                    dlg.hide();
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.button_restart_long:
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    dlg.hide();
                    game.restartLevel();
                    break;

                case R.drawable.button_menu_long:
                    dlg.hide();
                    finish();
                    break;

                case R.drawable.button_usehint_long:
                    prefs.useHint();
                    game.useHint();
                    dlg.hide();
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.button_freehints_long:
                    dlg.hide();
                    showFreeHintsMenu.run();
                    break;


                case R.drawable.button_buyhints_long:
                    dlg.hide();
                    showBuyHintsMenu.run();
                    break;

                case R.drawable.button_promo:
                    wentTapjoy = true;
                    TapjoyConnect.getTapjoyConnectInstance().showOffers();
                    break;
            }
        }

        @Override
        public void onCancel() {
            game.setStage(Game.Stages.MainStage);
            game.setPaused(false);
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
                gameOverMessageController.showRays();
                runOnUiThread(showNewLevelDialog);
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
            levelInfo.hide();
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
                runOnUiThread(showHintMenu);
            topButtons.updateHints();
        }
    };

    class AdController implements IOnAdShowListener {
        public MyAdWhirlLayout adWhirlLayout;
        public RelativeLayout.LayoutParams lpUp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        public RelativeLayout.LayoutParams lpDown = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean isShowingAd = false;
        boolean shouldShowAdTop = false;
        boolean canShowAd = false;
        boolean shouldShowIngameAd;
        boolean canShowIngameAd = true;
        UserPreferences prefs;
        int adHeight;

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
            adWhirlLayout.setId(R.id.adCont);
        }

        public MyAdWhirlLayout getAdLayout() {
            return adWhirlLayout;
        }

        public void finish() {
            adWhirlLayout.setAdShowListener(null);
            MyAdWhirlLayout.setEnforceUpdate(false);
            adWhirlLayout.setVisibility(View.GONE);
        }

        public int getAdHeight(){
            return adWhirlLayout.isAdAvailable() ? adHeight : 0;
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
        }

        @Override
        public void onAdShow() {
            if (isFinished)
                return;
            canShowAd = true;
            if (shouldShowAdTop) {
                showAdTop();
            } else if (shouldShowIngameAd && canShowIngameAd) {
                runOnUiThread(moveAdBottom);
                runOnUiThread(showAD);
            }
        }

        @Override
        public void onAdSizeChanged(int width, int height) {
            if (game.initDone && ! isFinished)
                setGameMargins(height);
            adHeight = height;
            gameOverMessageController.setAdVisible(shouldShowAdTop & isShowingAd);
        }

        @Override
        public void onAdFail() {
            if (isFinished)
                return;
            canShowAd = false;
            if (isShowingAd)
                runOnUiThread(hideAD);
        }

        Runnable showAD = new Runnable() {
            @Override
            public void run() {
                if (isShowingAd)
                    return;

                adWhirlLayout.setVisibility(View.VISIBLE);
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
                gameOverMessageController.setAdVisible(false);
            }
        };

        Runnable moveAdBottom = new Runnable() {
            @Override
            public void run() {
                adWhirlLayout.setLayoutParams(lpDown);
                AdWhirlAdapter.setGoogleAdSenseExpandDirection("UP");
                topButtons.alignTop();
                gameOverMessageController.setAdVisible(false);
            }
        };

        Runnable moveAdTop = new Runnable() {
            @Override
            public void run() {
                adWhirlLayout.setLayoutParams(lpUp);
                AdWhirlAdapter.setGoogleAdSenseExpandDirection("BOTTOM");
                topButtons.alignUnderView(adWhirlLayout);
            }
        };
    }

    class TopButtonController {
        RelativeLayout layout;
        ImageView pauseBtn;
        OutlinedTextView hintBtn;

        ImageView nextBtn;
        ImageView restartBtn;
        ImageView menuBtn;
        ImageView helpBtn;
        RelativeLayout.LayoutParams rlpTop;
        RelativeLayout.LayoutParams rlpUnderView;
        ScoreCounter scoreCounter;

        public TopButtonController(RelativeLayout rootLayour){
            layout = (RelativeLayout)getLayoutInflater().inflate(R.layout.partial_topbuttons, null);
            pauseBtn = (ImageView)layout.findViewById(R.id.pauseBtn);
            hintBtn = (OutlinedTextView)layout.findViewById(R.id.hintBtn);
            nextBtn = (ImageView)layout.findViewById(R.id.nextBtn);
            restartBtn = (ImageView)layout.findViewById(R.id.restartBtn);
            menuBtn = (ImageView)layout.findViewById(R.id.menuBtn);
            helpBtn = (ImageView)layout.findViewById(R.id.helpBtn);

            pauseBtn.setOnClickListener(mainListener);
            hintBtn.setOnClickListener(mainListener);
            nextBtn.setOnClickListener(mainListener);
            restartBtn.setOnClickListener(mainListener);
            menuBtn.setOnClickListener(mainListener);
            helpBtn.setOnClickListener(mainListener);

            rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale));
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlpTop.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)hintBtn.getLayoutParams();
            int bgWidth = hintBtn.getBackground().getIntrinsicWidth();
            int bgHeight = hintBtn.getBackground().getIntrinsicHeight();
            float scale = ((float)rlpTop.height / bgHeight );
            lp.width = Math.round(bgWidth * scale);
            lp.height = rlpTop.height;
            int[] iPaddings = {27, 23, 94, 23};
            hintBtn.setPadding((int)(iPaddings[0] * scale), (int)(iPaddings[1] * scale), (int)(iPaddings[2] * scale), (int)(iPaddings[3] * scale));
            hintBtn.setLayoutParams(lp);
            hintBtn.setTypeface(Resources.getFont(getApplicationContext()));
            hintBtn.setMaxLines2(1);
            updateHints();

            addScoreCounter();
            rootLayour.addView(layout, rlpTop);
        }

        public void updateHints(){
            runOnUiThread(updateHints);
        }

        void addScoreCounter(){
            int size = (int)(Metrics.squareButtonSize * Metrics.squareButtonScale);
            scoreCounter = new ScoreCounter(GameActivity.this, size * 32 / 10, size);
            scoreCounter.setVisibility(View.GONE);
            scoreCounter.setScore(prefs.getScore());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addView(scoreCounter.getView(), lp);
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

        public void showScore(){
            runOnUiThread(showScore);
        }

        public void hideScore(){
            runOnUiThread(hideScore);
        }

        void hideAll(){
            pauseBtn.setVisibility(View.GONE);
            hintBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
            restartBtn.setVisibility(View.GONE);
            menuBtn.setVisibility(View.GONE);
            helpBtn.setVisibility(View.GONE);
        }

        Runnable updateHints = new Runnable() {
            @Override
            public void run() {
                hintBtn.setText2(Integer.toString(prefs.getHintsRemaining()));
            }
        };

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
                nextBtn.setVisibility(View.VISIBLE);
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

        Runnable showScore = new Runnable() {
            @Override
            public void run() {
                scoreCounter.setScoreProlonged(prefs.getScore());
                scoreCounter.setVisibility(View.VISIBLE);
            }
        };

        Runnable hideScore = new Runnable() {
            @Override
            public void run() {
                scoreCounter.setVisibility(View.GONE);
            }
        };

        View.OnClickListener mainListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.getInstance(GameActivity.this).playButtonSound();
                switch (v.getId()){
                    case R.id.pauseBtn:
                        runOnUiThread(showPausedDialog);


                        break;
                    case R.id.hintBtn:
                        if (game.isHinting())
                            return;

                        if (game.isFreeHint())
                            game.useHint();
                        else
                            runOnUiThread(showHintMenu);
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
        RelativeLayout layout;
        OutlinedTextView title;
        OutlinedTextView message;
        boolean isWon;
        int msgValue;
        StarsController stars;


        GameOverMessageController() {
            if (layout == null){
                layout = (RelativeLayout)getLayoutInflater().inflate(R.layout.partial_level_result, null);
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

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            rootLayout.addView(layout, lp);
            layout.setVisibility(View.GONE);

            stars = new StarsController();
        }

        public void showRays(){
            runOnUiThread(showRays);
        }

        public void hideRays(){
            runOnUiThread(hideRays);
        }

        public void show(boolean isWon, int msgValue){
            this.isWon = isWon;
            this.msgValue = msgValue;
            runOnUiThread(show);
        }

        public void hide(){
            runOnUiThread(hide);
        }

        public void setAdVisible(boolean visible){
            stars.onAdVisibilityChanged(visible);
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
                int titleId = isWon ? getWinTitleId() : getLostTitleId();
                title.setText2(titleId);
                String msg;
                if (isWon){
                    msg = getResources().getString(R.string.score, msgValue);
                    stars.showScoreStars(msgValue);
                }
                else if (msgValue == 1)
                    msg = getResources().getString(R.string.possible_in_1_touch);
                else
                    msg = getResources().getString(R.string.possible_in_touches, msgValue);
                message.setText2(msg);
                layout.setVisibility(View.VISIBLE);
            }
        };

       Runnable hide = new Runnable() {
           @Override
           public void run() {
               layout.setVisibility(View.GONE);
           }
       };

       Runnable hideRays = new Runnable() {
           @Override
           public void run() {
               layout.findViewById(R.id.backCont).setVisibility(View.GONE);
           }
       };

       Runnable showRays = new Runnable() {
           @Override
           public void run() {
               if (((RelativeLayout)layout.findViewById(R.id.backCont)).getChildCount() == 0){
                   int width = Metrics.screenWidth;
                   int height = Metrics.screenHeight;
                   int diagonal = (int)(Math.sqrt(width * width + height * height));
                   float scale = (float)diagonal/width;

                   ImageView rays = new ImageView(GameActivity.this);
                   rays.setImageResource(R.drawable.rays);
                   rays.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);

                   RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
                   lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                   ((RelativeLayout)layout.findViewById(R.id.backCont)).addView(rays, lp);
                   lp = (RelativeLayout.LayoutParams)layout.findViewById(R.id.backCont).getLayoutParams();
                   lp.width = lp.height = diagonal;

                   RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                   animation.setDuration(600);
                   animation.setRepeatMode(Animation.RESTART);
                   animation.setRepeatCount(Integer.MAX_VALUE);

                   ScaleAnimation scaleAnim = new ScaleAnimation(scale, scale, scale, scale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                   AnimationSet set = new AnimationSet(true);
                   set.addAnimation(animation);
                   set.addAnimation(scaleAnim);
                   set.setInterpolator(new LinearInterpolator());
                   set.setFillAfter(true);

                   rays.setAnimation(set);
               }

               layout.findViewById(R.id.backCont).setVisibility(View.VISIBLE);
           }
       };
    }

    class StarsController{
        RelativeLayout.LayoutParams starLP;
        private Array<ImageView> activeStars ;
        private Pool<ImageView> starsPool;
        int activeStarsCount;
        Interpolator interpolatorIn = new AccelerateInterpolator();
        Interpolator interpolatorOut = new LinearInterpolator();

        StarsController() {
            int size = (int)(Metrics.squareButtonSize * Metrics.squareButtonScale);
            starLP = new RelativeLayout.LayoutParams(size, size);

            starsPool = new Pool<ImageView>(5, 10){
                @Override
                protected ImageView newObject() {
                    ImageView img = new ImageView(GameActivity.this);
                    img.setImageResource(R.drawable.star);
                    img.setLayoutParams(starLP);
                    return img;
                }
            };
            activeStars = new Array<ImageView>(10);
        }

        Runnable freeStars = new Runnable() {
            @Override
            public void run() {
                for (int i=0; i< activeStars.size; i++)
                    rootLayout.removeView(activeStars.get(i));
                starsPool.free(activeStars);
            }
        };

        Animation.AnimationListener animListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                        activeStarsCount--;
                        if (activeStarsCount == 0)
                            runOnUiThread(freeStars);
                    }

            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        };

        public void onAdVisibilityChanged(boolean visible){
            starLP.topMargin = visible ? adController.getAdHeight() : 0;
        }

        public void showScoreStars(int score){
            float size = Metrics.squareButtonSize * Metrics.squareButtonScale;
            int amount = 3 + score / 1500;
            if (adController!= null)
                starLP.topMargin = adController.getAdHeight();

            for (int i=0; i< amount; i++){
                ImageView img = starsPool.obtain();
                float endScale = (float)(Math.random() *0.3 + 0.7);
                int endX = (int)(Math.random() * size * (1.2 - endScale));
                int endY = (int)(Math.random() * size * (1.2 - endScale));
                float startX = (float)(Math.random() *0.5 + 0.5);
                float startY = (float)(Math.random() *0.5 + 0.5);
                int timeDev = (int)(Math.random() * 400);
                img.setAnimation(getAnimation(startX, startY, endX, endY, endScale, timeDev));
                rootLayout.addView(img);
                activeStarsCount++;
            }
        }

        Animation getAnimation(float startX, float startY, int endX, int endY, float endScale, int timeDeviation){

            TranslateAnimation moveInAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, startX, Animation.ABSOLUTE, endX,
                    Animation.RELATIVE_TO_PARENT, startY, Animation.ABSOLUTE, endY);
            moveInAnim.setDuration(600 + timeDeviation);
            moveInAnim.setStartOffset(0);
            moveInAnim.setInterpolator(interpolatorIn);

            ScaleAnimation scaleInAnim = new ScaleAnimation(0, endScale, 0, endScale);
            scaleInAnim.setDuration(600 + timeDeviation);
            scaleInAnim.setInterpolator(interpolatorIn);

            ScaleAnimation scaleOutAnim = new ScaleAnimation(endScale, 0, endScale, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f  );
            scaleOutAnim.setDuration(500);
            scaleOutAnim.setStartOffset(1300 + timeDeviation);
            scaleOutAnim.setInterpolator(interpolatorOut);

            AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 0);
            fadeOutAnimation.setDuration(500);
            fadeOutAnimation.setStartOffset(1300 + timeDeviation);
            fadeOutAnimation.setInterpolator(interpolatorOut);

            AnimationSet animSet = new AnimationSet(true);
            animSet.addAnimation(scaleInAnim);
            animSet.addAnimation(moveInAnim);
            animSet.addAnimation(scaleOutAnim);
            animSet.addAnimation(fadeOutAnimation);

            animSet.setAnimationListener(animListener);
            animSet.setFillAfter(true);

            return animSet;
        }
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