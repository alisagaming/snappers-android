package ru.emerginggames.snappers.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 19.06.12
 * Time: 12:18
 */
public class FacebookFriend {
    //“facebook_id”: long, “first_name”: string, “installed”: boolean, “xp_count”: int
    public long facebook_id;
    public String first_name;
    public boolean installed;
    public int xp_count;

    public FacebookFriend(JSONObject data) throws JSONException {
        facebook_id = data.getLong("facebook_id");
        first_name =data.optString("first_name");
        installed = data.optBoolean("installed");
        xp_count = data.optInt("xp_count");
    }
}
