package ru.emerginggames.snappers;

import android.content.Context;
import android.util.Log;
import ru.emerginggames.snappers.data.CryptHelperDES;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

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


    public static String getAdwhirlKey(Context context){
        try{
            Log.e("ENCODED!!!", CryptHelperDES.encrypt(UserPreferences.getInstance(context).getKey1(), "af87a4cc66d54347"));
            Log.e("ENCODED!!!", CryptHelperDES.encrypt(UserPreferences.getInstance(context).getKey1(), "b277ff8b6c588b21"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try{
            String key = UserPreferences.getInstance(context).getKey1();
            //"af87a4cc66d54347" + "b277ff8b6c588b21";
            return CryptHelperDES.decrypt(key, "2604ECD2F2A403DA94163A6CD1764D8AD097FFBD67B0A097") +
                    CryptHelperDES.decrypt(key, "52578AFF92CA7D45A5053355BA5315A1F32C18E2DD845681");

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
}
