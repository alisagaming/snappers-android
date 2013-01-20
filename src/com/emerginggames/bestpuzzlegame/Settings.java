package com.emerginggames.bestpuzzlegame;

import android.content.Context;


import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 01.04.12
 * Time: 6:01
 */
public class Settings {
    public static final String TAG = "Snappers2";

    public static final boolean IS_AMAZON = false;

    public static enum CrashReporter {HockeyApp, ACRA, ACRA_BUGSENSE}
    public static final String FONT = "Berlin Sans FB.ttf";

    public static final int BONUS_FOR_RATE = 1;
    public static final int BONUS_FOR_LIKE = 1;
    public static final int BONUS_FOR_FOLLOW = 1;
    public static final int BONUS_FOR_SHARE = 1;
    public static final int BONUS_FOR_INVITE = 1;

    public static final float REPEAT_MULT = 0.1f;
    public static final float HINTED_MULT = 0.5f;

    public static boolean DEBUG = false;
    public static final boolean DEBUG_BUY = true & DEBUG;
    public static final boolean ENABLE_ALL_LEVELS = false & DEBUG;
    public static final boolean NO_PREF_ENCRYPT = true & DEBUG;
    public static boolean GoogleInAppEnabled = true;

    public static final long GIFT_INTERVAL = 24*60*60*1000;//1 day
    public static final long LIKE_INTERVAL = 24*60*60*1000;//3 day
    public static final long RATE_INTERVAL = 24*60*60*1000;//1 day
    public static final int LEVEL_TO_RECOMMEND = 10;//30;


    public static final String APP_ID = "0e03399851c2ed799503a9019c9630fd";
    public static final String BUGSENSE_API_KEY = "8a555912";
    public static final CrashReporter CRASH_REPORTER = CrashReporter.ACRA;
    public static final boolean SEND_EXTENDED_AD_INFO = false;
    public static final boolean IS_PREMIUM = false;

    public static final String CB_APP_ID = "50fae39d17ba470459000020";
    public static final String CB_SIGNATURE = "d73c906d55ce8fda76851797174b9eb230568d14";
    //1543619c1de44e80af13f861b204b778
    private static String adwhirlKey = "5D43298AD9AB6642H81E3D3F20BA02E8FHAAD7E50F0B01C4B6H13863AC093001CC1H16B7733B6AFA5528H39DA19D96F7DF7F3HAD9A87096A80397FHDF9A4393DB6D4F33";
    //App ID ? 6ac99625-6d02-4326-becd-213a233c511a
    private static String TJKey = "AEA6EAFD7FA9BC26565E0C589BC0238AJC78105DD8B3BFAE7J63943BBFE8BB9317J24B66487EDBAF2C1J7C340D30CF49B18BB5F3AC4B52D0289D";
    //GvTn bQgQ 1P86 bB5X gr4L

                                       //E5FDD91C61783906K0357C137318677C4KE887BEF3AE633FABK5DD66C05D4227322K4AD4B5F812A26DCD
    private static String TJSecretKey = "2D6CA34B4974E7B9K0FFB56E29EA65C62K58DE70E9EAF4D73DK77181EC5A393B1E3K9927B62D954DAE0F";

    private static final String gInAPPKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWK5eC6WW8fiRuvVfxaSaqUyMnpthTtV8abNUYLbS4pRcjbolfQJTFqZg+rzIADeBV1VZ0Iw/ZHO6i7n3iqFSDqKdBHwkteoEgynBJy+9THP7X8kN+C+h1V25cJforTyZiaXaY0Nz2Vq+mphnmGl9tettg/ZUiyqPm3Tt6SSJFdhdk1y5SO8LtTB+2VLHYe8e+XiEI2YGXm4NJ2G79pXooDSrmYV0CZ0uT6w3w7F9vmPEF0fvxtEN5D9SMhRfWlJ6rV/Mhnf46JmZTC5nGXhCwMG/uhWQbywhgLtQyMGzehsKxZyW5SSGq80RwHhmpYMN9F6Ekq8I6UTC/KHqt1UhQIDAQAB";

    private static final String mediationKey = "CE61BF07159274D6O67E17937D7A51DC7O49479B778C212A30OA1D3454BF7B738D9";





    public static String getAdMobKey(Context context) {
        return  "df03" + "8f9a" + "c585" + "4e20";
        //UserPreferences prefs = UserPreferences.getInstance(context);

/*        try{
            prefs.getKey1();
            prefs.getKey2();
            //df03 8f9a c585 4e20
            Log.e("ENCODED - adMob1!!!", CryptHelperDES.encrypt(prefs.getKey12(), "df03"));
            Log.e("ENCODED - adMob2!!!", CryptHelperDES.encrypt(prefs.getKey12(), "8f9a"));
            Log.e("ENCODED - adMob3!!!", CryptHelperDES.encrypt(prefs.getKey12(), "c585"));
            Log.e("ENCODED - adMob4!!!", CryptHelperDES.encrypt(prefs.getKey12(), "4e20"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        /*String key = prefs.getKey1() + prefs.getKey2();
        try{
            String[] chunks = mediationKey.split("O");
            StringBuilder b = new StringBuilder();
            for (String c: chunks)
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), c));

            //Log.e("DECODED - adwirl!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }


    public static String getAdwhirlKey(Context context) {

        return  "1543619" + "c1de44e80" + "af13f86" + "1b204b778";
        //UserPreferences prefs = UserPreferences.getInstance(context);

/*        try{
            prefs.getKey1();
            prefs.getKey2();
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
        /*String key = prefs.getKey1() + prefs.getKey2();
        try{
            String[] chunks = adwhirlKey.split("H");
            StringBuilder b = new StringBuilder();
            for (String c: chunks)
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), c));

            //Log.e("DECODED - adwirl!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
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
        return  "6ac99625-" + "6d02-4326" + "-becd-213a233c511a";
        //UserPreferences prefs = UserPreferences.getInstance(context);

/*        try{
            prefs.getKey1();
            prefs.getKey2();
            Log.e("ENCODED - TjAppKey1!!!", CryptHelperDES.encrypt(prefs.getKey12(), "6ac99625"));
            Log.e("ENCODED - TjAppKey2!!!", CryptHelperDES.encrypt(prefs.getKey12(), "6d02"));
            Log.e("ENCODED - TjAppKey3!!!", CryptHelperDES.encrypt(prefs.getKey12(), "4326"));
            Log.e("ENCODED - TjAppKey4!!!", CryptHelperDES.encrypt(prefs.getKey12(), "becd"));
            Log.e("ENCODED - TjAppKey5!!!", CryptHelperDES.encrypt(prefs.getKey12(), "213a233c511a"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

        /*String key = prefs.getKey1() + prefs.getKey2();
        try{
            String[] chunks = TJKey.split("J");
            StringBuilder b = new StringBuilder();

            for (int i=0; i< chunks.length; i++){//C78105DD8B3BFAE7
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), chunks[i]));
                if (i != chunks.length-1)
                    b.append('-');
            }

            //Log.e("DECODED - TJ-key!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }

    public static String getTapJoySecretKey(Context context){
        return "GvTnbQgQ1P86bB5Xgr4L";
        //UserPreferences prefs = UserPreferences.getInstance(context);


        //GvTn bQgQ 1P86 bB5X gr4L
/*        try{
            prefs.getKey1();
            Log.e("ENCODED - Tj-secret!!!1", CryptHelperDES.encrypt(prefs.getKey12(), "GvTn"));
            Log.e("ENCODED - Tj-secret!!!2", CryptHelperDES.encrypt(prefs.getKey12(), "bQgQ"));
            Log.e("ENCODED - Tj-secret!!!3", CryptHelperDES.encrypt(prefs.getKey12(), "1P86"));
            Log.e("ENCODED - Tj-secret!!!4", CryptHelperDES.encrypt(prefs.getKey12(), "bB5X"));
            Log.e("ENCODED - Tj-secret!!!5", CryptHelperDES.encrypt(prefs.getKey12(), "gr4L"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        /*String key = prefs.getKey1();
        try{
            String[] chunks = TJSecretKey.split("K");
            StringBuilder b = new StringBuilder();
            for (String c: chunks)
                b.append(CryptHelperDES.decrypt(prefs.getKey12(), c));

            //Log.e("DECODED - TJ-secret!!!", b.toString());
            return b.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        //return "PfVKO1NNtOicS512X2jA";
    }

    public static String getInAppKey(){
        //TODO: encrypt
        return gInAPPKey;
    }


    static int xpOfLevel[] = {
            0,          // 1
            2000,       // 2
            6000,       // 3
            12000,      // 4
            20000,      // 5
            30000,      // 6
            45000,      // 7
            65000,      // 8
            90000,      // 9
            120000,     // 10
            160000,     // 11
            210000,     // 12
            270000,     // 13
            340000,     // 14
            420000,     // 15
            505000,     // 16
            595000,     // 17
            695000,     // 18
            800000,     // 19
            900000,     // 20
            1000000,    // 21
            1105000,    // 22
            1210000,    // 23
            1315000,    // 24
            1425000,    // 25
            1535000,    // 26
            1645000,    // 27
            1760000,    // 28
            1880000,    // 29
            2000000,    // 30
            2125000,    // 31
            2250000,    // 32
            2375000,    // 33
            2500000,    // 34
            2625000,    // 35
            2750000,    // 36
            2875000,    // 37
            3000000,    // 38
            3150000,    // 39
            3300000,    // 40
    };

    public static int getLevel(int score){
        for (int i=1; i<xpOfLevel.length; i++){
            if (xpOfLevel[i]> score)
                return i;
        }
        return xpOfLevel.length;
    }

    public static int getLevelXp(int level){
        if (level < xpOfLevel.length)
            return xpOfLevel[level - 1];
        return Integer.MAX_VALUE;
    }

    public static boolean isMaxLevel(int level){
        return level == xpOfLevel.length;
    }
}
