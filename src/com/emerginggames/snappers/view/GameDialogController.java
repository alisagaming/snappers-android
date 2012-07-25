package com.emerginggames.snappers.view;

import android.content.Intent;
import android.net.Uri;
import com.emerginggames.snappers.*;
import com.emerginggames.snappers.gdx.Game;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.Goods;
import com.tapjoy.TapjoyConnect;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 18.07.12
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class GameDialogController {
    GameActivity activity;
    UserPreferences prefs;
    Game game;
    GameDialog dlg;

    public GameDialogController(GameActivity activity, Game game) {
        this.activity = activity;
        this.game = game;
        prefs = UserPreferences.getInstance(activity.getApplicationContext());
    }



    public void showPaused() {
        activity.runOnUiThread(showPausedDialog);
    }

    public void showHintMenu() {
        activity.runOnUiThread(showHintMenu);
    }

    public void showFreeGamesBanner(){
        activity.runOnUiThread(showFreeGamesBanner);
    }

    Runnable showFreeGamesBanner = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else
                dlg.clear();

            dlg.setMessage(R.string.downloadFreeGame, Metrics.fontSize);

            dlg.show();
        }
    };

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

    Runnable showFreeHintsDialog = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else
                dlg.clear();
            if(prefs.getLikeUrl() != null)
                dlg.addButton(R.drawable.like_fb_btn, R.drawable.like_fb_btn_tap);
            if (prefs.getFollowUrl() != null)
                dlg.addButton(R.drawable.follow_twitter_btn, R.drawable.follow_twitter_btn_tap);
            if (prefs.isTapjoyEnabled())
                dlg.addButton(R.drawable.tapjoy_btn, R.drawable.tapjoy_btn_tap);
            dlg.setTitle(R.string.freeHints);
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
            else if (activity.checkNetworkStatus())
                showBuyHintMenu();
            else
                showGetOnlineMenu();
            dlg.show();
        }

        void fillUseHintMenu(int hintsLeft){
            if (hintsLeft == 1)
                dlg.setMessage(R.string.youHaveOneHint, Metrics.fontSize);
            else
                dlg.setMessage(activity.getResources().getString(R.string.youHave_n_Hints, hintsLeft), Metrics.fontSize);

            dlg.addButton(R.drawable.useahintlong, R.drawable.useahintlong_tap);
            if (prefs.isTapjoyEnabled())
                dlg.addButton(R.drawable.freehintslong, R.drawable.freehintslong_tap);
            dlg.addButton(R.drawable.cancellong, R.drawable.cancellong_tap);

        }

        void showBuyHintMenu(){
            android.content.res.Resources res = activity.getResources();
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
            android.content.res.Resources res = activity.getResources();
            StringBuilder msg = new StringBuilder();
            msg.append(res.getString(R.string.youHaveNoHints)).append("\n").append(res.getString(R.string.getOnline));
            dlg.setMessage(msg, Metrics.fontSize);
            dlg.addButton(R.drawable.cancellong, R.drawable.cancellong_tap);
        }
    };

    void initDialog(){
        dlg = new GameDialog(activity);
        dlg.setWidth(Math.min(Metrics.menuWidth, Metrics.screenWidth * 9 / 10));
        dlg.setBtnClickListener(dialogButtonListener);
        dlg.setItemSpacing(Metrics.screenMargin);
        dlg.setTypeface(Resources.getFont(activity));
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
                    activity.finish();
                    break;

                case R.drawable.storelong:
                    activity.launchStore();
                    break;

                case R.drawable.useahintlong:
                    prefs.useHint();
                    game.useHint();
                    dlg.hide();
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.freehintslong:
                    activity.runOnUiThread(showFreeHintsDialog);
                    break;

                case R.drawable.buy1hint:
                    activity.buy(Goods.HintPack1);
                    break;

                case R.drawable.buyhintslong:
                    activity.buy(Goods.HintPack10);
                    break;

                case R.drawable.like_fb_btn:
                    activity.wentTemp = true;
                    startUrl(prefs.getLikeUrl());
                    if (!prefs.isLiked()){
                        prefs.setLiked();
                        prefs.addHints(Settings.BONUS_FOR_LIKE);
                    }
                    break;

                case R.drawable.follow_twitter_btn:
                    activity.wentTemp = true;
                    startUrl(prefs.getFollowUrl());
                    if (!prefs.isFollowed()){
                        prefs.setFolowed();
                        prefs.addHints(Settings.BONUS_FOR_FOLLOW);
                    }

                    break;

                case R.drawable.tapjoy_btn:
                    activity.wentTemp = true;
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

    void startUrl(String url){
        if (url == null)
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
        activity.startActivity(intent);
    }
}
