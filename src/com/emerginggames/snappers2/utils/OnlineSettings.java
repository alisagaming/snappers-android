package com.emerginggames.snappers2.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.emerginggames.snappers2.Settings;
import org.acra.ErrorReporter;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.emerginggames.snappers2.UserPreferences;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 19.04.12
 * Time: 13:49
 */
public class OnlineSettings implements Runnable {
    static OnlineSettings instance;
    private static final int RETRY_INTERVAL = 5 * 60 * 1000;
    public static final String SETTINGS_URL_GOOGLE = "https://s3.amazonaws.com/emerginggames/bestpuzzlegame-googleplay.json";
    public static final String SETTINGS_URL_AMAZON = "https://s3.amazonaws.com/emerginggames/bestpuzzlegame-amazon.json";

    Context mContext;
    HandlerThread thread;
    Looper looper;
    Handler handler;

    public OnlineSettings(Context mContext) {
        this.mContext = mContext;
        thread = new HandlerThread("worker for OnlineSettings");
        thread.start();
        looper = thread.getLooper();
        handler = new Handler(looper);
    }

    public static void update(Context context){
        if (instance == null)
            instance = new OnlineSettings(context);
        instance.handler.post(instance);
    }

    @Override
    public void run() {
        //String locale = mContext.getResources().getConfiguration().locale.getCountry();
        try{
            SettingsData data = new SettingsData(downloadSettings());
            UserPreferences.getInstance(mContext).saveSettings(data);
            thread.quit();
            instance = null;
            return;
        }catch (JSONException e){
            ErrorReporter.getInstance().handleSilentException(e);
        }
        catch (NullPointerException e){
            ErrorReporter.getInstance().handleSilentException(e);
        } catch (Exception e){}
        handler.postDelayed(this, RETRY_INTERVAL);
    }

    JSONObject downloadSettings() throws IOException, JSONException {
        HttpGet get = new HttpGet(Settings.IS_AMAZON ? SETTINGS_URL_AMAZON : SETTINGS_URL_GOOGLE);
        HttpClient client = new DefaultHttpClient(get.getParams());
        return new JSONObject(client.execute(get, new BasicResponseHandler()));
    }

    public static class SettingsData {
        public boolean tapJoy;
        public boolean inGameAds;
        public int defaultHints;
        public int latestVersion;
        public float moreGamesFrequency;
        public String twitterUrl;
        public String facebookUrl;
        public float interrestialFreq;

        public SettingsData(JSONObject json) throws JSONException {
            if (json.has("latest_version"))
                latestVersion = json.getInt("latest_version");
            if (json.has("in_game_ads"))
                inGameAds = json.getBoolean("in_game_ads");
            if (json.has("hints"))
                defaultHints = json.getInt("hints");
            if (json.has("tapjoy"))
                tapJoy = json.getBoolean("tapjoy");
            if (json.has("more_games_frequency"))
                moreGamesFrequency = (float)json.getDouble("more_games_frequency");
            if (json.has("twitter"))
                twitterUrl = json.getString("twitter");
            if (json.has("facebook"))
                facebookUrl = json.getString("facebook");
            if (json.has("interstitial_frequency"))
                interrestialFreq = (float)json.getDouble("interstitial_frequency");
        }
    }
}