package ru.emerginggames.snappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 17:01
 */
public class UserPreferences {
    private static final String PREFERENCES = "Preferences";
    private static final String INITIIALISED = "Initialised";
    private static final String SCORE = "Score";
    private static final String HINTS = "Hints";
    private static final String LEVEL_UNLOCK = "Pack %s levels unlocked";
    private static final int INITIAL_HINTS = 10;
    Context context;
    private static UserPreferences instance;
    SharedPreferences prefs;


    public static UserPreferences getInstance(Context context){
        if (instance == null)
            return instance = new UserPreferences(context);
        else if (context != null)
            instance.context = context;
        return instance;
    }

    public UserPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        initialise();
    }

    public static void setContext(Context context) {
        if (instance == null)
            instance = new UserPreferences(context);
        else if (context!= null)
            instance.context = context;
    }

    
    public int getHintsRemaining(){
        return prefs.getInt(HINTS, 0);
    }

    public void useHint(){
        addHints(-1);
    }

    public void addHints(int amount){
        int hints = getHintsRemaining();
        Editor editor = prefs.edit();
        editor.putInt(HINTS, hints + amount);
        editor.commit();
    }
    
    public boolean isPackUnlocked(int id){
        return getLevelUnlocked(id) > 0;
    }

    public boolean isPackUnlocked(LevelPack pack){
        return getLevelUnlocked(pack) > 0;
    }

    public int getLevelUnlocked(LevelPack pack){
        if (pack == null)
            return 0;
        if (Settings.ENABLE_ALL_LEVELS)
            return 1000;
        return prefs.getInt(String.format(LEVEL_UNLOCK, pack.name), 0);
    }
    
    public int getLevelUnlocked(int packId){
        return getLevelUnlocked(LevelPackTable.get(packId, context));
    }

    public void unlockNextLevelPack(LevelPack cur){
        if (cur.isPremium)
            return;
        LevelPack[] packs = LevelPackTable.getAllNotPremium(context);
        for (int i=0; i< packs.length-1; i++){
            if (packs[i].id == cur.id){
                Editor editor = prefs.edit();
                editor.putInt(String.format(LEVEL_UNLOCK, packs[i+1].name), 100);
                editor.commit();
                return;
            }
        }
    }
    
    public int getScore(){
        return prefs.getInt(SCORE, 0);
    }

    public void addScore(int addScore){
        addScore += getScore();
        Editor editor = prefs.edit();
        editor.putInt(SCORE, addScore);
        editor.commit();
    }

    public void unlockNextLevel(Level currentLevel){
        LevelPack pack = LevelPackTable.get(currentLevel.packNumber, context);
        int unlocked = getLevelUnlocked(pack);
        if (unlocked> currentLevel.number)
            return;

        Editor editor = prefs.edit();
        editor.putInt(String.format(LEVEL_UNLOCK, pack.name), currentLevel.number + 1);
        editor.commit();
    }

    public boolean isLevelSolved(Level level){
        int unlocked = getLevelUnlocked(level.packNumber);
        return level.number < unlocked;
    }


    private void initialise(){
        if (prefs.getBoolean(INITIIALISED, false))
            return;

        Editor editor = prefs.edit();
        editor.putInt(HINTS, INITIAL_HINTS);
        LevelPack pack1 = LevelPackTable.get(1, context);
        editor.putInt(String.format(LEVEL_UNLOCK, pack1.name), 100);
        editor.putBoolean(INITIIALISED, true);
        editor.commit();
    }


}
