package com.emerginggames.snappers.view;

import android.app.Activity;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 19.06.12
 * Time: 23:35
 */
public class MyAdView extends AdView {
    OnMeasuredListener measuredListener;

    public MyAdView(Activity activity, AdSize adSize, String adUnitId) {
        super(activity, adSize, adUnitId);
    }

    public interface OnMeasuredListener{
        void onMeasured(int width, int height);
    }

    public void setOnMeasuredListener(OnMeasuredListener measuredListener) {
        this.measuredListener = measuredListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (measuredListener != null)
            measuredListener.onMeasured(getMeasuredWidth(), getMeasuredHeight());
    }
}
