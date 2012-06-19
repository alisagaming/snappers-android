package ru.emerginggames.snappers.transport;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
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

    public static void sync(String token, String code, SyncData data, JsonResponseHandler handler) {
        JSONObject obj = data.toJson();
        try {
            obj.put(KEY_ACCESS_TOKEN, token);
            obj.put("code", code);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setParams(obj);
        syncRequest.setHandler(handler);
        WorkerThreads.run(syncRequest);

    }

    static class SyncRequest extends MyJsonRequest {
        SyncRequest() {
            super(METHOD_SYNC, true);
        }

        @Override
        void onSuccess(JSONObject object) {
            handler.onOk(SyncData.fromJson(object));
        }
    }

    static class FriendsRequest extends MyJsonRequest {
        FriendsRequest() {
            super(METHOD_FRIENDS, false);
        }

        @Override
        void onSuccess(JSONObject object) {
            handler.onOk(SyncData.fromJson(object));
        }
    }


}
