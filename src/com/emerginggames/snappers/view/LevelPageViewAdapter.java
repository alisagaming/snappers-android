package com.emerginggames.snappers.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.emerginggames.snappers.model.LevelPack;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 03.08.12
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class LevelPageViewAdapter extends PagerAdapter {

    private static final int LEVELS_PER_PAGE = 25;
    LevelPack pack;
    private IOnItemSelectedListener listener;
    SparseArray<LevelListView> items = new SparseArray<LevelListView>();
    Context context;

    int innerPaddingLeft = 0;
    int innerPaddingTop = 0;
    int innerPaddingRight = 0;
    int innerPaddingBottom = 0;

    public LevelPageViewAdapter(Context context, LevelPack pack, IOnItemSelectedListener listener) {
        this.pack = pack;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (pack.levelCount - 1)/ LEVELS_PER_PAGE + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setInnerPaddings(int innerPaddingLeft, int innerPaddingTop, int innerPaddingRight, int innerPaddingBottom) {
        this.innerPaddingLeft = innerPaddingLeft;
        this.innerPaddingTop = innerPaddingTop;
        this.innerPaddingRight = innerPaddingRight;
        this.innerPaddingBottom = innerPaddingBottom;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LevelListView item = new LevelListView(context,  position * LEVELS_PER_PAGE + 1, pack, listener);
        item.setPadding(innerPaddingLeft, innerPaddingTop, innerPaddingRight, innerPaddingBottom);
        items.put(position, item);
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        items.remove(position);
    }

    public void refreshItems(){
        for(int i = 0; i < items.size(); i++)
            items.valueAt(i).refresh();
    }
}
