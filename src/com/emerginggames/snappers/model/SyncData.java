package com.emerginggames.snappers.model;

import android.content.Context;
import com.emerginggames.snappers.UserPreferences;
import com.emerginggames.snappers.model.LevelPack;
import org.json.JSONException;
import org.json.JSONObject;
import com.emerginggames.snappers.Settings;
import com.emerginggames.snappers.UserPreferences;
import com.emerginggames.snappers.data.LevelPackTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 12:27
 */
public class SyncData {
    public int hint_count;
    public int xp_count;
    public int xp_level;
    public String gifts;
    public int dollars_spent;
    public String promoCode;
    public Map<String, Integer> levelUnlocks;

    public SyncData() {
    }

    public SyncData(JSONObject data) throws JSONException {
        hint_count = data.optInt("hint_count");
        xp_count = data.optInt("xp_count");
        gifts = data.optString("gifts");
        dollars_spent = data.optInt("dollars_spent");
        promoCode = data.getString("code");

        levelUnlocks = new HashMap<String, Integer>();
        if (!data.isNull("max_unlocked_level_for_pack1"))
            levelUnlocks.put("level-pack-1", data.getInt("max_unlocked_level_for_pack1"));
        if (!data.isNull("max_unlocked_level_for_pack2"))
            levelUnlocks.put("level-pack-2", data.getInt("max_unlocked_level_for_pack2"));
        if (!data.isNull("max_unlocked_level_for_pack3"))
            levelUnlocks.put("level-pack-3", data.getInt("max_unlocked_level_for_pack3"));
        if (!data.isNull("max_unlocked_level_for_pack4"))
            levelUnlocks.put("level-pack-4", data.getInt("max_unlocked_level_for_pack4"));
        if (!data.isNull("max_unlocked_level_for_pack5"))
            levelUnlocks.put("level-pack-5", data.getInt("max_unlocked_level_for_pack5"));
        if (!data.isNull("max_unlocked_level_for_packP1"))
            levelUnlocks.put("premium-level-pack-1", data.getInt("max_unlocked_level_for_packP1"));
    }

    SyncData(Context context){
        UserPreferences prefs = UserPreferences.getInstance(context);
        hint_count = prefs.getHintsRemaining();
        xp_count = prefs.getScore();
        xp_level = Settings.getLevel(xp_count);
        promoCode = prefs.getPromoCode();

        for (LevelPack pack: LevelPackTable.getAll(context)){
            int n = prefs.getLevelUnlocked(pack);
            if (n > 0)
                addLevelUnlock(pack, n);
        }
    }

    public static SyncData load(Context context){
        return new SyncData(context);
    }

    public void save(Context context){
        UserPreferences prefs = UserPreferences.getInstance(context);
        int currentScore = prefs.getScore();
        if (currentScore >= xp_count)
            return;

        prefs.setHints(hint_count);
        prefs.setScore(xp_count);
        if(!promoCode.equals(prefs.getPromoCode()))
            prefs.setPromoCode(promoCode);

        for (Map.Entry<String, Integer> pairs : levelUnlocks.entrySet()) {
            String key = pairs.getKey();
            Integer value = pairs.getValue();

            prefs.unlockLevel(key, value);
        }
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("xp_count", Integer.toString(xp_count));
            obj.put("xp_level", Integer.toString(xp_level));
            obj.put("hint_count", Integer.toString(hint_count));
            obj.put("dollars_spent", Integer.toString(dollars_spent));
            obj.put("code", promoCode);

            for (Map.Entry<String, Integer> pairs : levelUnlocks.entrySet()) {
                String key = pairs.getKey();
                Integer value = pairs.getValue();

                obj.put(getPackJsonId(key), value.toString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public void addLevelUnlock(LevelPack pack, int levels) {
        if (levelUnlocks == null)
            levelUnlocks = new HashMap<String, Integer>();

        levelUnlocks.put(pack.name, levels);
    }

    String getPackJsonId(String name) {
        if (name.equals("level-pack-1"))
            return "max_unlocked_level_for_pack1";
        else if (name.equals("level-pack-2"))
            return "max_unlocked_level_for_pack2";
        else if (name.equals("level-pack-3"))
            return "max_unlocked_level_for_pack3";
        else if (name.equals("level-pack-4"))
            return "max_unlocked_level_for_pack4";
        else if (name.equals("level-pack-5"))
            return "max_unlocked_level_for_pack5";
        else if (name.equals("premium-level-pack-1"))
            return "max_unlocked_level_for_packP1";

        return "";
    }
}
