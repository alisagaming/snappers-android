package ru.emerginggames.snappers.model;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import ru.emerginggames.snappers.UserPreferences;
import ru.emerginggames.snappers.data.LevelPackTable;

import java.util.HashMap;
import java.util.Iterator;
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
    public Map<String, Integer> levelUnlocks;

    public static SyncData fromJson(JSONObject object) {
        SyncData result = new SyncData();
        try {
            result.hint_count = object.getInt("hint_count");
            result.xp_count = object.getInt("xp_count");
            result.gifts = object.getString("gifts");
            result.dollars_spent = object.getInt("dollars_spent");

            result.levelUnlocks = new HashMap<String, Integer>();
            if (!object.isNull("max_unlocked_level_for_pack1"))
                result.levelUnlocks.put("level-pack-1", object.getInt("max_unlocked_level_for_pack1"));
            if (!object.isNull("max_unlocked_level_for_pack2"))
                result.levelUnlocks.put("level-pack-2", object.getInt("max_unlocked_level_for_pack2"));
            if (!object.isNull("max_unlocked_level_for_pack3"))
                result.levelUnlocks.put("level-pack-3", object.getInt("max_unlocked_level_for_pack3"));
            if (!object.isNull("max_unlocked_level_for_pack4"))
                result.levelUnlocks.put("level-pack-4", object.getInt("max_unlocked_level_for_pack4"));
            if (!object.isNull("max_unlocked_level_for_pack5"))
                result.levelUnlocks.put("level-pack-5", object.getInt("max_unlocked_level_for_pack5"));
            if (!object.isNull("max_unlocked_level_for_packP1"))
                result.levelUnlocks.put("premium-level-pack-1", object.getInt("max_unlocked_level_for_packP1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("xp_count", Integer.toString(xp_count));
            obj.put("xp_level", Integer.toString(xp_level));
            obj.put("hint_count", Integer.toString(hint_count));
            obj.put("dollars_spent", Integer.toString(dollars_spent));

            for (Map.Entry<String, Integer> pairs : levelUnlocks.entrySet()) {
                String key = pairs.getKey();
                Integer value = pairs.getValue();

                obj.put(key, value.toString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public void addLevelUnlock(LevelPack pack, int levels) {
        if (levelUnlocks == null)
            levelUnlocks = new HashMap<String, Integer>();

        levelUnlocks.put(getPackShortId(pack), levels);
    }

    String getPackShortId(LevelPack pack) {
        if (pack.name.equals("level-pack-1"))
            return "max_unlocked_level_for_pack1";
        else if (pack.name.equals("level-pack-2"))
            return "max_unlocked_level_for_pack2";
        else if (pack.name.equals("level-pack-3"))
            return "max_unlocked_level_for_pack3";
        else if (pack.name.equals("level-pack-4"))
            return "max_unlocked_level_for_pack4";
        else if (pack.name.equals("level-pack-5"))
            return "max_unlocked_level_for_pack5";
        else if (pack.name.equals("premium-level-pack-1"))
            return "max_unlocked_level_for_packP1";

        return "";
    }

    public LevelPack getLevelPack(String jsonId, Context context) {
        if (jsonId.equals("max_unlocked_level_for_pack1"))
            return LevelPackTable.get("level-pack-1", context);
        else if (jsonId.equals("max_unlocked_level_for_pack2"))
            return LevelPackTable.get("level-pack-2", context);
        else if (jsonId.equals("max_unlocked_level_for_pack3"))
            return LevelPackTable.get("level-pack-3", context);
        else if (jsonId.equals("max_unlocked_level_for_pack4"))
            return LevelPackTable.get("level-pack-4", context);
        else if (jsonId.equals("max_unlocked_level_for_pack5"))
            return LevelPackTable.get("level-pack-5", context);
        else if (jsonId.equals("max_unlocked_level_for_packP1"))
            return LevelPackTable.get("premium-level-pack-1", context);

        return null;

    }
}
