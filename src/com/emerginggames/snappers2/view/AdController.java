package com.emerginggames.snappers2.view;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.emerginggames.snappers2.GameActivity;
import com.emerginggames.snappers2.UserPreferences;
import com.emrg.view.MyAdView;
import com.google.ads.*;
import com.emerginggames.snappers2.R;
import com.emerginggames.snappers2.Settings;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 19.06.12
 * Time: 20:03
 */
public class AdController implements AdListener, MyAdView.OnMeasuredListener {
    //private static String MY_AD_UNIT_ID = "df038f9ac5854e20";
    public RelativeLayout.LayoutParams lpUp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public RelativeLayout.LayoutParams lpDown = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    boolean isShowingAd = false;
    boolean shouldShowAdTop = false;
    boolean canShowAd = false;
    boolean shouldShowIngameAd;
    boolean canShowIngameAd = true;
    UserPreferences prefs;
    GameActivity activity;
    RelativeLayout rootLayout;
    private MyAdView adView;
    int width;
    int height;


    public AdController(GameActivity activity, RelativeLayout rootLayout) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        prefs = UserPreferences.getInstance(activity.getApplicationContext());
        lpUp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpUp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpDown.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpDown.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        shouldShowIngameAd = prefs.getIngameAds();

        adView = new MyAdView(activity, AdSize.BANNER, Settings.getAdMobKey(activity));

        adView.setLayoutParams(shouldShowIngameAd ? lpDown : lpUp);

        if (!shouldShowIngameAd)
            adView.setVisibility(View.INVISIBLE);

        adView.setId(R.id.adCont);

        rootLayout.addView(adView);
        AdRequest adRequest= new AdRequest();
/*        Set<String> keyWords = new HashSet<String>();
        keyWords.add("game");
        keyWords.add("puzzle");
        adRequest.setKeywords(keyWords);*/

        if (Settings.DEBUG){
            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
            Context context = activity.getApplicationContext();
            String deviceId = getEncodedDeviceId(context);
            adRequest.addTestDevice(deviceId);
        }

        adView.loadAd(adRequest);
        adView.setAdListener(this);
        adView.setOnMeasuredListener(this);
    }

    public void destroy() {
        if (activity.isFinished)
            if (adView != null) {
                adView.destroy();
            }
        adView = null;
    }

    public void finish() {
        rootLayout.removeView(adView);
    }

    public void showAdTop() {
        shouldShowAdTop = true;

        if (!canShowAd)
            return;
        if (shouldShowIngameAd)
            activity.runOnUiThread(moveAdTop);
        if (!isShowingAd)
            activity.runOnUiThread(showAD);
    }

    public void hideAdTop() {
        shouldShowAdTop = false;
        if (shouldShowIngameAd)
            activity.runOnUiThread(moveAdBottom);
        else
            activity.runOnUiThread(hideAD);
    }

    public void setGameMargins(int height) {
        if (height == 0)
            height = adView.getHeight();

        if (shouldShowIngameAd) {
            if (height < activity.getGame().getMarginBottom())
                canShowIngameAd = true;
            else if (height < activity.getGame().getMaxMarginBottom()) {
                canShowIngameAd = true;
                activity.getGame().resizeMarginBottom(height);
            }
            else canShowIngameAd = false;
            if (!canShowIngameAd)
                activity.runOnUiThread(hideAD);
        }
    }

    public int getAdHeight(){
        return isShowingAd ? height : 0;
    }

    Runnable showAD = new Runnable() {
        @Override
        public void run() {
            if (isShowingAd)
                return;

            adView.setVisibility(View.VISIBLE);
            rootLayout.removeView(adView);
            rootLayout.addView(adView);
            isShowingAd = true;
            activity.getTopButtons().alignUnderView(adView);
        }
    };

    Runnable hideAD = new Runnable() {
        @Override
        public void run() {
            if (!isShowingAd)
                return;

            adView.setVisibility(View.INVISIBLE);
            isShowingAd = false;
            activity.getTopButtons().alignTop();
            activity.getGameOverMessageController().setAdVisible(false);
        }
    };

    Runnable moveAdBottom = new Runnable() {
        @Override
        public void run() {
            adView.setLayoutParams(lpDown);
            activity.getTopButtons().alignTop();
            activity.getGameOverMessageController().setAdVisible(false);
        }
    };

    Runnable moveAdTop = new Runnable() {
        @Override
        public void run() {
            adView.setLayoutParams(lpUp);
            activity.getTopButtons().alignUnderView(adView);
        }
    };

    @Override
    public void onReceiveAd(Ad ad) {
        if (activity.isFinished)
            return;
        canShowAd = true;
        if (shouldShowAdTop) {
            showAdTop();
        } else if (shouldShowIngameAd && canShowIngameAd) {
            activity.runOnUiThread(moveAdBottom);
            activity.runOnUiThread(showAD);
        }
    }

    @Override
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {
        if (activity.isFinished)
            return;
        canShowAd = false;
        if (isShowingAd)
            activity.runOnUiThread(hideAD);
    }

    @Override
    public void onPresentScreen(Ad ad) {
    }

    @Override
    public void onDismissScreen(Ad ad) {
    }

    @Override
    public void onLeaveApplication(Ad ad) {
    }

    @Override
    public void onMeasured(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            if (activity.getGame().initDone && !activity.isFinished)
                setGameMargins(height);
            activity.getGameOverMessageController().setAdVisible(shouldShowAdTop & isShowingAd);
        }
    }

    public static String getEncodedDeviceId(Context context) {
        String androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        String hashedId;
        if ((androidId == null) || isEmulator()) {
            hashedId = md5("emulator");
        } else {
            hashedId = md5(androidId);
        }

        if (hashedId == null) {
            return null;
        }

        return hashedId.toUpperCase(Locale.US);
    }

    public static boolean isEmulator() {
        return (Build.BOARD.equals("unknown")
                && Build.DEVICE.equals("generic")
                && Build.BRAND.equals("generic"));
    }

    private static String md5(String val) {
        String result = null;

        if ((val != null) && (val.length() > 0)) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(val.getBytes(), 0, val.length());
                result = String.format("%032X", new BigInteger(1, md5.digest()));
            } catch (NoSuchAlgorithmException nsae) {
                result = val.substring(0, 32);
            }
        }

        return result;
    }
}


