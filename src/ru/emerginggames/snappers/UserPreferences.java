package ru.emerginggames.snappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.acra.ACRA;
import ru.emerginggames.snappers.data.CryptHelperAES;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.data.LevelTable;
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
    private static final String TAPJOY_ENABLED = "TAPJOY_ENABLED";
    private static final String INGAMEADS = "INGAMEADS";
    private static final String HINTS_TOUCHED = "HINTS_TOUCHED";
    private static final String G_IN_APP_INIT_DONE = "IN_APP_INIT_DONE";
    public static String Key1;
    public static String Key11;
    public static String Key21;
    public static String Key2;
    public static String Key3;
    public static String Key4;
    public static String Key5;

    private static final int INITIAL_HINTS = Settings.DEBUG? 10:2;
    Context context;
    private static UserPreferences instance;
    SharedPreferences prefs;
    DeviceUuidFactory factory;
    HintChangedListener hintChangedListener;

    public static UserPreferences getInstance(Context context){
        if (instance == null)
            return instance = new UserPreferences(context);
        //else if (context != null)
        //    instance.context = context;
        return instance;
    }

    public UserPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (LevelPackTable.getName().equals("vitaliy.suprun"))
            if (LevelPackTable.getHost().equals("gmail.com")){
                if (Key1 == null)
                    factory = new DeviceUuidFactory(context);
                getKey1();
                getKey2();
                getKey3();
            }
        initialise();
    }

    public static void setContext(Context context) {
        if (instance == null)
            instance = new UserPreferences(context);
        else if (context!= null)
            instance.context = context;
    }

    public void setTapjoyEnabled(boolean enabled){
        putBoolean(TAPJOY_ENABLED, enabled, TAPJOY_ENABLED);
    }

    public boolean isTapjoyEnabled(){
        return getBoolean(TAPJOY_ENABLED, false, TAPJOY_ENABLED);
    }

    public void setIngameAds(boolean enabled){
        putBoolean(INGAMEADS, enabled, INGAMEADS);
    }

    public boolean getIngameAds(){
        return getBoolean(INGAMEADS, false, INGAMEADS);
    }

    public void touchHints(){
        putBoolean(HINTS_TOUCHED, true, HINTS_TOUCHED);
    }

    public boolean areHintsTouched(){
        return getBoolean(HINTS_TOUCHED, false, HINTS_TOUCHED);
    }

    public int getHintsRemaining(){
        return getInt(HINTS, 0, HINTS);
    }

    public void setGInAppInitDone(boolean done){
        putBoolean(G_IN_APP_INIT_DONE, done, G_IN_APP_INIT_DONE);
    }

    public boolean isGInAppInitDone(){
        return getBoolean(G_IN_APP_INIT_DONE, false, G_IN_APP_INIT_DONE);
    }

    public void useHint(){
        addHints(-1);
    }

    public void addHints(int amount){
        int hintsRemaining = getHintsRemaining();
        setHints(amount + hintsRemaining);
        if (hintChangedListener!= null)
            hintChangedListener.onHintsChanged(hintsRemaining, hintsRemaining +amount);

    }

    public void setHints(int amount){
        putInt(HINTS, amount, HINTS);
        if (!areHintsTouched())
            touchHints();
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
        for (int i=0; i< packs.length-1; i++)
            if (packs[i].id == cur.id){
                unlockLevelPack(packs[i + 1]);
                return;
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
        return level.number < getLevelUnlocked(level.packNumber);
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
        putBoolean(MUSIC, enabled, MUSIC);
    }

    public boolean getMusic(){
        return getBoolean(MUSIC, true, MUSIC);
    }

    public void setSound(boolean enabled){
        putBoolean(SOUND, enabled, SOUND);
    }

    public boolean getSound(){
        return getBoolean(SOUND, true, SOUND);
    }

    private String _S(String s){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.encrypt(getKey3(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    private String deS(String s){
        try{
            return CryptHelperAES.decrypt(getKey3(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String _S(String s, String salt){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.encrypt(salt + getKey3(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String deS(String s, String salt){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.decrypt(salt + getKey3(), s);
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
        if (Key1 == null)
            Key1 = context.getResources().getString(R.string.app_name) + LevelPackTable.MAIL;
        Key11 = Key1;
        return Key1;
    }

    public String getKey3(){
        if (Key3 == null)
            Key3 = factory.getDeviceUuid().toString() + LevelPackTable.getHost();
        return Key3;
    }

    public String getKey12(){
        String res = Key11;
        Key11 = Key21;
        return Key21 = res;
    }

    public String getKey2(){
        if (Key2 == null)
            Key2 = LevelPackTable.getHost() + LevelTable.getMail();
        Key21 = Key2;
        return Key2;
    }

    public void setHintChangedListener(HintChangedListener hintChangedListener) {
        this.hintChangedListener = hintChangedListener;
    }

    public interface HintChangedListener{
        public void onHintsChanged(int old, int current);
    }
}
