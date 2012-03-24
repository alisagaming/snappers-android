package ru.emerginggames.snappers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;
import ru.emerginggames.snappers.view.LevelListFragment;

import java.util.ArrayList;
import java.util.List;

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


    public LevelPageAdapter(FragmentManager fm, LevelPack pack, IOnItemSelectedListener listener) {
        super(fm);

        this.pack = pack;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        LevelListFragment item = new LevelListFragment(i * LEVELS_PER_PAGE + 1, pack, listener);
        fragments.put(i, item);
        return item;
    }

    @Override
    public int getCount() {
        return (pack.levels.length - 1)/ LEVELS_PER_PAGE + 1;
    }
}
