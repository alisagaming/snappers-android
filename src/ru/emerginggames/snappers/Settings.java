package ru.emerginggames.snappers;

import android.content.Context;
import android.util.Log;
import ru.emerginggames.snappers.data.CryptHelperDES;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 01.04.12
 * Time: 6:01
 */
public class Settings {
    public static enum CrashReporter {HockeyApp, ACRA, ACRA_BUGSENSE}
    public static final boolean ENABLE_ALL_LEVELS = false;
    public static final float REPEAT_MULT = 0.1f;
    public static final float HINTED_MULT = 0.5f;
    public static boolean DEBUG = true;
    public static final String APP_ID = "0e03399851c2ed799503a9019c9630fd";
    public static final String BUGSENSE_API_KEY = "8a555912";
    public static final CrashReporter CRASH_REPORTER = CrashReporter.ACRA;
    public static final boolean SEND_EXTENDED_AD_INFO = false;
    //1543619c1de44e80af13f861b204b778
    private static String adwhirlKey = "5D43298AD9AB6642HED6DFBDA4F442B33HAAD7E50F0B01C4B6H285690406470CD02H16B7733B6AFA5528HE7AA041885AA117AHAD9A87096A80397FHE9C3FAAC58D1D615";
    //App ID ? 6ac99625-6d02-4326-becd-213a233c511a
    private static String TJKey = "A69127567A50A671572E74997F8580A6JC78105DD8B3BFAE7J1BBB20C96D5B687BJ24B66487EDBAF2C1JAC0A19640D6AAD514B8E1BBE3F8248E2";
    //GvTn bQgQ 1P86 bB5X gr4L
    private static String TJSecretKey = "E5FDD91C61783906K19727591AFFB805AKE887BEF3AE633FABK5153EAA399A85809K4AD4B5F812A26DCD";




    public static String getAdwhirlKey(Context context) {
        UserPreferences prefs = UserPreferences.getInstance(context);

/*        try{
            prefs.getKey1();
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "1543"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "619c"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "1de4"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "4e80"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "af13"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "f861"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "b204"));
            Log.e("ENCODED - adwirl!!!", CryptHelperDES.encrypt(prefs.getKey12(), "b778"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        String key = prefs.getKey1();
        try{
            String[] chunks = adwhirlKey.split("H");
            StringBuilder b = new StringBuilder();
            for (String c: chunks)
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), c));

            //Log.e("DECODED - adwirl!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getGitCommitString(Context context){
        DataInputStream stream = new DataInputStream(context.getResources().openRawResource(R.raw.commit));
        try{
            return stream.readLine();
        } catch (IOException e){
            return "";
        }
    }
    //App ID ? 6ac99625-6d02-4326-becd-213a233c511a
    //App Secret Key ? GvTnbQgQ1P86bB5Xgr4L

    public static String getTapJoyAppId(Context context){
        UserPreferences prefs = UserPreferences.getInstance(context);

        /*try{
            prefs.getKey1();
            Log.e("ENCODED - TjAppKey!!!", CryptHelperDES.encrypt(prefs.getKey12(), "6ac99625"));
            Log.e("ENCODED - TjAppKey!!!", CryptHelperDES.encrypt(prefs.getKey12(), "6d02"));
            Log.e("ENCODED - TjAppKey!!!", CryptHelperDES.encrypt(prefs.getKey12(), "4326"));
            Log.e("ENCODED - TjAppKey!!!", CryptHelperDES.encrypt(prefs.getKey12(), "becd"));
            Log.e("ENCODED - TjAppKey!!!", CryptHelperDES.encrypt(prefs.getKey12(), "213a233c511a"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        String key = prefs.getKey1();
        try{
            String[] chunks = TJKey.split("J");
            StringBuilder b = new StringBuilder();

            for (int i=0; i< chunks.length; i++){
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), chunks[i]));
                if (i != chunks.length-1)
                    b.append('-');
            }

            //Log.e("DECODED - TJ-key!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        //return "437aa914-31c2-4eb2-a061-9badc6d52fb8";
    }

    public static String getTapJoySecretKey(Context context){
        UserPreferences prefs = UserPreferences.getInstance(context);


/*        //GvTn bQgQ 1P86 bB5X gr4L
        try{
            prefs.getKey1();
            Log.e("ENCODED - Tj-secret!!!", CryptHelperDES.encrypt(prefs.getKey12(), "GvTn"));
            Log.e("ENCODED - Tj-secret!!!", CryptHelperDES.encrypt(prefs.getKey12(), "bQgQ"));
            Log.e("ENCODED - Tj-secret!!!", CryptHelperDES.encrypt(prefs.getKey12(), "1P86"));
            Log.e("ENCODED - Tj-secret!!!", CryptHelperDES.encrypt(prefs.getKey12(), "bB5X"));
            Log.e("ENCODED - Tj-secret!!!", CryptHelperDES.encrypt(prefs.getKey12(), "gr4L"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        String key = prefs.getKey1();
        try{
            String[] chunks = TJSecretKey.split("K");
            StringBuilder b = new StringBuilder();
            for (String c: chunks)
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), c));

            //Log.e("DECODED - TJ-secret!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //TODO: encrypt
        //return "PfVKO1NNtOicS512X2jA";
    }




}
