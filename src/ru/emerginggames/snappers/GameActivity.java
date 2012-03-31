package ru.emerginggames.snappers;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ru.emerginggames.snappers.data.LevelTable;
import ru.emerginggames.snappers.gdx.Game;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.03.12
 * Time: 16:23
 */
public class GameActivity extends AndroidApplication {
    public static final boolean DEBUG = true;
    public static final String LEVEL_PARAM_TAG = "Level";
    public static final String LEVEL_PACK_PARAM_TAG = "Level pack";

    Game game;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources.context = this;
        Resources.loadFont(this);

        Intent intent = getIntent();
        Level level = (Level)intent.getSerializableExtra(LEVEL_PARAM_TAG);
        LevelPack pack = (LevelPack) intent.getSerializableExtra(LEVEL_PACK_PARAM_TAG);
        game = new Game();
        game.setStartLevel(level, pack);


        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;

        initialize(game, config);
    }

    public void launchStore(){
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }

    public void levelPackWon(){
        //TODO: do
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: do
    }
}