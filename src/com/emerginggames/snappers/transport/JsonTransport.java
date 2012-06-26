package com.emerginggames.snappers.transport;

import com.emerginggames.snappers.Settings;
import com.emerginggames.snappers.model.SyncData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.emerginggames.snappers.model.FacebookFriend;
import com.emerginggames.snappers.utils.WorkerThreads;

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
    static final String METHOD_INVITE = "invite";
    static final String METHOD_SHARE = "share";
    static final String METHOD_GIFT = "gift";
    static final String METHOD_PROMO = "promo";

    static final String KEY_ACCESS_TOKEN = "access_token";
    static final String KEY_INVITE_TO = "invite_to";
    static final String KEY_GIFT_TO = "gift_to";
    static final String KEY_MESSAGE = "message";
    static final String KEY_CODE = "code";

    public static void sync(String token, SyncData data, JsonResponseHandler handler) {
        JSONObject obj = data.toJson();
        try {
            obj.put(KEY_ACCESS_TOKEN, token);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        SyncRequest syncRequest = new SyncRequest(obj, handler);
        syncRequest.setDebug(Settings.DEBUG);
        WorkerThreads.run(syncRequest);
    }

    public static void getFriends(String token, JsonResponseHandler handler){
        Map<String, String> data = new HashMap<String, String>();
        data.put(KEY_ACCESS_TOKEN, token);

        FriendsRequest request = new FriendsRequest(data, handler);
        request.setDebug(Settings.DEBUG);
        WorkerThreads.run(request);
    }

    public static void invite(String token, long user, String message, JsonResponseHandler handler){
        JSONObject data = new JSONObject();
        try{
            data.put(KEY_ACCESS_TOKEN, token);
            data.put(KEY_INVITE_TO, user);
            data.put(KEY_MESSAGE, message);
        }catch (Exception e){ handler.onError(e);}

        OkRequest request = new OkRequest(METHOD_INVITE, "InviteOkMessage", data, handler);
        request.setDebug(Settings.DEBUG);
        WorkerThreads.run(request);
    }

    public static void share(String token, String message, JsonResponseHandler handler){
        JSONObject data = new JSONObject();
        try{
            data.put(KEY_ACCESS_TOKEN, token);
            data.put(KEY_MESSAGE, message);
        }catch (Exception e){ handler.onError(e);}

        OkRequest request = new OkRequest(METHOD_SHARE, "ShareOkMessage", data, handler);
        request.setDebug(Settings.DEBUG);
        WorkerThreads.run(request);
    }

    public static void gift(String token, long user, JsonResponseHandler handler){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(KEY_ACCESS_TOKEN, token);
        data.put(KEY_GIFT_TO, user);

        OkRequest request = new OkRequest(METHOD_GIFT, "GiftOkMessage", data, handler);
        request.setDebug(Settings.DEBUG);
        WorkerThreads.run(request);
    }

    public static void promo(String token, String code, JsonResponseHandler handler){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(KEY_ACCESS_TOKEN, token);
        data.put(KEY_CODE, code);

        PromoRequest request = new PromoRequest(data, handler);
        request.setDebug(Settings.DEBUG);
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

    static class OkRequest extends MyJsonRequest{
        String okType;
        OkRequest(String method, String okType, JSONObject data, JsonResponseHandler handler) {
            super(method, true);
            this.okType = okType;
            setParams(data);
            setHandler(handler);
        }

        OkRequest(String method, String okType, Map data, JsonResponseHandler handler) {
            super(method, false);
            this.okType = okType;
            setParams(data);
            setHandler(handler);
        }

        @Override
        boolean isResponceOk(JSONObject object) throws JSONException {
            return object.getString("type").equalsIgnoreCase(okType);
        }

        @Override
        void onSuccess(Object object) {
            handler.onOk(null);
        }
    }

    static class PromoRequest extends MyJsonRequest {
        PromoRequest(Map params, JsonResponseHandler handler) {
            super(METHOD_PROMO, false);
            setParams(params);
            setHandler(handler);
        }

        @Override
        void onSuccess(Object object) {
            try {
                int hints = ((JSONObject)object).getInt("promo_hints");
                handler.onOk(hints);
            } catch (JSONException e) {
                handler.onError(e);
            }
        }

        @Override
        boolean isResponceOk(JSONObject object) throws JSONException {
            return object.getString("type").equalsIgnoreCase("PromoOkMessage");
        }
    }




}
