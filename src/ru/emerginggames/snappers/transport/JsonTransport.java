package ru.emerginggames.snappers.transport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.emerginggames.snappers.model.FacebookFriend;
import ru.emerginggames.snappers.model.SyncData;
import ru.emerginggames.snappers.utils.WorkerThreads;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.06.12
 * Time: 19:26
 */
public class JsonTransport {
    public static final String SERVER = "https://snappersbackend.emerginggames.com:8080/";
    static final String METHOD_SYNC = "sync";
    static final String METHOD_FRIENDS = "friends";
    static final String KEY_ACCESS_TOKEN = "access_token";

    public static void sync(String token, SyncData data, JsonResponseHandler handler) {
        JSONObject obj = data.toJson();
        try {
            obj.put(KEY_ACCESS_TOKEN, token);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SyncRequest syncRequest = new SyncRequest(obj, handler);
        WorkerThreads.run(syncRequest);
    }

    public static void getFriends(String token, JsonResponseHandler handler){
        Map<String, String> data = new HashMap<String, String>();
        data.put(KEY_ACCESS_TOKEN, token);

        FriendsRequest request = new FriendsRequest(data, handler);
        WorkerThreads.run(request);
    }

    static class SyncRequest extends MyJsonRequest {
        SyncRequest(JSONObject params, JsonResponseHandler handler) {
            super(METHOD_SYNC, true);
            setParams(params);
            setHandler(handler);
        }

        @Override
        void onSuccess(Object object) {
            try {
                handler.onOk( new SyncData((JSONObject)object));
            } catch (JSONException e) {
                handler.onError(e);
            }
        }

        @Override
        boolean isResponceOk(JSONObject object) throws JSONException {
            return object.getString("type").equalsIgnoreCase("SyncOkMessage");
        }
    }

    static class FriendsRequest extends MyJsonRequest {
        FriendsRequest(Map params, JsonResponseHandler handler) {
            super(METHOD_FRIENDS, false);
            setParams(params);
            setHandler(handler);
        }

        @Override
        void onSuccess(Object object) {
            try {
                JSONArray data = (JSONArray)object;
                FacebookFriend[] friends = new FacebookFriend[data.length()];
                for (int i=0; i< data.length(); i++)
                    friends[i] = new FacebookFriend(data.getJSONObject(i));

                handler.onOk( friends);
            } catch (JSONException e) {
                handler.onError(e);
            }
        }

        @Override
        boolean isResponceOk(JSONObject object) throws JSONException {
            return object.getString("type").equalsIgnoreCase("FriendsOkMessage");
        }
    }


}
