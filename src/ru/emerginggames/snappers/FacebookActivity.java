package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.model.SyncData;
import ru.emerginggames.snappers.transport.JsonResponseHandler;
import ru.emerginggames.snappers.transport.JsonTransport;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 17:34
 */
public class FacebookActivity extends Activity {
    private static final String APP_ID = "256726611099954";
    Facebook facebook = new Facebook(APP_ID);
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_empty);

        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        /*
        * Only call authorize if the access_token has expired.
        */
        if (!facebook.isSessionValid()) {

            facebook.authorize(this, new Facebook.DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();

                    doSync("");
                }

                @Override
                public void onFacebookError(FacebookError error) {
                    finish();
                }

                @Override
                public void onError(DialogError e) {
                    finish();
                }

                @Override
                public void onCancel() {
                    finish();
                }
            });
        }
        else {
            doSync("");
            facebook.extendAccessTokenIfNeeded(this, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    void doSync(String code){
        JsonTransport.sync(mPrefs.getString("access_token", null), "", getSyncDataFromPrefs(), new JsonResponseHandler(){
            @Override
            public void onError(Exception error) {
            }

            @Override
            public void onOk(Object responce) {
                saveSyncDataToPrefs((SyncData)responce);
            }
        });
    }

    public void getFriends(){

    }

    public SyncData getSyncDataFromPrefs(){
        SyncData syncData = new SyncData();
        UserPreferences prefs = UserPreferences.getInstance(this);
        syncData.hint_count = prefs.getHintsRemaining();
        syncData.xp_count = prefs.getScore();
        syncData.xp_level = Settings.getLevel(syncData.xp_count);

        for (LevelPack pack: LevelPackTable.getAll(this)){
            int n = prefs.getLevelUnlocked(pack);
            if (n > 0)
                syncData.addLevelUnlock(pack, n);
        }

        return syncData;
    }

    public void saveSyncDataToPrefs(SyncData data){
        UserPreferences prefs = UserPreferences.getInstance(this);
        prefs.setHints(data.hint_count);
        prefs.setScore(data.xp_count);

        for (Map.Entry<String, Integer> pairs : data.levelUnlocks.entrySet()) {
            String key = pairs.getKey();
            Integer value = pairs.getValue();

            prefs.unlockLevel(key, value);
        }

    }
}