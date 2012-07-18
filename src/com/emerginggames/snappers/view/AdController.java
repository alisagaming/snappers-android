package com.emerginggames.snappers.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.emerginggames.snappers.GameActivity;
import com.emerginggames.snappers.UserPreferences;
import com.google.ads.*;
import com.emerginggames.snappers.R;
import com.emerginggames.snappers.Settings;
import com.emerginggames.snappers.gdx.Game;

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
    private static final long RETRY_DELAY = 30000;
    public RelativeLayout.LayoutParams lpUp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public RelativeLayout.LayoutParams lpDown = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    boolean isShowingAd = false;
    boolean shouldShowAdTop = false;
    boolean canShowAd = false;
    boolean shouldShowIngameAd;
    boolean canShowIngameAd = true;
    UserPreferences prefs;
    GameActivity activity;
    Game game;
    RelativeLayout rootLayout;
    private MyAdView adView;
    int width;
    int height;
    boolean isAdTop;
    Handler handler = new Handler();


    public AdController(GameActivity activity, Game game, RelativeLayout rootLayout) {
        this.activity = activity;
        this.game = game;
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
        updateAd.run();
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
        if (shouldShowIngameAd || !isShowingAd)
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
            if (height < game.getMarginBottom())
                canShowIngameAd = true;
            else if (height < game.getMaxMarginBottom()) {
                canShowIngameAd = true;
                game.resizeMarginBottom(height);
            } else canShowIngameAd = false;
            if (!canShowIngameAd)
                activity.runOnUiThread(hideAD);
        }
    }

    Runnable showAD = new Runnable() {
        @Override
        public void run() {
            if (isShowingAd)
                return;

            adView.setVisibility(View.VISIBLE);
            adView.bringToFront();
            //rootLayout.removeView(adView);
            //rootLayout.addView(adView);
            isShowingAd = true;
            //activity.getTopButtons().alignUnderView(adView);
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
        }
    };

    Runnable moveAdBottom = new Runnable() {
        @Override
        public void run() {
            adView.setLayoutParams(lpDown);
            activity.getTopButtons().alignTop();
            isAdTop = false;
        }
    };

    Runnable moveAdTop = new Runnable() {
        @Override
        public void run() {
            adView.setLayoutParams(lpUp);
            activity.getTopButtons().alignUnderView(adView);
            isAdTop = true;
        }
    };

    @Override
    public void onReceiveAd(Ad ad) {
        if (activity.isFinished)
            return;
        canShowAd = true;
        if (shouldShowAdTop) {
            if (!isShowingAd || !isAdTop)
                activity.runOnUiThread(moveAdTop);
            if (!isShowingAd)
                activity.runOnUiThread(showAD);
        } else if (shouldShowIngameAd && canShowIngameAd) {
            if (!isShowingAd || isAdTop)
                activity.runOnUiThread(moveAdBottom);
            if (!isShowingAd)
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
        scheduleNextAdGet();
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
            if (game.initDone && !activity.isFinished)
                setGameMargins(height);
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

    void scheduleNextAdGet(){
        handler.postDelayed(updateAd, RETRY_DELAY);
    }

    private Runnable updateAd = new Runnable() {
        @Override
        public void run() {
            AdRequest adRequest= new AdRequest();

            if (Settings.DEBUG){
                adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
                Context context = activity.getApplicationContext();
                String deviceId = getEncodedDeviceId(context);
                adRequest.addTestDevice(deviceId);
            }

            adView.loadAd(adRequest);
        }
    };
}


