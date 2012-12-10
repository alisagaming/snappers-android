package com.emerginggames.snappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.emerginggames.snappers.utils.OnlineSettings;
import org.acra.ACRA;
import com.emerginggames.snappers.data.CryptHelperAES;
import com.emerginggames.snappers.data.LevelPackTable;
import com.emerginggames.snappers.data.LevelTable;
import com.emerginggames.snappers.model.Level;
import com.emerginggames.snappers.model.LevelPack;
import com.emerginggames.snappers.utils.DeviceUuidFactory;

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
    private static final String DONT_SHOW_APPRATER = "dontshowagain";
    private static final String LAST_APP_RATED = "LAST_DATE_APP_RATED";
    private static final String LATEST_VERSION = "latestVersion";
    private static final String MORE_GAMES_FREQ = "moreGamesFreq";
    private static final String FB_URL = "fbUrl";
    private static final String TWITTER_URL = "twUrl";
    private static final String FB_LIKED = "fbLiked";
    private static final String TW_FOLLOWED = "twFollowed";
    private static final String DECRYPTED = "decrypted";
    public static String Key1;
    public static String Key11;
    public static String Key21;
    public static String Key2;
    public static String Key3;
    public static String Key4;
    public static String Key5;
    private static Object hintSync = new Object();

    private static final int INITIAL_HINTS = Settings.DEBUG ? 10 : 2;
    Context context;
    private static UserPreferences instance;
    SharedPreferences prefs;
    DeviceUuidFactory factory;
    HintChangedListener hintChangedListener;
    private boolean decryptedAll = false;

    public static UserPreferences getInstance(Context context) {
        if (instance == null)
            return instance = new UserPreferences(context.getApplicationContext());
        //else if (context != null)
        //    instance.context = context;
        return instance;
    }

    public UserPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (LevelPackTable.getName().equals("vitaliy.suprun"))
            if (LevelPackTable.getHost().equals("gmail.com")) {
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
        else if (context != null)
            instance.context = context;
    }

    public boolean isTapjoyEnabled() {
        return prefs.getBoolean(TAPJOY_ENABLED, false);
    }

    public boolean getIngameAds() {
        return prefs.getBoolean(INGAMEADS, false);
    }

    public void touchHints() {
        putBoolean(HINTS_TOUCHED, true);
    }

    public boolean areHintsTouched() {
        return getBoolean(HINTS_TOUCHED, false, HINTS_TOUCHED);
    }

    public int getHintsRemaining() {
        return getInt(HINTS, 0, HINTS);
    }

    public void setGInAppInitDone(boolean done) {
        putBoolean(G_IN_APP_INIT_DONE, done);
    }

    public boolean isGInAppInitDone() {
        return getBoolean(G_IN_APP_INIT_DONE, false, G_IN_APP_INIT_DONE);
    }

    public void useHint() {
        addHints(-1);
    }

    public void addHints(int amount) {
        synchronized (hintSync) {
            int hintsRemaining = getHintsRemaining();
            putInt(HINTS, amount + hintsRemaining);
            if (hintChangedListener != null)
                hintChangedListener.onHintsChanged(hintsRemaining, hintsRemaining + amount);
            if (!areHintsTouched())
                touchHints();
        }
    }

    public boolean isPackUnlocked(int id) {
        return getLevelUnlocked(id) > 0;
    }

    public boolean isPackUnlocked(LevelPack pack) {
        return getLevelUnlocked(pack) > 0;
    }

    public int getLevelUnlocked(LevelPack pack) {
        if (pack == null)
            return 0;
        if (Settings.ENABLE_ALL_LEVELS)
            return 1000;
        return getInt(String.format(LEVEL_UNLOCK, pack.name), 0, pack.name);
    }

    public int getLevelUnlocked(int packId) {
        return getLevelUnlocked(LevelPackTable.get(packId, context));
    }

    public void unlockNextLevelPack(LevelPack cur) {
        if (cur.isPremium)
            return;
        LevelPack[] packs = LevelPackTable.getAllByPremium(context, false);
        for (int i = 0; i < packs.length - 1; i++)
            if (packs[i].id == cur.id) {
                unlockLevelPack(packs[i + 1]);
                return;
            }
    }

    public void unlockLevelPack(LevelPack pack) {
        putInt(String.format(LEVEL_UNLOCK, pack.name), 1);
    }

    public void lockLevelPack(LevelPack pack) {
        Editor editor = prefs.edit();
        editor.remove(_S(String.format(LEVEL_UNLOCK, pack.name)));
        editor.commit();
    }

    public int getScore() {
        return prefs.getInt(SCORE, 0);
    }

    public void addScore(int addScore) {
        addScore += getScore();
        Editor editor = prefs.edit();
        editor.putInt(SCORE, addScore);
        editor.commit();
    }

    public void unlockNextLevel(Level currentLevel) {
        if (currentLevel.pack == null)
            currentLevel.pack = LevelPackTable.get(currentLevel.packNumber, context);
        int unlocked = getLevelUnlocked(currentLevel.pack);
        if (unlocked > currentLevel.number)
            return;

        putInt(String.format(LEVEL_UNLOCK, currentLevel.pack.name), currentLevel.number + 1);
    }

    public boolean isLevelSolved(Level level) {
        return level.number < getLevelUnlocked(level.packNumber);
    }

    public boolean isAdFree() {
        return Settings.NO_ADS || getBoolean(ADFREE, false, ADFREE);
    }

    public void setAdFree(boolean isAdFree) {
        putBoolean(ADFREE, isAdFree);
    }

    private void initialise() {
        decryptAll();
        if (getBoolean(INITIIALISED, false, INITIIALISED))
            return;

        unlockLevelPack(LevelPackTable.get(1, context));
        putInt(HINTS, INITIAL_HINTS);
        putBoolean(INITIIALISED, true);

        ACRA.getACRASharedPreferences().edit().putBoolean(ACRA.PREF_ENABLE_DEVICE_ID, false).commit();
    }

    public void setMusic(boolean enabled) {
        putBoolean(MUSIC, enabled);
    }

    public boolean getMusic() {
        return getBoolean(MUSIC, true, MUSIC);
    }

    public void setSound(boolean enabled) {
        putBoolean(SOUND, enabled);
    }

    public boolean getSound() {
        return getBoolean(SOUND, true, SOUND);
    }

    public boolean canShowAppRater() {
        return !prefs.getBoolean(DONT_SHOW_APPRATER, false);
    }

    public void dontShowApprater() {
        prefs.edit().putBoolean(DONT_SHOW_APPRATER, true).commit();
    }

    public long getLastAskAppRate() {
        return prefs.getLong(LAST_APP_RATED, 0);
    }

    public void setLastAskedToRateApp(long time) {
        prefs.edit().putLong(LAST_APP_RATED, time).commit();
    }

    public void saveSettings(OnlineSettings.SettingsData data) {
        Editor editor = prefs.edit();
        if (prefs.getBoolean(TAPJOY_ENABLED, false) != data.tapJoy)
            editor.putBoolean(TAPJOY_ENABLED, data.tapJoy);
        if (prefs.getBoolean(INGAMEADS, false) != data.inGameAds)
            editor.putBoolean(INGAMEADS, data.inGameAds);
        if (prefs.getInt(LATEST_VERSION, 0) != data.latestVersion)
            editor.putInt(LATEST_VERSION, data.latestVersion);
        if (prefs.getFloat(MORE_GAMES_FREQ, 0) != data.moreGamesFrequency)
            editor.putFloat(MORE_GAMES_FREQ, data.moreGamesFrequency);
        if (!prefs.getString(FB_URL, "").equals(data.facebookUrl))
            editor.putString(FB_URL, data.facebookUrl);
        if (!prefs.getString(TWITTER_URL, "").equals(data.twitterUrl))
            editor.putString(TWITTER_URL, data.twitterUrl);

        if (!areHintsTouched() && getHintsRemaining() != data.defaultHints)
            editor.putInt(HINTS, data.defaultHints);

        editor.commit();
    }

    public String getLikeUrl() {
        return prefs.getString(FB_URL, null);
    }

    public String getFollowUrl() {
        return prefs.getString(TWITTER_URL, null);
    }

    public boolean isLiked() {
        return getBoolean(FB_LIKED, false, FB_LIKED);
    }

    public boolean isFollowed() {
        return getBoolean(TW_FOLLOWED, false, TW_FOLLOWED);
    }

    public void setLiked() {
        putBoolean(FB_LIKED, true);
    }

    public void setFolowed() {
        putBoolean(TW_FOLLOWED, true);
    }

    public float getMoreGameFreq() {
        return prefs.getFloat(MORE_GAMES_FREQ, 0);
    }


    private String _S(String s) {
        try {
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.encrypt(getKey3(), s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String deS(String s) {
        try {
            return CryptHelperAES.decrypt(getKey3(), s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String _S(String s, String salt) {
        try {
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.encrypt(salt + getKey3(), s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String deS(String s, String salt) {
        try {
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.decrypt(salt + getKey3(), s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getInt(String key, int def, String salt) {
        if (decryptedAll)
            return prefs.getInt(key, def);

        try {
            return Integer.parseInt(deS(prefs.getString(_S(key), null), salt));
        } catch (Exception e) {
            return prefs.getInt(key, def);
        }
    }

    private void putInt(String key, int val) {
        Editor editor = prefs.edit();
        editor.putString(key, Integer.toString(val));
        editor.commit();
    }

    private boolean getBoolean(String key, boolean def, String salt) {
        if (decryptedAll)
            return prefs.getBoolean(key, def);

        try {
            return Boolean.parseBoolean(deS(prefs.getString(_S(key), null), salt));
        } catch (Exception e) {
            return prefs.getBoolean(key, def);
        }
    }

    private void putBoolean(String key, boolean val) {
        Editor editor = prefs.edit();
        editor.putString(key, Boolean.toString(val));
        editor.commit();
    }

    public String getKey1() {
        if (Key1 == null)
            Key1 = context.getResources().getString(R.string.app_name) + LevelPackTable.MAIL;
        Key11 = Key1;
        return Key1;
    }

    public String getKey3() {
        if (Key3 == null)
            Key3 = factory.getDeviceUuid().toString() + LevelPackTable.getHost();
        return Key3;
    }

    public String getKey12() {
        String res = Key11;
        Key11 = Key21;
        return Key21 = res;
    }

    public String getKey2() {
        if (Key2 == null)
            Key2 = LevelPackTable.getHost() + LevelTable.getMail();
        Key21 = Key2;
        return Key2;
    }

    public void setHintChangedListener(HintChangedListener hintChangedListener) {
        this.hintChangedListener = hintChangedListener;
    }

    public interface HintChangedListener {
        public void onHintsChanged(int old, int current);
    }

    void decryptAll() {
        decryptedAll = prefs.getBoolean(DECRYPTED, false);
        if (decryptedAll)
            return;

        Editor editor = prefs.edit();

        if (!getBoolean(INITIIALISED, false, INITIIALISED)){
            //editor.clear().commit();
            return;
        } else {
            editor.putBoolean(INITIIALISED, true);
            editor.remove(_S(INITIIALISED));
        }

        if (areHintsTouched()) {
            editor.putBoolean(HINTS_TOUCHED, true);
            editor.remove(_S(HINTS_TOUCHED));
        }

        editor.putInt(HINTS, getHintsRemaining());
        editor.remove(_S(HINTS));

        if (isGInAppInitDone()) {
            editor.putBoolean(G_IN_APP_INIT_DONE, true);
            editor.remove(_S(G_IN_APP_INIT_DONE));
        }

        LevelPack pack;
        for (int id = 1; id < 12; id++)
            if (isPackUnlocked(id)) {
                pack = LevelPackTable.get(id, context);
                editor.putInt(String.format(LEVEL_UNLOCK, pack.name), getLevelUnlocked(pack));
                editor.remove(_S(String.format(LEVEL_UNLOCK, pack.name)));
            }

        if (isAdFree()){
            editor.putBoolean(ADFREE, true);
            editor.remove(_S(ADFREE));
        }

        editor.putBoolean(MUSIC, getMusic());
        editor.remove(_S(MUSIC));

        editor.putBoolean(SOUND, getSound());
        editor.remove(_S(SOUND));

        if (isLiked()){
            editor.putBoolean(FB_LIKED, true);
            editor.remove(_S(FB_LIKED));
        }

        if (isFollowed()){
            editor.putBoolean(TW_FOLLOWED, true);
            editor.remove(_S(TW_FOLLOWED));
        }

        editor.putBoolean(DECRYPTED, true);
        editor.commit();
    }
}
