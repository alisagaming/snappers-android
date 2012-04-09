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
    private static final String ADFREE = "adfree";
    private static final String MUSIC = "music";
    private static final String SOUND = "sound";
    private static final int INITIAL_HINTS = 10;
    Context context;
    private static UserPreferences instance;
    SharedPreferences prefs;

    //TODO: encrypt all sellable data


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
        return deI(prefs.getString(_S(HINTS), null), 0);
    }

    public void useHint(){
        addHints(-1);
    }

    public void addHints(int amount){
        int hints = getHintsRemaining();
        Editor editor = prefs.edit();
        editor.putString(_S(HINTS), _I(hints + amount));
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
        return deI(prefs.getString(_S(String.format(LEVEL_UNLOCK, pack.name)), null), 0);
    }
    
    public int getLevelUnlocked(int packId){
        return getLevelUnlocked(LevelPackTable.get(packId, context));
    }

    public void unlockNextLevelPack(LevelPack cur){
        if (cur.isPremium)
            return;
        LevelPack[] packs = LevelPackTable.getAllByPremium(context, false);
        for (int i=0; i< packs.length-1; i++){
            if (packs[i].id == cur.id){
                unlockLevelPack(packs[i + 1]);
                return;
            }
        }
    }

    public void unlockLevelPack(LevelPack pack){
        Editor editor = prefs.edit();
        editor.putString(_S(String.format(LEVEL_UNLOCK, pack.name)), _I(1));
        editor.commit();
    }

    public void lockLevelPack(LevelPack pack){
        Editor editor = prefs.edit();
        editor.remove(_S(String.format(LEVEL_UNLOCK, pack.name)));
        editor.commit();
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
        editor.putString(_S(String.format(LEVEL_UNLOCK, pack.name)), _I(currentLevel.number + 1));
        editor.commit();
    }

    public boolean isLevelSolved(Level level){
        int unlocked = getLevelUnlocked(level.packNumber);
        return level.number < unlocked;
    }

    public boolean isAdFree(){
        return deB(prefs.getString(_S(ADFREE), null), false);
    }

    public void setAdFree(boolean isAdFree){
        Editor editor = prefs.edit();
        editor.putString(_S(ADFREE), _B(isAdFree));
        editor.commit();
    }

    private void initialise(){
        if (prefs.getBoolean(INITIIALISED, false))
            return;

        unlockLevelPack(LevelPackTable.get(1, context));
        Editor editor = prefs.edit();
        editor.putString(_S(HINTS), _I(INITIAL_HINTS));
        editor.putBoolean(INITIIALISED, true);
        editor.commit();
    }

    public void setMusic(boolean enabled){
        Editor editor = prefs.edit();
        editor.putBoolean(MUSIC, enabled);
        editor.commit();
    }

    public boolean getMusic(){
        return prefs.getBoolean(MUSIC, true);
    }

    public void setSound(boolean enabled){
        Editor editor = prefs.edit();
        editor.putBoolean(SOUND, enabled);
        editor.commit();
    }

    public boolean getSound(){
        return prefs.getBoolean(SOUND, true);
    }

    private String _S(String s){
        //TODO: sypher s
        return s;
    }
    
    private String deS(String s){
        return s;
    }
    
    private String _I(int i){
        return _S(Integer.toString(i));
    }
    
    private String _B(boolean b){
        return _S(Boolean.toString(b));
    }
    
    private int deI(String s, int def){
        try {
            return Integer.parseInt(deS(s));
        }catch (Exception e){
            return def;
        }
    }
    
    private boolean deB(String s, boolean def){
        try {
            return Boolean.parseBoolean(deS(s));
        }catch (Exception e){
            return def;
        }
    }
}
