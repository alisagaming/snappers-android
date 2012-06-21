package com.emerginggames.snappers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import com.emerginggames.snappers.model.LevelPack;
import com.emerginggames.snappers.view.IOnItemSelectedListener;
import com.emerginggames.snappers.view.LevelListFragment;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.03.12
 * Time: 1:52
 */
public class LevelPageAdapter extends FragmentPagerAdapter{
    private static final int LEVELS_PER_PAGE = 25;
    LevelPack pack;
    private IOnItemSelectedListener listener;
    SparseArray<LevelListFragment> fragments = new SparseArray<LevelListFragment>();

    int innerPaddingLeft = 0;
    int innerPaddingTop = 0;
    int innerPaddingRight = 0;
    int innerPaddingBottom = 0;

    public void setInnerPaddings(int innerPaddingLeft, int innerPaddingTop, int innerPaddingRight, int innerPaddingBottom) {
        this.innerPaddingLeft = innerPaddingLeft;
        this.innerPaddingTop = innerPaddingTop;
        this.innerPaddingRight = innerPaddingRight;
        this.innerPaddingBottom = innerPaddingBottom;
    }

    public LevelPageAdapter(FragmentManager fm, LevelPack pack, IOnItemSelectedListener listener) {
        super(fm);

        this.pack = pack;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        LevelListFragment item = new LevelListFragment(i * LEVELS_PER_PAGE + 1, pack, listener);
        item.setInnerPaddings(innerPaddingLeft, innerPaddingTop, innerPaddingRight, innerPaddingBottom);
        fragments.put(i, item);
        return item;
    }

    @Override
    public int getCount() {
        return (pack.levelCount - 1)/ LEVELS_PER_PAGE + 1;
    }
}
