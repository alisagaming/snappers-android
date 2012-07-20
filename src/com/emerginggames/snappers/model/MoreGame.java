package com.emerginggames.snappers.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 20.07.12
 * Time: 1:25
 * To change this template use File | Settings | File Templates.
 */
public class MoreGame {
    public String id;
    public String name;
    public String description;
    public String url;
    public String icon;

    public MoreGame(JSONObject json) {
        try {
            id = json.getString("id");
            name = json.getString("name");
            url = json.getString("url");
            icon = json.getString("icon");


            if (json.has("description"))
                description = json.getString("description");
        } catch (JSONException e) {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MoreGame){
            MoreGame other = (MoreGame)o;

            return id.equals(other.id) && name.equals(other.name) && url.equals(other.url) && icon.equals(other.icon) &&
                    ((description == null && other.description == null) || (description != null && description.equals(other.description)));
        }

        return false;
    }
}
