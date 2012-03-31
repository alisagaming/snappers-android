package ru.emerginggames.snappers;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;
import ru.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 16:27
 */
public class SelectLevelActivity extends FragmentActivity implements IOnItemSelectedListener {
    LevelPack pack;
    private LevelPageAdapter adapter;
    AsyncTask<Integer, Integer, Integer> preloadTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.select_level);
        pack = LevelPackTable.get(1, this);
        pack.levels = LevelTable.getLevels(this, pack.id);

        adapter = new LevelPageAdapter(getSupportFragmentManager(), pack, this);

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        com.viewpagerindicator.CirclePageIndicator
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);

        Resources.loadFont(this);
        OutlinedTextView scoreView = (OutlinedTextView)findViewById(R.id.score);
        scoreView.setStroke(Color.BLACK, 2);
        scoreView.setTypeface(Resources.font);
        int textSize = getWindowManager().getDefaultDisplay().getWidth()/10;
        scoreView.setTextSize(textSize);
        scoreView.setPadding(0, textSize/4, textSize/4, 0);
        int rootPadding = getWindowManager().getDefaultDisplay().getHeight()/40;
        findViewById(R.id.root).setPadding(0, 0 , 0, rootPadding);
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
        intent.putExtra(GameActivity.LEVEL_PARAM_TAG, pack.levels[number - 1]);
        intent.putExtra(GameActivity.LEVEL_PACK_PARAM_TAG, pack);
        startActivity(intent);
    }

    public void onStoreButtonClick(View v){
        startActivity(new Intent(this, StoreActivity.class));
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