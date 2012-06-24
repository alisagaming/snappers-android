package com.emerginggames.snappers.transport;

import android.content.Context;
import android.util.Log;
import com.emerginggames.snappers.UserPreferences;
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
public class NameRequestListener implements AsyncFacebookRunner.RequestListener {
    Context context;
    FacebookTransport.ResponseListener listener;

    public NameRequestListener(Context context, FacebookTransport.ResponseListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void onComplete(final String response, final Object state) {
        try {
            JSONObject json = Util.parseJson(response);
            final String name = json.getString("first_name");
            UserPreferences prefs = UserPreferences.getInstance(context);
            if (name != null && !name.equalsIgnoreCase(prefs.getFacebookUserName()))
                prefs.setFacebookUserName(name);
            if (listener != null)
                listener.onOk(state);

        } catch (JSONException e) {
            Log.w("SNAPPERS", "JSON Error in response");
        } catch (FacebookError e) {
            Log.w("SNAPPERS", "Facebook Error: " + e.getMessage());
        } catch (Exception e){
            Log.w("SNAPPERS", "Facebook Error: " + e.getMessage());
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
