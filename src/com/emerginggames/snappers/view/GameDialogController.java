package com.emerginggames.snappers.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.emerginggames.snappers.*;
import com.emerginggames.snappers.gdx.Game;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.Goods;
import com.emerginggames.snappers.model.MoreGame;
import com.emerginggames.snappers.utils.MoreGamesUtil;
import com.tapjoy.TapjoyConnect;

import java.util.List;

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
            List<MoreGame> games = MoreGamesUtil.load(activity.getApplicationContext());
            if(games == null || games.size() == 0)
                return;

            MoreGamesAdapter adapter = new MoreGamesAdapter(activity.getApplicationContext(), games);
            int pos = (int)(Math.random() * games.size());
            View gameView = adapter.getView(pos, null, null);
            MoreGame game = (MoreGame)adapter.getItem(pos);
            gameView.setPadding(0,0,0,0);

            if (dlg == null)
                initDialog();
            else
                dlg.clear();

            dlg.setTitle(R.string.downloadFreeGame, new int[]{13, 0});

            dlg.addView(gameView);

            dlg.addButton(R.drawable.download_btn, R.drawable.download_btn_tap, game);
            dlg.addButton(R.drawable.nothanks_btn, R.drawable.nothanks_btn_tap);
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
                    dlg.dismiss();
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
        synchronized (this){

            if (dlg != null)
                return;

            int maxWidthDip;
            float density = activity.getResources().getDisplayMetrics().density;
            int dpWidth = (int)(Metrics.screenWidth / density);
            if (dpWidth < 3 * 160)
                maxWidthDip = (int)(1.7f * 160);
            else if (dpWidth < 5 * 160)
                maxWidthDip = (int)(2.6f * 160);
            else
                maxWidthDip = (int)(3.5f * 160);

            int maxWidth = (int)(maxWidthDip * density);

            dlg = new GameDialog(activity);
            dlg.setWidth(Math.min(Metrics.screenWidth * 80 / 100, maxWidth));


            dlg.setBtnClickListener(dialogButtonListener);
            dlg.setItemSpacing(Metrics.screenMargin);
            dlg.setTypeface(Resources.getFont(activity));
        }
    }

    GameDialog.OnDialogEventListener dialogButtonListener = new GameDialog.OnDialogEventListener() {
        @Override
        public void onButtonClick(int unpressedDrawableId, Object tag) {
            switch (unpressedDrawableId){
                case R.drawable.cancellong:
                case R.drawable.resumelong:
                    dlg.dismiss();
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    break;

                case R.drawable.restartlong:
                    game.setPaused(false);
                    game.setStage(Game.Stages.MainStage);
                    dlg.dismiss();
                    game.restartLevel();
                    break;

                case R.drawable.menulong:
                    dlg.dismiss();
                    activity.finish();
                    break;

                case R.drawable.storelong:
                    activity.launchStore();
                    break;

                case R.drawable.useahintlong:
                    prefs.useHint();
                    game.useHint();
                    dlg.dismiss();
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

                case R.drawable.nothanks_btn:
                    dlg.dismiss();
                    break;

                case R.drawable.download_btn:
                    MoreGame game = (MoreGame)tag;
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(game.url)));
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
