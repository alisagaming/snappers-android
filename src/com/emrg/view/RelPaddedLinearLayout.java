package com.emrg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 01.04.12
 * Time: 0:49
 */
public class RelPaddedLinearLayout extends LinearLayout{
    float multiplier = 1;

    public RelPaddedLinearLayout(Context context) {
        super(context);
    }

    public RelPaddedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (hMode == MeasureSpec.EXACTLY && wMode == MeasureSpec.EXACTLY){
            int vPad = Math.round(wSize * multiplier); 
            int hPad = Math.round(hSize * multiplier);
            setPadding(vPad, hPad, vPad, hPad);
        }
            

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
