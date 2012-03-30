package ru.emerginggames.snappers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 16:27
 */
public class SelectLevelActivity extends FragmentActivity implements IOnItemSelectedListener {
    LevelPack pack;
    private LevelPageAdapter adapter;
    protected Thread bgLoadThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level);
        pack = LevelPackTable.get(1, this);
        pack.levels = LevelTable.getLevels(this, pack.id);

        adapter = new LevelPageAdapter(getSupportFragmentManager(), pack, this);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        com.viewpagerindicator.CirclePageIndicator
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        LevelPack pack2 = LevelPackTable.get(pack.id, this);
        pack.levelsUnlocked = pack2.levelsUnlocked;
        super.onResume();
        startBgPreload();
    }

    @Override
    public void onItemSelected(int number) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.LEVEL_PARAM_TAG, pack.levels[number - 1]);
        intent.putExtra(GameActivity.LEVEL_PACK_PARAM_TAG, pack);
        startActivity(intent);
    }

    protected void startBgPreload(){
        if (bgLoadThread != null)
            return;
        bgLoadThread = new Thread(){
            @Override
            public void run() {
                Resources.preloadBg(SelectLevelActivity.this.pack.background);
                Resources.preparePreload();
                SelectLevelActivity.this.bgLoadThread = null;
            }
        };
        bgLoadThread.run();

    }
}