package com.emerginggames.snappers2.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.06.12
 * Time: 4:17
 */
public class FacebookFriend extends DbItem {
    //“facebook_id”: long, “first_name”: string, “installed”: boolean, “xp_count”: int
    //public long id;
    public long facebook_id;
    public String first_name;
    public boolean installed;
    public int xp_count;
    public long lastSendGift;

    public FacebookFriend() {
    }

    public FacebookFriend(JSONObject data) throws JSONException {
        facebook_id = data.getLong("facebook_id");
        first_name =data.optString("first_name");
        installed = data.optBoolean("installed");
        xp_count = data.optInt("xp_count");
    }
}
