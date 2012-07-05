package com.emerginggames.snappers2;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.emerginggames.snappers2.data.FriendTable;
import com.emerginggames.snappers2.model.FbUserInfo;
import com.emerginggames.snappers2.model.SyncData;
import com.emerginggames.snappers2.utils.UserPreferencesBase;
import org.acra.ACRA;
import com.emerginggames.snappers2.data.LevelPackTable;
import com.emerginggames.snappers2.model.Level;
import com.emerginggames.snappers2.model.LevelPack;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 17:01
 */
public class UserPreferences extends UserPreferencesBase {

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
    private static final String LAST_USED_DAILY_BONUS = "LAST_USED_DAILY_BONUS";
    private static final String USER_LIKED = "LIKED";
    private static final String USER_RATED = "RATED";
    private static final String LAST_RATE_RECOMMENDED = "LAST_RATE_RECOMMENDED";
    private static final String LAST_LIKE_OR_RATE_RECOMMENDED = "LAST_LIKE_OR_RATE_RECOMMENDED";
    private static final String PROMO_CODE = "PROMO_CODE";
    private static final String FACEBOOK_NAME = "fbName";
    private static final String FACEBOOK_EXPIRES = "access_expires";
    private static final String FACEBOOK_TOKEN = "access_token";
    private static final String FB_UID = "fb_uid_";
    private static final String HAD_FB_SYNC = "had fb sync";
    private static final String SHARE_TO_FB = "shareToFb";


    private static final int INITIAL_HINTS = Settings.DEBUG ? 10 : 2;
    private static UserPreferences instance;
    private static final Object editorLock = new Object();


    HintChangedListener hintChangedListener;
    long fbUID;


    public static UserPreferences getInstance(Context context) {
        if (instance == null)
            return instance = new UserPreferences(context);
        return instance;
    }

    public UserPreferences(Context context) {
        super(context);
        initialise();
        fbUID = prefs.getLong(FB_UID, 0);
    }

    public static void setContext(Context context) {
        if (instance == null)
            instance = new UserPreferences(context);
        else if (context != null)
            instance.context = context;
    }

    public void setLastUsedDailyBonus(long time) {
        putLong(LAST_USED_DAILY_BONUS, time);
    }

    public long getLastUsedDailyBonus() {
        return getLong(LAST_USED_DAILY_BONUS, 0);
    }

    public void setTapjoyEnabled(boolean enabled) {
        putBoolean(TAPJOY_ENABLED, enabled);
    }

    public boolean isTapjoyEnabled() {
        return getBoolean(TAPJOY_ENABLED, false);
    }

    public void setIngameAds(boolean enabled) {
        putBoolean(INGAMEADS, enabled);
    }

    public boolean getIngameAds() {
        return getBoolean(INGAMEADS, false);
    }

    public void touchHints() {
        putBoolean(HINTS_TOUCHED, true);
    }

    public boolean areHintsTouched() {
        return getBoolean(HINTS_TOUCHED, false);
    }

    public int getHintsRemaining() {
        return getInt(fbUID == 0 ? HINTS : "!" + HINTS, 0, HINTS);
    }

    public void setGInAppInitDone(boolean done) {
        putBoolean(G_IN_APP_INIT_DONE, done);
    }

    public boolean isGInAppInitDone() {
        return getBoolean(G_IN_APP_INIT_DONE, false);
    }

    public void useHint() {
        addHints(-1);
    }

    public void addHints(int amount) {
        int hintsRemaining = getHintsRemaining();
        setHints(amount + hintsRemaining);
        if (hintChangedListener != null)
            hintChangedListener.onHintsChanged(hintsRemaining, hintsRemaining + amount);

    }

    public void setHints(int amount) {
        putInt(fbUID == 0 ? HINTS : "!" + HINTS, amount, HINTS);
        if (!areHintsTouched())
            touchHints();
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
        if (pack.id == 1 || (Settings.IS_PREMIUM && pack.title.equals("Extra")))
            return Math.max(1, getInt(getPackKey(pack.name), 0, pack.name));

        return getInt(getPackKey(pack.name), 0, pack.name);
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
        putInt(getPackKey(pack.name), 1, pack.name);
    }

    public void lockLevelPack(LevelPack pack) {
        remove(getPackKey(pack.name));
    }

    public int getScore() {
        return prefs.getInt(fbUID == 0 ? SCORE : "!" + SCORE, 0);
    }

    public void setScore(int addScore) {
        Editor editor = prefs.edit();
        editor.putInt(fbUID == 0 ? SCORE : "!" + SCORE, addScore);
        editor.commit();
    }

    public void addScore(int addScore) {
        addScore += getScore();
        Editor editor = prefs.edit();
        editor.putInt(fbUID == 0 ? SCORE : "!" + SCORE, addScore);
        editor.commit();
    }

    public void unlockNextLevel(Level currentLevel) {
        if (currentLevel.pack == null)
            currentLevel.pack = LevelPackTable.get(currentLevel.packNumber, context);
        int unlocked = getLevelUnlocked(currentLevel.pack);
        if (unlocked > currentLevel.number)
            return;

        unlockLevel(currentLevel.pack.name, currentLevel.number + 1);
    }

    public void unlockLevel(String name, int level) {
        putInt(getPackKey(name), level, name);
    }

    public boolean isLevelSolved(Level level) {
        return level != null && level.number < getLevelUnlocked(level.packNumber);
    }

    public boolean isAdFree() {
        return getBoolean(ADFREE, false);
    }

    public void setAdFree(boolean isAdFree) {
        putBoolean(ADFREE, isAdFree);
    }

    private void initialise() {
        if (getBoolean(INITIIALISED, false))
            return;

        putInt(fbUID == 0 ? HINTS : "!" + HINTS, INITIAL_HINTS, HINTS);
        putBoolean(INITIIALISED, true);

        ACRA.getACRASharedPreferences().edit().putBoolean(ACRA.PREF_ENABLE_DEVICE_ID, false).commit();
    }

    public void setMusic(boolean enabled) {
        putBoolean(MUSIC, enabled);
    }

    public boolean getMusic() {
        return getBoolean(MUSIC, true);
    }

    public void setSound(boolean enabled) {
        putBoolean(SOUND, enabled);
    }

    public boolean getSound() {
        return getBoolean(SOUND, true);
    }

    public boolean getShareToFb() {
        return getBoolean(SHARE_TO_FB, true);
    }

    public void setShareToFb(boolean enabled) {
        putBoolean(SHARE_TO_FB, enabled);
    }



    public boolean isLiked() {
        return getBoolean(USER_LIKED, false);
    }

    public void setLiked(boolean liked) {
        putBoolean(USER_LIKED, liked);
    }

    public boolean isRated() {
        return getBoolean(USER_RATED, false);
    }

    public void setRated(boolean rated) {
        putBoolean(USER_RATED, rated);
    }

    public long getLastLikeOrRateRecommeded() {
        return getLong(LAST_LIKE_OR_RATE_RECOMMENDED, 0);
    }

    public void setLastLikeOrRecommended(long time) {
        putLong(LAST_LIKE_OR_RATE_RECOMMENDED, time);
    }

    public void setPromoCode(String code) {
        putString(PROMO_CODE, code);
    }

    public String getPromoCode() {
        return getString(PROMO_CODE);
    }

    public String getFbToken() {
        return prefs.getString(FACEBOOK_TOKEN, null);
    }

    public void setFbToken(String token) {
        prefs.edit().putString("access_token", token).commit();
    }

    public void setFacebookUserName(String name) {
        prefs.edit().putString(FACEBOOK_NAME, name).commit();
    }

    public String getFacebookUserName() {
        return prefs.getString(FACEBOOK_NAME, null);
    }

    public long getFbExpires() {
        return prefs.getLong(FACEBOOK_EXPIRES, 0);
    }

    public void setFbExpires(long expires) {
        prefs.edit().putLong(FACEBOOK_EXPIRES, expires);
    }

    public void setFb(String token, long expires) {
        prefs.edit().putString(FACEBOOK_TOKEN, token).putLong(FACEBOOK_EXPIRES, expires).commit();
    }

    public void logoffFb() {
        prefs.edit().remove(FACEBOOK_EXPIRES).remove(FACEBOOK_NAME).remove(FACEBOOK_TOKEN).remove(FB_UID).commit();
        fbUID = 0;
        clearFbUidInfo();
    }

    public void setCurrentUser(FbUserInfo user) {
        if (prefs.getLong(FB_UID, 0) != user.fbUID) {
            clearFbUidInfo();
            prefs.edit().putLong(FB_UID, user.fbUID).putString(FACEBOOK_NAME, user.name).commit();
            fbUID = user.fbUID;
        } else if (!user.name.equalsIgnoreCase(getFacebookUserName()))
            setFacebookUserName(user.name);
    }

    public void loggedIn(String token, long expires) {
        clearFbUidInfo();
        setFb(token, expires);
    }

    public void clearFbUidInfo() {
        FriendTable.clear(context);
        synchronized (editorLock) {
            editor = prefs.edit();
            editor
                    .remove(HAD_FB_SYNC);

            remove("!" + SCORE);
            remove("!" + HINTS);

            LevelPack[] packs = LevelPackTable.getAll(context);

            for (LevelPack pack : packs)
                remove("!" + getPackKey(pack.name));

            editor.commit();
            editor = null;
        }

        fbUID = 0;
    }

    String getPackKey(String name) {
        String key = String.format(LEVEL_UNLOCK, name);
        return fbUID == 0 ? key : "!" + key;
    }

    boolean hadFbSync() {
        return prefs.getBoolean(HAD_FB_SYNC, false);
    }

    void setFbSync() {
        if (editor == null){
            Editor edt = prefs.edit();
            edt.putBoolean(HAD_FB_SYNC, true);
            edt.commit();
        }
        else
            editor.putBoolean(HAD_FB_SYNC, true);
    }

    public SyncData getSyncData() {
        long tmpUid = 0;
        if (!hadFbSync()) {
            tmpUid = fbUID;
            fbUID = 0;
        }

        SyncData syncData = new SyncData();
        syncData.hint_count = getHintsRemaining();
        syncData.xp_count = getScore();
        syncData.xp_level = Settings.getLevel(syncData.xp_count);
        syncData.promoCode = getPromoCode();

        for (LevelPack pack : LevelPackTable.getAll(context)) {
            int n = getLevelUnlocked(pack);
            if (n > 0)
                syncData.addLevelUnlock(pack, n);
        }

        if (tmpUid != 0)
            fbUID = tmpUid;


        return syncData;
    }

    public void saveSyncData(SyncData data) {
        int currentScore = getScore();
        synchronized (editorLock) {
            editor = prefs.edit();
            if (currentScore < data.xp_count) {
                setHints(data.hint_count);
                setScore(data.xp_count);
                if (!data.promoCode.equals(getPromoCode()))
                    setPromoCode(data.promoCode);

                for (Map.Entry<String, Integer> pairs : data.levelUnlocks.entrySet()) {
                    String key = pairs.getKey();
                    Integer value = pairs.getValue();

                    unlockLevel(key, value);
                }
            }

            if (data.gifts != null && data.gifts.length != 0) {
                addHints(data.gifts.length);
            }

            setFbSync();

            editor.commit();
            editor = null;
        }
    }

    public boolean hasUID() {
        return fbUID != 0;
    }

    public void setHintChangedListener(HintChangedListener hintChangedListener) {
        this.hintChangedListener = hintChangedListener;
    }

    public interface HintChangedListener {
        public void onHintsChanged(int old, int current);
    }

}
