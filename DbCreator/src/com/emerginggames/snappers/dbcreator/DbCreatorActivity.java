package com.emerginggames.snappers.dbcreator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.emerginggames.snappers.data.DbCreatorOpenHelper;

public class DbCreatorActivity extends Activity
{
    boolean dbStarted = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dbStarted){
            dbStarted = true;
            ((TextView)findViewById(R.id.text1)).setText("starting db creation...");
            new DbCreatorOpenHelper(DbCreatorActivity.this).initializeDataBase();
            ((TextView)findViewById(R.id.text1)).setText("db creation done");
        }
    }
}
