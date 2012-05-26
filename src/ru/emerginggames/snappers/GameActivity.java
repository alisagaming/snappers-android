package ru.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import ru.emerginggames.snappers.view.BuyHintsDialog;
import ru.emerginggames.snappers.view.GameDialog;
import ru.emerginggames.snappers.view.ImageView;
import ru.emerginggames.snappers.view.OutlinedTextView;

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
    GameDialog dlg;
    UserPreferences prefs;
    TopButtonController topButtons;
    GameOverMessageController gameOverMessageController;
    LevelInfo levelInfo;
    BuyHintsDialog buyHintsDlg;

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

        levelInfo = new LevelInfo(rootLayout);
        topButtons = new TopButtonController(rootLayout);
        topButtons.showMainButtons();
        gameOverMessageController = new GameOverMessageController();

        if (!prefs.isAdFree()) {
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

    private boolean checkNetworkType(ConnectivityManager conMgr, int type) {
        NetworkInfo netInfo = conMgr.getNetworkInfo(type);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void launchStore() {
        Intent intent = new Intent(GameActivity.this, StoreActivity.class);
        startActivity(intent);
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
            dlg.addButton(R.drawable.button_buyhints_long);
            dlg.addButton(R.drawable.button_freehints_long);
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
                    //wentTapjoy = true;
                    //TapjoyConnect.getTapjoyConnectInstance().showOffers();
                    //TODO: do;
                    break;


                case R.drawable.button_buyhints_long:
                    dlg.hide();
                    showBuyHintsMenu.run();
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
            topButtons.updateHints();
        }
    };

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
                topButtons.alignTop();
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
            hintBtn.setTypeface(Resources.getFont(getApplicationContext()));
            hintBtn.setMaxLines2(1);
            updateHints();

            rootLayour.addView(layout, rlpTop);
        }

        public void updateHints(){
            runOnUiThread(updateHints);
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
                //restartBtn.setVisibility(View.VISIBLE);
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