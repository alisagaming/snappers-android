package com.emerginggames.bestpuzzlegame;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.emerginggames.bestpuzzlegame.data.LevelPackTable;
import com.emrg.view.FixedRatioPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.emerginggames.bestpuzzlegame.data.LevelTable;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.emerginggames.bestpuzzlegame.model.Level;
import com.emerginggames.bestpuzzlegame.model.LevelPack;
import com.emrg.view.IOnItemSelectedListener;
import com.emrg.view.ImageView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 16:27
 */
public class SelectLevelActivity extends PaginatedSelectorActivity implements IOnItemSelectedListener {
    public static final String LEVEL_PACK_TAG = "Level pack";
    public static final String FIRST_RUN_TAG = "first run";

    LevelPack pack;
    private LevelPageAdapter adapter;
    AsyncTask<Integer, Integer, Integer> preloadTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!intent.hasExtra(LEVEL_PACK_TAG))
            pack = LevelPackTable.get(1, getApplicationContext());
        else
            pack = (LevelPack) intent.getSerializableExtra(LEVEL_PACK_TAG);
        pack.levelCount = LevelTable.countLevels(this, pack.id);

        adapter = new LevelPageAdapter(getSupportFragmentManager(), pack, this);

        FixedRatioPager pager = (FixedRatioPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setPageMargin(20);
        pager.setRatio(1);
        int padV = getWindowManager().getDefaultDisplay().getWidth() / 20;
        pager.setInnerPaddings(padV, padV / 3, padV, padV / 3);
        adapter.setInnerPaddings(padV, padV / 3, padV, padV / 3);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);

        int dotposition= pack.background.lastIndexOf(".");
        String res  = pack.background.substring(0,dotposition);

        int id = getResources().getIdentifier(res, "drawable", getPackageName());
        if (id > 0)
            ((ImageView)findViewById(R.id.bgImage)).setImageResource(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreload();
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

    @Override
    public void onBackButtonClick(View v) {
        super.onBackButtonClick(v);
        if (getIntent().hasExtra(FIRST_RUN_TAG))
            startActivity(new Intent(this, SelectPackActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().hasExtra(FIRST_RUN_TAG)){
            startActivity(new Intent(this, SelectPackActivity.class));
        }
    }
}