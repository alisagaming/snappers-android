package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.FacebookFriend;
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

                    getFriends();
                    doSync();
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
            getFriends();
            //doSync();
            facebook.extendAccessTokenIfNeeded(this, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    void doSync(){
        JsonTransport.sync(mPrefs.getString("access_token", null), SyncData.load(this), new JsonResponseHandler(){
            @Override
            public void onError(Exception error) {
                Log.e("Snappers", error.getMessage(), error);
                play();
            }

            @Override
            public void onOk(Object responce) {
                ((SyncData)responce).save(FacebookActivity.this);
            }
        });
    }

    public void getFriends(){
        JsonTransport.getFriends(mPrefs.getString("access_token", null), new JsonResponseHandler() {
            @Override
            public void onOk(Object responce) {
                showFriendsList((FacebookFriend[])responce);
            }

            @Override
            public void onError(Exception error) {
                Log.e("Snappers", error.getMessage(), error);
                play();
            }
        });
    }

    void showFriendsList(FacebookFriend[] friends){

    }

    void play(){
        startActivity(new Intent(this, SelectPackActivity.class));
    }


}