package ru.emerginggames.snappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.acra.ACRA;
import ru.emerginggames.snappers.data.CryptHelperAES;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.utils.DeviceUuidFactory;

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

    private static final int INITIAL_HINTS = Settings.DEBUG? 10:3;
    Context context;
    private static UserPreferences instance;
    SharedPreferences prefs;
    DeviceUuidFactory factory;

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
        if (LevelPackTable.getName().equals("viitaliy.suprun"))
            if (LevelPackTable.getHost().equals("gmail.com"))
                factory = new DeviceUuidFactory(context);
        initialise();
    }

    public static void setContext(Context context) {
        if (instance == null)
            instance = new UserPreferences(context);
        else if (context!= null)
            instance.context = context;
    }

    
    public int getHintsRemaining(){
        return getInt(HINTS, 0, HINTS);
    }

    public void useHint(){
        addHints(-1);
    }

    public void addHints(int amount){
        int hints = getHintsRemaining();
        putInt(HINTS, hints + amount, HINTS);
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
        return getInt(String.format(LEVEL_UNLOCK, pack.name), 0, pack.name);
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
        putInt(String.format(LEVEL_UNLOCK, pack.name), 1, pack.name);
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
        if (currentLevel.pack == null)
        currentLevel.pack = LevelPackTable.get(currentLevel.packNumber, context);
        int unlocked = getLevelUnlocked(currentLevel.pack);
        if (unlocked> currentLevel.number)
            return;

        putInt(String.format(LEVEL_UNLOCK, currentLevel.pack.name), currentLevel.number + 1, currentLevel.pack.name);
    }

    public boolean isLevelSolved(Level level){
        int unlocked = getLevelUnlocked(level.packNumber);
        return level.number < unlocked;
    }

    public boolean isAdFree(){
        return  getBoolean(ADFREE, false, ADFREE);
    }

    public void setAdFree(boolean isAdFree){
        putBoolean(ADFREE, isAdFree, ADFREE);
    }

    private void initialise(){
        if (getBoolean(INITIIALISED, false, INITIIALISED))
            return;

        unlockLevelPack(LevelPackTable.get(1, context));
        putInt(HINTS, INITIAL_HINTS, HINTS);
        putBoolean(INITIIALISED, true, INITIIALISED);

        ACRA.getACRASharedPreferences().edit().putBoolean(ACRA.PREF_ENABLE_DEVICE_ID, false).commit();
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
        try{
            return CryptHelperAES.encrypt(getKey2(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    private String deS(String s){
        try{
            return CryptHelperAES.decrypt(getKey2(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String _S(String s, String salt){
        try{
            return CryptHelperAES.encrypt(salt + getKey2(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String deS(String s, String salt){
        try{
            return CryptHelperAES.decrypt(salt + getKey2(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private int getInt(String key, int def, String salt){
        try {
            return Integer.parseInt(deS(prefs.getString(_S(key), null), salt));
        }catch (Exception e){
            return def;
        }
    }

    private void putInt(String key, int val, String salt){
        Editor editor = prefs.edit();
        editor.putString(_S(key), _S(Integer.toString(val), salt));
        editor.commit();
    }

    private boolean getBoolean(String key, boolean def, String salt){
        try {
            return Boolean.parseBoolean(deS(prefs.getString(_S(key), null), salt));
        }catch (Exception e){
            return def;
        }
    }

    private void putBoolean(String key, boolean val, String salt){
        Editor editor = prefs.edit();
        editor.putString(_S(key), _S(Boolean.toString(val), salt));
        editor.commit();
    }

    public String getKey1(){
        return context.getResources().getString(R.string.app_name) + LevelPackTable.MAIL;
    }

    public String getKey2(){
        return factory.getDeviceUuid().toString() + LevelPackTable.getHost();
    }
}
