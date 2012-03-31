package ru.emerginggames.snappers.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 2:31
 */
public class SquaredPager extends ViewPager{
    public SquaredPager(Context context) {
        super(context);
    }

    public SquaredPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentChildOnTop(boolean currentChildOnTop) {
        setChildrenDrawingOrderEnabled(currentChildOnTop);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(wSize, hMode));
    }





    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        PagerAdapter adapter = getAdapter();
        View current = null;
        if (adapter instanceof ICurrentItemSelector)
            current = ((ICurrentItemSelector)adapter).getCurrentItemView();
        int cur = indexOfChild(current);
        if (cur == -1)
            return i;
        int res;
        if (i< cur)
            res = i;
        else if (childCount-1 == i)
            res = cur;
        else res = i+1;

        return res;

    }
}
