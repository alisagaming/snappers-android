package com.emerginggames.bestpuzzlegame.model;

import org.json.JSONException;
import org.json.JSONObject;

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
    //public String gifts;
    public long[] gifts;
    public int dollars_spent;
    public String promoCode;
    public Map<String, Integer> levelUnlocks;


    public SyncData() {
    }

    public SyncData(JSONObject data) throws JSONException {
        hint_count = data.optInt("hint_count");
        xp_count = data.optInt("xp_count");
        dollars_spent = data.optInt("dollars_spent");
        promoCode = data.getString("code");
        setGifts(data.optString("gifts"));

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

    void setGifts(String giftsStr) {
        if (giftsStr == null || giftsStr.length() == 0)
            return;
        String[] giftsArr = giftsStr.split(",");
        gifts = new long[giftsArr.length];
        for (int i = 0; i < giftsArr.length; i++)
            gifts[i] = Long.parseLong(giftsArr[i]);
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("xp_count", Integer.toString(xp_count));
            obj.put("xp_level", Integer.toString(xp_level));
            obj.put("hint_count", Integer.toString(hint_count));
            obj.put("dollars_spent", Integer.toString(dollars_spent));
            obj.put("code", promoCode);

            if (levelUnlocks != null)
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
