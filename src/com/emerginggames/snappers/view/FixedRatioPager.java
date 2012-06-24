package com.emerginggames.snappers.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 2:31
 */
public class FixedRatioPager extends ViewPager{
    float mRatio = 0;
    int innerPaddingLeft = 0;
    int innerPaddingTop = 0;
    int innerPaddingRight = 0;
    int innerPaddingBottom = 0;

    public FixedRatioPager(Context context) {
        super(context);
    }

    public FixedRatioPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentChildOnTop(boolean currentChildOnTop) {
        setChildrenDrawingOrderEnabled(currentChildOnTop);
    }

    public void setRatio(float mRatio) {
        this.mRatio = mRatio;
        if (mRatio == 0)
            return;
        ViewGroup.LayoutParams _lp = getLayoutParams();
        if (_lp instanceof LinearLayout.LayoutParams){
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)_lp;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.weight = 0;
            setLayoutParams(lp);
        }
    }

    public void setInnerPaddings(int innerPaddingLeft, int innerPaddingTop, int innerPaddingRight, int innerPaddingBottom) {
        this.innerPaddingLeft = innerPaddingLeft;
        this.innerPaddingTop = innerPaddingTop;
        this.innerPaddingRight = innerPaddingRight;
        this.innerPaddingBottom = innerPaddingBottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio == 0){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int innerWidth = wSize - innerPaddingLeft - innerPaddingRight;
        int innerHeight = Math.round(innerWidth * mRatio);
        int newHeight = innerHeight + innerPaddingTop + innerPaddingBottom;
        if (newHeight > hSize){
            int overSize = newHeight - hSize;
            innerPaddingLeft += overSize/2;
            innerPaddingRight += overSize/2;
            newHeight = hSize;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(newHeight, hMode));
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        PagerAdapter adapter = getAdapter();
        View current = null;
        if (adapter instanceof ICurrentItemSelector)
            current = ((ICurrentItemSelector)adapter).getCurrentItemView();
        int cur = indexOfChild(current);

        if (cur == -1 || i < cur)
            return i;

         if (childCount-1 == i)
            return cur;
        return i+1;
    }
}
