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
import ru.emerginggames.snappers.view.GameDialog;
import ru.emerginggames.snappers.view.ImageView;

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

        gameListener = new GameListener(prefs);
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

        topButtons = new TopButtonController();
        topButtons.addToView(rootLayout);
        topButtons.showMainButtons();

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

    void showHelp(){
        final View v = getLayoutInflater().inflate(R.layout.partial_help, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeView(v);
                game.setStage(Game.Stages.GameOverStage);
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rootLayout.addView(v, lp);
        game.setStage(Game.Stages.HelpStage);
        topButtons.hideAll();
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
                    gameListener.launchStore();
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
                    gameListener.buy(Goods.HintPack1);
                    break;

                case R.drawable.buyhintslong:
                    gameListener.buy(Goods.HintPack10);
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
        UserPreferences prefs;

        @Override
        public void showPaused() {
            runOnUiThread(showPausedDialog);
        }

        @Override
        public void showHintMenu() {
            runOnUiThread(showHintMenu);
        }

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
            prefs.unlockNextLevelPack(pack);
            setResult(1);
            finish();
        }

        @Override
        public void useHint() {
            prefs.useHint();
        }

        @Override
        public void buy(Goods goods) {
            if (mStore != null) {
                wentShop = true;
                mStore.buy(goods);
            }
        }

        @Override
        public void levelSolved(Level level) {
            prefs.unlockNextLevel(level);
            topButtons.showGameWonMenu();
            if (adController != null)
                adController.showAdTop();
        }

        @Override
        public boolean isLevelSolved(Level level) {
            return prefs.isLevelSolved(level);
        }

        @Override
        public void showLostMenu() {
            topButtons.showGameLostMenu();
            if (adController != null)
                adController.showAdTop();
        }

        @Override
        public void hideGameOverMenu() {
            if (adController != null)
                adController.hideAdTop();
            topButtons.showMainButtons();
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
    }

    UserPreferences.HintChangedListener hintChangedListener = new UserPreferences.HintChangedListener() {
        @Override
        public void onHintsChanged(int old, int current) {
            if (game.getStage() == Game.Stages.HintMenu)
                runOnUiThread(showHintMenu);
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

        public int getAdHeight() {
            return adWhirlLayout.isAdAvailable() ? adWhirlLayout.getHeight() : 0;
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
        ImageView hintBtn;
        ImageView shopBtn;
        ImageView nextBtn;
        ImageView restartBtn;
        ImageView menuBtn;
        ImageView helpBtn;
        RelativeLayout.LayoutParams rlpTop;
        RelativeLayout.LayoutParams rlpUnderView;

        public TopButtonController(){
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
        }

        public void addToView(ViewGroup g){
            rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale));
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlpTop.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlpTop.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);

            g.addView(layout, rlpTop);
        }

        public void alignTop(){
            layout.setLayoutParams(rlpTop);
        }

        public void alignUnderView(View v){
            if (rlpUnderView == null){
                rlpUnderView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (Metrics.squareButtonSize * Metrics.squareButtonScale));
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
                        gameListener.showPaused();
                        break;
                    case R.id.hintBtn:
                        if (game.isHinting())
                            return;

                        if (game.isFreeHint())
                            game.useHint();
                        else
                            gameListener.showHintMenu();
                        break;
                    case R.id.shopBtn:
                        gameListener.launchStore();
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
}