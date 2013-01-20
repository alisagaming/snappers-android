package com.emerginggames.bestpuzzlegame.view;

import android.content.Intent;
import android.net.Uri;
import android.view.WindowManager;
import com.emerginggames.bestpuzzlegame.*;
import com.emerginggames.bestpuzzlegame.transport.FacebookTransport;
import com.emerginggames.bestpuzzlegame.gdx.Game;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.tapjoy.TapjoyConnect;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.06.12
 * Time: 15:08
 */
public class GameDialogsController {
    GameDialog dlg;
    BuyHintsDialog buyHintsDlg;
    NewLevelDialog newLevelDialog;
    GameActivity mActivity;
    UserPreferences prefs;
    int mCurrentLevel;

    public GameDialogsController(GameActivity mActivity) {
        this.mActivity = mActivity;
        prefs = UserPreferences.getInstance(mActivity);
    }

    public void dismiss() {
        if (dlg != null)
            dlg.dismiss();
        if (newLevelDialog != null)
            newLevelDialog.dismiss();
        if (buyHintsDlg != null)
            buyHintsDlg.dismiss();
    }

    public void showPauseDialog() {
        mActivity.runOnUiThread(showPausedDialog);
    }

    public void showHintMenu() {
        mActivity.runOnUiThread(showHintMenu);
    }

    public void showNewLevelDialog(int newLevel) {
        mCurrentLevel = newLevel;
        mActivity.runOnUiThread(showNewLevelDialog);
        mActivity.getGame().showRays(true);
    }

    void showPromoDialog() {
        PromoDialog promoDialog = new PromoDialog(mActivity);
        promoDialog.setOwnerActivity(mActivity);
        promoDialog.show();
    }

    public void showShareDialog() {
        mActivity.runOnUiThread(showShareDialog);
    }

    public void showRateDialog() {
        prefs.setLastLikeOrRecommended(System.currentTimeMillis());
        mActivity.runOnUiThread(showRateDialog);
    }

    public void showLikeDialog() {
        prefs.setLastLikeOrRecommended(System.currentTimeMillis());
        mActivity.runOnUiThread(showLikeDialog);
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
            if (buyHintsDlg == null) {
                buyHintsDlg = new BuyHintsDialog(mActivity, Metrics.screenWidth * 95 / 100);
            }

            buyHintsDlg.setTitle(R.string.buy_hints);

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
            //dlg.addButton(R.drawable.button_invite);
            dlg.addButton(R.drawable.button_follow, !prefs.isFollowed());
            dlg.addButton(R.drawable.button_like, !prefs.isLiked());
            //dlg.addButton(R.drawable.button_promo);
            dlg.addButton(R.drawable.button_tapjoy);
            dlg.addButton(R.drawable.button_rate, !prefs.isRated());

            dlg.show();
        }
    };

    Runnable showHintMenu = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else {
                if (dlg.isShowing())
                    dlg.hide();
                dlg.clear();
            }
            dlg.setTwoButtonsARow(false);
            dlg.setItemSpacing(Metrics.screenMargin * 2);

            int hintsLeft = prefs.getHintsRemaining();
            if (hintsLeft > 0)
                fillUseHintMenu(hintsLeft);
            else if (mActivity.checkNetworkStatus())
                showBuyHintMenu();
            else
                showGetOnlineMenu();
            dlg.show();
        }

        void fillUseHintMenu(int hintsLeft) {
            if (hintsLeft == 1)
                dlg.setMessage(R.string.youHaveOneHint, Metrics.fontSize);
            else
                dlg.setMessage(mActivity.getString(R.string.youHave_n_Hints, hintsLeft), Metrics.fontSize);

            dlg.addButton(R.drawable.button_usehint_long);
            dlg.addButton(R.drawable.button_freehints_long);
            dlg.addButton(R.drawable.button_buyhints_long);
        }

        void showBuyHintMenu() {
            StringBuilder msg = new StringBuilder();
            msg.append(mActivity.getString(R.string.youHaveNoHints)).append("\n").append(mActivity.getString(R.string.buySome));
            dlg.setMessage(msg, Metrics.fontSize);
            dlg.addButton(R.drawable.button_freehints_long);
            dlg.addButton(R.drawable.button_buyhints_long);
        }

        void showGetOnlineMenu() {
            StringBuilder msg = new StringBuilder();
            msg.append(mActivity.getString(R.string.youHaveNoHints)).append("\n").append(mActivity.getString(R.string.getOnline));
            dlg.setMessage(msg, Metrics.fontSize);
            //TODO: add button if needed
        }
    };

    void initDialog() {
        dlg = new GameDialog(mActivity, Metrics.screenWidth * 95 / 100);
        dlg.setBtnClickListener(dialogButtonListener);
        dlg.setItemSpacing(Metrics.screenMargin * 2);
        dlg.setTypeface(Resources.getFont(mActivity));
    }

    void showNeedFacebookMessage() {
        if (dlg == null)
            initDialog();
        else
            dlg.clear();
        dlg.setMessage(R.string.needFacebook, Metrics.fontSize);
        dlg.addOkButton();
        dlg.show();
    }

    Runnable showNewLevelDialog = new Runnable() {
        @Override
        public void run() {
            if (newLevelDialog == null) {
                newLevelDialog = new NewLevelDialog(mActivity, Metrics.screenWidth * 95 / 100);
            }
            newLevelDialog.setLevel(mCurrentLevel);
            newLevelDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            newLevelDialog.setTypeface(Resources.getFont(mActivity.getApplicationContext()));
            newLevelDialog.show();
            newLevelDialog.setBtnClickListener(new GameDialog.OnDialogEventListener() {
                @Override
                public void onButtonClick(int unpressedDrawableId) {
                    newLevelDialog.hide();
                    mActivity.getGame().showRays(false);
                }

                @Override
                public void onCancel() {
                    newLevelDialog.hide();
                    mActivity.getGame().showRays(false);
                }
            });

            if (prefs.getSound())
                Resources.fanfareSound.play();
        }
    };

    Runnable showShareDialog = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else dlg.clear();

            dlg.setMessage(R.string.share, (int) (Metrics.fontSize * 1.2f));
            dlg.addButton(R.drawable.button_share, 0.5f);
            dlg.show();
        }
    };

    Runnable showRateDialog = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else dlg.clear();

            dlg.setMessage(R.string.rateUs, (int) (Metrics.fontSize * 1.2f));
            dlg.addButton(R.drawable.button_rate_long, 0.5f);
            dlg.show();
        }
    };

    Runnable showLikeDialog = new Runnable() {
        @Override
        public void run() {
            if (dlg == null)
                initDialog();
            else dlg.clear();

            dlg.setMessage(R.string.likeUs, (int) (Metrics.fontSize * 1.2f));
            dlg.addButton(R.drawable.button_like_long, 0.5f);
            dlg.show();
        }
    };

    GameDialog.OnDialogEventListener dialogButtonListener = new GameDialog.OnDialogEventListener() {
        @Override
        public void onButtonClick(int unpressedDrawableId) {
            Game game = mActivity.getGame();
            switch (unpressedDrawableId) {
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
                    mActivity.finish();
                    mActivity.onCloseGame();
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
                    dlg.hide();
                    if (mActivity.hasFacebook())
                        showPromoDialog();
                    else
                        showNeedFacebookMessage();
//                    mActivity.wentTapjoy = true;
//                    TapjoyConnect.getTapjoyConnectInstance().showOffers();
                    break;

                case R.drawable.button_like_long:
                case R.drawable.button_like:
                    dlg.hide();
                    //if (mActivity.hasFacebook()) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mActivity.getString(R.string.likeUrl)));
                        mActivity.startActivity(browserIntent);
                        prefs.addHints(Settings.BONUS_FOR_LIKE);
                        prefs.setLiked(true);
                    //} else
                    //    showNeedFacebookMessage();
                    break;

                case R.drawable.button_tapjoy:
                    mActivity.wentTapjoy = true;
                    TapjoyConnect.getTapjoyConnectInstance().showOffers();

                    break;

                case R.drawable.button_follow:
                    dlg.hide();
                    Intent browserIntentFollow = new Intent(Intent.ACTION_VIEW, Uri.parse(mActivity.getString(R.string.followUrl)));
                    mActivity.startActivity(browserIntentFollow);
                    prefs.addHints(Settings.BONUS_FOR_FOLLOW);
                    prefs.setFollowed(true);

                    break;

                case R.drawable.button_rate_long:
                case R.drawable.button_rate:
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Settings.STORE_BASE + mActivity.getPackageName())));
                    prefs.addHints(Settings.BONUS_FOR_RATE);
                    prefs.setRated(true);
                    dlg.hide();
                    break;

                case R.drawable.button_share:
                    FacebookTransport transport = new FacebookTransport(mActivity);
                    transport.share(null);
                    dlg.hide();
                    prefs.addHints(Settings.BONUS_FOR_SHARE);
                    break;

                case R.drawable.button_invite:
                    dlg.hide();
                    if (mActivity.hasFacebook()) {
                        mActivity.startActivity(new Intent(mActivity, InviteActivity.class));
                    } else
                        showNeedFacebookMessage();
                    break;


            }
        }

        @Override
        public void onCancel() {
            Game game = mActivity.getGame();
            if (game.getStage() != Game.Stages.GameOverStage)
                game.setStage(Game.Stages.MainStage);
            game.setPaused(false);
        }
    };
}


