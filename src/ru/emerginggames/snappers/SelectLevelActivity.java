package ru.emerginggames.snappers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.FixedRatioPager;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 16:27
 */
public class SelectLevelActivity extends PaginatedSelectorActivity implements IOnItemSelectedListener {
    public static final String LEVEL_PACK_TAG = "Level pack";
    LevelPack pack;
    private LevelPageAdapter adapter;
    AsyncTask<Integer, Integer, Integer> preloadTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!intent.hasExtra(LEVEL_PACK_TAG))
            finish();
        pack = (LevelPack)intent.getSerializableExtra(LEVEL_PACK_TAG);
        pack.levelCount = LevelTable.countLevels(this, pack.id);

        adapter = new LevelPageAdapter(getSupportFragmentManager(), pack, this);

        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setPageMargin(20);
        pager.setRatio(1);


        com.viewpagerindicator.CirclePageIndicator
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        
        View footer = findViewById(R.id.footer);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footer.getLayoutParams();
        lp.weight = 1;
        footer.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
        LevelPack pack2 = LevelPackTable.get(pack.id, this);
        pack.levelsUnlocked = pack2.levelsUnlocked;
        super.onResume();
        startPreload();

    }

    @Override
    public void onItemSelected(int number) {
        Intent intent = new Intent(this, GameActivity.class);
        Level level = LevelTable.getLevel(this, number, pack.id);
        intent.putExtra(GameActivity.LEVEL_PARAM_TAG, level);
        intent.putExtra(GameActivity.LEVEL_PACK_PARAM_TAG, pack);
        startActivity(intent);
    }
    


    protected void startPreload(){
        if (preloadTask != null)
            return;

        findViewById(R.id.resLoadIndicator).setBackgroundColor(0x80800000);
        findViewById(R.id.bgLoadIndicator).setBackgroundColor(0x80800000);

        preloadTask = new AsyncTask<Integer, Integer, Integer>(){
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
                if (values[0] == 1)
                    findViewById(R.id.resLoadIndicator).setBackgroundColor(0x80008000);
                else if (values[0] == 2)
                    findViewById(R.id.bgLoadIndicator).setBackgroundColor(0x80008000);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                SelectLevelActivity.this.preloadTask = null;
            }
        };

        preloadTask.execute();

    }
}