package com.emerginggames.snappers.transport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.emerginggames.snappers.UserPreferences;
import com.emerginggames.snappers.data.FriendTable;
import com.emerginggames.snappers.model.FacebookFriend;
import com.emerginggames.snappers.model.SyncData;
import com.facebook.android.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.06.12
 * Time: 18:20
 */
public class FacebookTransport {
    //private static final String APP_ID = "256726611099954";//that was my
    private static final String APP_ID = "342228659169507"; //that's from iphone app
    Facebook facebook;
    Activity context;
    private AsyncFacebookRunner mAsyncRunner;
    UserPreferences prefs;

    public FacebookTransport(Activity context) {
        this.context = context;
        prefs = UserPreferences.getInstance(context.getApplicationContext());

        facebook = new Facebook(APP_ID);

        String access_token = prefs.getFbToken();
        long expires = prefs.getFbExpires();
        if (access_token != null)
            facebook.setAccessToken(access_token);
        if (expires != 0)
            facebook.setAccessExpires(expires);

        mAsyncRunner = new AsyncFacebookRunner(facebook);
    }

    public void sync(ResponseListener listener) {
        UserPreferences mPrefs = UserPreferences.getInstance(context);
        JsonTransport.sync(mPrefs.getFbToken(), SyncData.load(context), new FacebookJsonListener(listener) {
            @Override
            public void onOk(Object responce) {
                ((SyncData) responce).save(context);
                super.onOk(responce);
            }
        });
    }


    public Facebook getFB() {
        return facebook;
    }

    public void getFriends(ResponseListener listener) {
        UserPreferences mPrefs = UserPreferences.getInstance(context);
        JsonTransport.getFriends(mPrefs.getFbToken(), new FacebookJsonListener(listener));
    }

    public void invite(long user_id, String message, ResponseListener listener) {
        UserPreferences mPrefs = UserPreferences.getInstance(context);
        JsonTransport.invite(mPrefs.getFbToken(), user_id, message, new FacebookJsonListener(listener));
    }

    public void gift(final FacebookFriend friend, ResponseListener listener) {
        UserPreferences mPrefs = UserPreferences.getInstance(context);
        JsonTransport.gift(mPrefs.getFbToken(), friend.facebook_id, new FacebookJsonListener(listener) {
            @Override
            public void onOk(Object responce) {
                friend.lastSendGift = System.currentTimeMillis();
                FriendTable.update(context, friend);
                super.onOk(responce);
            }
        });
    }

    public static class ResponseListener {
        public void onOk(Object data) {
        }

        public void onError(Throwable e) {
        }
    }

    public interface GiftsReceiver {
        public void giftsReceived(long[] senders);
    }

    public void getName(ResponseListener listener) {
        mAsyncRunner.request("me", new NameRequestListener(context, listener));
    }

    public boolean isLoggedIn() {
        return facebook.isSessionValid();
    }

    public void extendAccessTokenIfNeeded() {
        facebook.extendAccessTokenIfNeeded(context, new Facebook.ServiceListener() {
            @Override
            public void onComplete(Bundle values) {
                UserPreferences.getInstance(context).setFbExpires(facebook.getAccessExpires());
            }

            public void onFacebookError(FacebookError e) {
            }

            @Override
            public void onError(Error e) {
            }
        });
    }

    public void login(final ResponseListener listener1, final boolean deferRequestName) {
        facebook.authorize(context, new Facebook.DialogListener() {
            @Override
            public void onComplete(Bundle values) {
                prefs.setFb(facebook.getAccessToken(), facebook.getAccessExpires());
                if (deferRequestName) {
                    listener1.onOk(null);
                    getName(null);
                } else
                    getName(new ResponseListener() {
                        @Override
                        public void onOk(Object data) {
                            listener1.onOk(null);
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener1.onOk(null);
                        }
                    });

            }

            @Override
            public void onFacebookError(FacebookError error) {
                listener1.onError(error);
            }

            @Override
            public void onError(DialogError e) {
                listener1.onError(e);
            }

            @Override
            public void onCancel() {
                listener1.onError(null);
            }
        });
    }

    public void logoff(final ResponseListener listener) {
        mAsyncRunner.logout(context, new AsyncFacebookRunner.RequestListener() {
            @Override
            public void onComplete(String response, Object state) {
                listener.onOk(state);
                prefs.clearFb();
            }

            @Override
            public void onIOException(IOException e, Object state) {
                listener.onError(e);
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
                listener.onError(e);
            }

            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
                listener.onError(e);
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                listener.onError(e);
            }
        });
    }

    private class FacebookJsonListener implements JsonResponseHandler {
        private ResponseListener listener;

        private FacebookJsonListener(ResponseListener listener) {
            this.listener = listener;
        }

        @Override
        public void onError(final Exception error) {
            Log.e("Snappers", error.getMessage(), error);

            if (listener != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(error);
                    }
                });
        }

        @Override
        public void onOk(final Object responce) {
            if (listener != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOk(responce);
                    }
                });
        }
    }

}
