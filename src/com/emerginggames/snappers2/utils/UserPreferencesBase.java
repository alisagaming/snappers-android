package com.emerginggames.snappers2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.emerginggames.snappers2.R;
import com.emerginggames.snappers2.Settings;
import com.emerginggames.snappers2.data.CryptHelperAES;
import com.emerginggames.snappers2.data.LevelPackTable;
import com.emerginggames.snappers2.data.LevelTable;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 28.06.12
 * Time: 17:15
 */
public class UserPreferencesBase {
    private static final String PREFERENCES = "Preferences";
    protected Context context;
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor editor;
    DeviceUuidFactory factory;
    public static String Key1;
    public static String Key11;
    public static String Key21;
    public static String Key2;
    public static String Key3;
    public static String Key4;
    public static String Key5;

    public UserPreferencesBase(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (LevelPackTable.getName().equals("vitaliy.suprun"))
            if (LevelPackTable.getHost().equals("gmail.com")){
                if (Key1 == null)
                    factory = new DeviceUuidFactory(context);
                getKey1();
                getKey2();
                getKey3();
            }

    }

    private String _K(String key){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return key;
            else
                return CryptHelperAES.encrypt(getKey3(), key);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String _S(String s, String salt){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.encrypt(salt + getKey3(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String deS(String s, String salt){
        try{
            if (Settings.NO_PREF_ENCRYPT)
                return s;
            else
                return CryptHelperAES.decrypt(salt + getKey3(), s);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    protected int getInt(String key, int def, String salt){
        try {
            return Integer.parseInt(getString(key, salt));
        }catch (Exception e){
            return def;
        }
    }

    protected void putInt(String key, int val, String salt){
        putString(key, Integer.toString(val), salt);
    }

    protected long getLong(String key, int def){
        try {
            return Long.parseLong(getString(key));
        }catch (Exception e){
            return def;
        }
    }

    protected void putLong(String key, long val){
        putString(key, Long.toString(val));
    }

    protected void putString(String key, String val){
        putString(key, val, key);
    }

    protected void putString(String key, String val, String salt){
        if (editor == null){
            SharedPreferences.Editor edt = prefs.edit();
            edt.putString(_K(key), _S(val, salt));
            edt.commit();
        }
        else
            editor.putString(_K(key), _S(val, salt));
    }

    protected String getString(String key, String salt){
        try {
            return deS(prefs.getString(_K(key), null), salt);
        }catch (Exception e){
            return null;
        }
    }

    protected String getString(String key){
        return getString(key, key);
    }

    protected boolean getBoolean(String key, boolean def){
        try {
            String val = getString(key);
            return val == null ? def : Boolean.parseBoolean(val);
        }catch (Exception e){
            return def;
        }
    }

    protected void putBoolean(String key, boolean val){
        putString(key, Boolean.toString(val));
    }

    protected void remove(String key){
        if (editor == null){
            SharedPreferences.Editor edt = prefs.edit();
            edt.remove(_K(key));
            edt.commit();
        }
        else
            editor.remove(_K(key));
    }

    public String getKey1(){
        if (Key1 == null)
            Key1 = context.getString(R.string.app_name) + LevelPackTable.MAIL;
        Key11 = Key1;
        return Key1;
    }

    public String getKey3(){
        if (Key3 == null)
            Key3 = factory.getDeviceUuid().toString() + LevelPackTable.getHost();
        return Key3;
    }

    public String getKey12(){
        String res = Key11;
        Key11 = Key21;
        return Key21 = res;
    }

    public String getKey2(){
        if (Key2 == null)
            Key2 = LevelPackTable.getHost() + LevelTable.getMail();
        Key21 = Key2;
        return Key2;
    }


}
