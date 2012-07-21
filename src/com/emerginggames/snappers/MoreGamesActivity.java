package com.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.MoreGame;
import com.emerginggames.snappers.utils.MoreGamesUtil;
import com.emerginggames.snappers.view.MoreGamesAdapter;
import com.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 20.07.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class MoreGamesActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_more_games);

        MoreGamesAdapter adapter = new MoreGamesAdapter(getApplicationContext(), MoreGamesUtil.load(getApplicationContext()));
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SoundManager.getInstance(MoreGamesActivity.this).playButtonSound();
                MoreGame game = (MoreGame)view.getTag();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(game.url)));
            }
        });
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        ViewGroup.LayoutParams lp = findViewById(R.id.backButton).getLayoutParams();
        lp.width = lp.height = r.width() /5;
        ((TextView)findViewById(R.id.title)).setTypeface(Resources.getFont(getApplicationContext()));
        ((OutlinedTextView)findViewById(R.id.title)).setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
    }

    public void onBackButtonClick(View v){
        SoundManager.getInstance(this).playButtonSound();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ((SnappersApplication)getApplication()).setSwitchingActivities();
        ((SnappersApplication)getApplication()).activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((SnappersApplication)getApplication()).activityResumed(this);
    }
}