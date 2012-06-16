package ru.emerginggames.snappers.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.adapters.AdWhirlAdapter;
import ru.emerginggames.snappers.GameActivity;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.Settings;
import ru.emerginggames.snappers.UserPreferences;
import ru.emerginggames.snappers.utils.IOnAdShowListener;
import ru.emerginggames.snappers.utils.MyAdWhirlLayout;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.06.12
 * Time: 14:41
 */
public class GameAdController implements IOnAdShowListener {
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
    GameActivity mActivity;
    RelativeLayout mRootLayout;

    public GameAdController(RelativeLayout rootLayout, GameActivity activity) {
        mActivity = activity;
        mRootLayout = rootLayout;
        prefs = UserPreferences.getInstance(mActivity.getApplicationContext());
        lpUp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpUp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpDown.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpDown.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        shouldShowIngameAd = prefs.getIngameAds();

        AdWhirlTargeting.setKeywords("game puzzle");
        AdWhirlAdapter.setGoogleAdSenseExpandDirection("UP");
        if (Settings.DEBUG)
            AdWhirlTargeting.setTestMode(true);

        adWhirlLayout = new MyAdWhirlLayout(mActivity, Settings.getAdwhirlKey(mActivity));
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
            mActivity.runOnUiThread(moveAdTop);
        if (!isShowingAd)
            mActivity.runOnUiThread(showAD);
    }

    public void hideAdTop() {
        shouldShowAdTop = false;
        if (shouldShowIngameAd)
            mActivity.runOnUiThread(moveAdBottom);
        else
            mActivity.runOnUiThread(hideAD);
    }

    public void setGameMargins(int height){
        if (height == 0)
            height = adWhirlLayout.getHeight();

        if (shouldShowIngameAd) {
            if (height < mActivity.getGame().getMarginBottom())
                canShowIngameAd = true;
            else if (height < mActivity.getGame().getMaxMarginBottom()) {
                canShowIngameAd = true;
                mActivity.getGame().resizeMarginBottom(height);
            }
            else canShowIngameAd = false;
            if (!canShowIngameAd)
                mActivity.runOnUiThread(hideAD);
        }
    }

    @Override
    public void onAdShow() {
        if (mActivity.isFinished)
            return;
        canShowAd = true;
        if (shouldShowAdTop) {
            showAdTop();
        } else if (shouldShowIngameAd && canShowIngameAd) {
            mActivity.runOnUiThread(moveAdBottom);
            mActivity.runOnUiThread(showAD);
        }
    }

    @Override
    public void onAdSizeChanged(int width, int height) {
        if (mActivity.getGame().initDone && ! mActivity.isFinished)
            setGameMargins(height);
        adHeight = height;
        mActivity.getGameOverMessageController().setAdVisible(shouldShowAdTop & isShowingAd);
    }

    @Override
    public void onAdFail() {
        if (mActivity.isFinished)
            return;
        canShowAd = false;
        if (isShowingAd)
            mActivity.runOnUiThread(hideAD);
    }

    Runnable showAD = new Runnable() {
        @Override
        public void run() {
            if (isShowingAd)
                return;

            adWhirlLayout.setVisibility(View.VISIBLE);
            mRootLayout.removeView(adWhirlLayout);
            mRootLayout.addView(adWhirlLayout);
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
            mActivity.getGameOverMessageController().setAdVisible(false);
        }
    };

    Runnable moveAdBottom = new Runnable() {
        @Override
        public void run() {
            adWhirlLayout.setLayoutParams(lpDown);
            AdWhirlAdapter.setGoogleAdSenseExpandDirection("UP");
            mActivity.getTopButtons().alignTop();
            mActivity.getGameOverMessageController().setAdVisible(false);
        }
    };

    Runnable moveAdTop = new Runnable() {
        @Override
        public void run() {
            adWhirlLayout.setLayoutParams(lpUp);
            AdWhirlAdapter.setGoogleAdSenseExpandDirection("BOTTOM");
            mActivity.getTopButtons().alignUnderView(adWhirlLayout);
        }
    };
}

