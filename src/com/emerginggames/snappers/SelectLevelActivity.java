package com.emerginggames.snappers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.view.FixedRatioPager;
import com.emerginggames.snappers.view.LevelPageViewAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import com.emerginggames.snappers.data.LevelTable;
import com.emerginggames.snappers.model.Level;
import com.emerginggames.snappers.model.LevelPack;
import com.emerginggames.snappers.view.IOnItemSelectedListener;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 16:27
 */
public class SelectLevelActivity extends PaginatedSelectorActivity implements IOnItemSelectedListener {
    public static final String LEVEL_PACK_TAG = "Level pack";
    LevelPack pack;
    private LevelPageViewAdapter adapter;
    AsyncTask<Integer, Integer, Integer> preloadTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!intent.hasExtra(LEVEL_PACK_TAG))
            finish();
        pack = (LevelPack) intent.getSerializableExtra(LEVEL_PACK_TAG);
        pack.levelCount = LevelTable.countLevels(this, pack.id);

        adapter = new LevelPageViewAdapter(getApplicationContext(), pack, this);

        FixedRatioPager pager = (FixedRatioPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setPageMargin(20);
        pager.setRatio(1);
        int padV = getWindowManager().getDefaultDisplay().getWidth() / 40;
        pager.setInnerPaddings(padV, 0, padV, 0);
        adapter.setInnerPaddings(padV, 0, padV, 0);

        com.viewpagerindicator.CirclePageIndicator
                mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreload();
        adapter.refreshItems();
    }

    @Override
    public void onItemSelected(int number) {
        SoundManager.getInstance(this).playButtonSound();
        ((SnappersApplication) getApplication()).setSwitchingActivities();
        Intent intent = new Intent(this, GameActivity.class);
        Level level = LevelTable.getLevel(this, number, pack);
        intent.putExtra(GameActivity.LEVEL_PARAM_TAG, level);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
            finish();
    }

    protected void startPreload() {
        if (preloadTask != null)
            return;

        findViewById(R.id.resLoadIndicator).setBackgroundColor(0x80800000);
        findViewById(R.id.bgLoadIndicator).setBackgroundColor(0x80800000);

        preloadTask = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                if (Settings.DEBUG) {
                    findViewById(R.id.resLoadIndicator).setVisibility(View.VISIBLE);
                    findViewById(R.id.bgLoadIndicator).setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                Resources.preparePreload();
                this.publishProgress(1);
                Resources.preloadBg(SelectLevelActivity.this.pack.background);
                this.publishProgress(2);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (Settings.DEBUG) {
                    if (values[0] == 1)
                        findViewById(R.id.resLoadIndicator).setBackgroundColor(0x80008000);
                    else if (values[0] == 2)
                        findViewById(R.id.bgLoadIndicator).setBackgroundColor(0x80008000);
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                SelectLevelActivity.this.preloadTask = null;
            }
        };

        preloadTask.execute();
    }
}