package ru.emerginggames.snappers.utils;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.adapters.AdWhirlAdapter;
import com.adwhirl.util.AdWhirlUtil;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import org.acra.ErrorReporter;
import ru.emerginggames.snappers.Settings;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class MyAdWhirlLayout extends AdWhirlLayout {
    private String keyAdWhirl;

    // Added so we can tell the previous adapter that it is being destroyed.
    private AdWhirlAdapter previousAdapter;
    private AdWhirlAdapter currentAdapter;

    public IOnAdShowListener adShowListener;

    private boolean hasWindow;
    private boolean isScheduled;

    public boolean isLoadFailed = false;


    private static boolean enforceUpdate = false;

    public static void setEnforceUpdate(boolean enforceUpdate) {
        MyAdWhirlLayout.enforceUpdate = enforceUpdate;
    }

    public MyAdWhirlLayout(final Activity context, final String keyAdWhirl) {
        super(context, keyAdWhirl);
    }

    public MyAdWhirlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isAdAvailable(){
        return activeRation != null && !isLoadFailed;
    }

    public void setAdShowListener(IOnAdShowListener adShowListener) {
        this.adShowListener = adShowListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth = getMeasuredWidth();
        int newHeight = getMeasuredHeight();
        if (width != newWidth || height != newHeight )
            if (adShowListener != null)
                adShowListener.onAdSizeChanged(newWidth, newHeight);
    }

    @Override
    protected void init(final Activity context, final String keyAdWhirl) {
        this.activityReference = new WeakReference<Activity>(context);
        this.superViewReference = new WeakReference<RelativeLayout>(this);
        this.keyAdWhirl = keyAdWhirl;
        this.hasWindow = true;
        this.isScheduled = true;
        scheduler.schedule(new InitRunnable(this, keyAdWhirl), 0, TimeUnit.SECONDS);

        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        setMaxWidth(0);
        setMaxHeight(0);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE) {
            this.hasWindow = true;
            if (!this.isScheduled) {
                this.isScheduled = true;

                if (this.extra != null) {
                    rotateThreadedNow();
                } else {
                    scheduler.schedule(new InitRunnable(this, keyAdWhirl), 0, TimeUnit.SECONDS);
                }
            }
        } else {
            this.hasWindow = false;
        }
    }

    private void rotateAd() {
        if (!this.hasWindow && !enforceUpdate) {
            this.isScheduled = false;
            return;
        }
        enforceUpdate = false;

        if (Settings.DEBUG) Log.i(AdWhirlUtil.ADWHIRL, "Rotating Ad");
        nextRation = adWhirlManager.getRation();

        handler.post(new HandleAdRunnable(this));
    }

    // Initialize the proper ad view from nextRation
    private void handleAd() {
        // We shouldn't ever get to a state where nextRation is null unless all
        // networks fail
        if (nextRation == null) {
            Log.e(AdWhirlUtil.ADWHIRL, "nextRation is null!");
            rotateThreadedDelayed();
            return;
        }

        String rationInfo = String
                .format(
                        "Showing ad:\n\tnid: %s\n\tname: %s\n\ttype: %d\n\tkey: %s\n\tkey2: %s",
                        nextRation.nid, nextRation.name, nextRation.type, nextRation.key,
                        nextRation.key2);
        if (Settings.DEBUG) Log.d(AdWhirlUtil.ADWHIRL, rationInfo);

        try {
            // Tell the previous adapter that its view will be destroyed.
            if (this.previousAdapter != null) {
                this.previousAdapter.willDestroy();
            }
            this.previousAdapter = this.currentAdapter;
            this.currentAdapter = AdWhirlAdapter.handle(this, nextRation);
        } catch (Throwable t) {
            if (Settings.DEBUG) Log.w(AdWhirlUtil.ADWHIRL, "Caught an exception in adapter:", t);
            rollover();
            return;
        }
    }

    // Rotate immediately
    public void rotateThreadedNow() {
        if (Settings.DEBUG) Log.d("ADWHIRL", "rotateThreadedNow");
        enforceUpdate = true;
        scheduler.schedule(new RotateAdRunnable(this), 0, TimeUnit.SECONDS);
    }

    // Rotate in extra.cycleTime seconds
    public void rotateThreadedDelayed() {
        if (Settings.DEBUG) Log.d("ADWHIRL", "rotateThreadedDelayed");
        if (Settings.DEBUG) Log.d(AdWhirlUtil.ADWHIRL, "Will call rotateAd() in " + extra.cycleTime
                + " seconds");
        scheduler.schedule(new RotateAdRunnable(this), extra.cycleTime,
                TimeUnit.SECONDS);
    }

    // Remove old views and push the new one
    @Override
    public void pushSubView(ViewGroup subView) {
        isLoadFailed = false;
        if (Settings.SEND_EXTENDED_AD_INFO)
            ErrorReporter.getInstance().handleSilentException(new Exception("successfully received ad"));
        super.pushSubView(subView);
        invalidate();
        if (adShowListener != null)
            adShowListener.onAdShow();
    }

    public void rollover() {
        isLoadFailed = true;
        if (Settings.SEND_EXTENDED_AD_INFO)
            ErrorReporter.getInstance().handleSilentException(new Exception("failed to receive ad"));
        if (adShowListener != null)
            adShowListener.onAdFail();
        nextRation = adWhirlManager.getRollover();
        handler.post(new HandleAdRunnable(this));
    }

    private static class InitRunnable implements Runnable {
        private WeakReference<MyAdWhirlLayout> adWhirlLayoutReference;
        private String keyAdWhirl;

        public InitRunnable(MyAdWhirlLayout adWhirlLayout, String keyAdWhirl) {
            adWhirlLayoutReference = new WeakReference<MyAdWhirlLayout>(adWhirlLayout);
            this.keyAdWhirl = keyAdWhirl;
        }

        public void run() {
            MyAdWhirlLayout adWhirlLayout = adWhirlLayoutReference.get();
            if (adWhirlLayout != null) {
                Activity activity = adWhirlLayout.activityReference.get();
                if (activity == null) {
                    return;
                }

                if (adWhirlLayout.adWhirlManager == null) {
                    adWhirlLayout.adWhirlManager = new AdWhirlManager(
                            new WeakReference<Context>(activity.getApplicationContext()),
                            keyAdWhirl);
                }

                if (!adWhirlLayout.hasWindow && !MyAdWhirlLayout.enforceUpdate) {
                    adWhirlLayout.isScheduled = false;
                    return;
                }

                adWhirlLayout.adWhirlManager.fetchConfig();
                adWhirlLayout.extra = adWhirlLayout.adWhirlManager.getExtra();

                if (adWhirlLayout.extra == null) {
                    adWhirlLayout.scheduler.schedule(this, 30, TimeUnit.SECONDS);
                } else {
                    adWhirlLayout.rotateAd();
                }
            }
        }
    }

    // Callback for external networks
    private static class HandleAdRunnable implements Runnable {
        private WeakReference<MyAdWhirlLayout> adWhirlLayoutReference;

        public HandleAdRunnable(MyAdWhirlLayout adWhirlLayout) {
            adWhirlLayoutReference = new WeakReference<MyAdWhirlLayout>(adWhirlLayout);
        }

        public void run() {
            MyAdWhirlLayout adWhirlLayout = adWhirlLayoutReference.get();
            if (adWhirlLayout != null) {
                adWhirlLayout.handleAd();
            }
        }
    }


    private static class RotateAdRunnable implements Runnable {
        private WeakReference<MyAdWhirlLayout> adWhirlLayoutReference;

        public RotateAdRunnable(MyAdWhirlLayout adWhirlLayout) {
            adWhirlLayoutReference = new WeakReference<MyAdWhirlLayout>(adWhirlLayout);
        }

        public void run() {
            MyAdWhirlLayout adWhirlLayout = adWhirlLayoutReference.get();
            if (adWhirlLayout != null) {
                adWhirlLayout.rotateAd();
            }
        }
    }
}
