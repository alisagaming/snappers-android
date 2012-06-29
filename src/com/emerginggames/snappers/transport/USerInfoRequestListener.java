package com.emerginggames.snappers.transport;

import android.content.Context;
import android.util.Log;
import com.emerginggames.snappers.UserPreferences;
import com.emerginggames.snappers.model.FbUserInfo;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.06.12
 * Time: 2:57
 */
public class UserInfoRequestListener implements AsyncFacebookRunner.RequestListener {
    Context context;
    FacebookTransport.ResponseListener listener;

    public UserInfoRequestListener(Context context, FacebookTransport.ResponseListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void onComplete(final String response, final Object state) {
        try {
            JSONObject json = Util.parseJson(response);
            FbUserInfo user = new FbUserInfo();

            user.name = json.getString("first_name");
            user.fbUID = json.getLong("id");
            UserPreferences.getInstance(context).setCurrentUser(user);

            if (listener != null)
                listener.onOk(user);

        } catch (JSONException e) {
            Log.w("SNAPPERS", "JSON Error in response");
        } catch (FacebookError e) {
            Log.w("SNAPPERS", "Facebook Error: " + e.getMessage());
        } catch (Exception e){
            Log.e("SNAPPERS", "Facebook Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFacebookError(FacebookError e, Object state) {
        if (listener != null)
            listener.onError(e);
    }

    @Override
    public void onIOException(IOException e, Object state) {
        if (listener != null)
            listener.onError(e);
    }

    @Override
    public void onFileNotFoundException(FileNotFoundException e, Object state) {
        if (listener != null)
            listener.onError(e);
    }

    @Override
    public void onMalformedURLException(MalformedURLException e, Object state) {
        if (listener != null)
            listener.onError(e);
    }
}
