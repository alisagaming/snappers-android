package com.emerginggames.snappers.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import org.acra.ErrorReporter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.emerginggames.snappers.UserPreferences;

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
    public static final String SETTINGS_URL = "http://s3.amazonaws.com/snappersandroid/default.xml";
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
        String locale = mContext.getResources().getConfiguration().locale.getCountry();
        try{
            getSettingsFromStream(downloadSettings(), locale).save(UserPreferences.getInstance(mContext));
            thread.quit();
            instance = null;
            return;
        }catch (XmlPullParserException e){
            ErrorReporter.getInstance().handleSilentException(e);
        }
        catch (NullPointerException e){
            ErrorReporter.getInstance().handleSilentException(e);
        } catch (Exception e){}
        handler.postDelayed(this, RETRY_INTERVAL);
    }

    InputStream downloadSettings() throws IOException {
        URL url = new URL(SETTINGS_URL);
        URLConnection connection = url.openConnection();
        connection.connect();
        return new BufferedInputStream(url.openStream());
    }

    CountrySettings getSettingsFromStream(InputStream stream, String locale) throws XmlPullParserException, IOException {
        CountrySettings defaultSettings = null, settings = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(stream, "UTF-8");

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equalsIgnoreCase("setting")){
                        settings = CountrySettings.parse(xpp);
                        if (settings.locale.equalsIgnoreCase(locale))
                            return settings;
                        if (settings.locale.equalsIgnoreCase("default"))
                            defaultSettings = settings;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.TEXT:
                    break;
            }
            eventType = xpp.next();
        }
        return defaultSettings;
    }

    private static class CountrySettings{
        String locale;
        boolean tapJoy;
        boolean inGameAds;
        int defaultHints;

        public static CountrySettings parse(XmlPullParser xpp){
            CountrySettings s = new CountrySettings();

            for (int i=0; i< xpp.getAttributeCount(); i++){
                if (xpp.getAttributeName(i).equalsIgnoreCase("name"))
                    s.locale = xpp.getAttributeValue(i);
                else if (xpp.getAttributeName(i).equalsIgnoreCase("tapjoy"))
                    s.tapJoy = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
                else if (xpp.getAttributeName(i).equalsIgnoreCase("ingameads"))
                    s.inGameAds = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
                else if (xpp.getAttributeName(i).equalsIgnoreCase("hints"))
                    s.defaultHints = Integer.parseInt(xpp.getAttributeValue(i));
            }

            return s;
        }

        public void save(UserPreferences prefs){
            prefs.setTapjoyEnabled(tapJoy);
            prefs.setIngameAds(inGameAds);
            if (!prefs.areHintsTouched())
                prefs.setHints(defaultHints);
        }
    }
}
