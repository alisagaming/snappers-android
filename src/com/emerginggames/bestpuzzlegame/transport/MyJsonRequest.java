package com.emerginggames.bestpuzzlegame.transport;

import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

public abstract class MyJsonRequest implements Runnable {
    private static final String TAG = "Snappers/MyJsonRequest";
    boolean isPost;
    boolean debug;
    JSONObject jsonParam;
    Map params;
    protected JsonResponseHandler handler;
    String methodName;
    private static final int SOCKET_TIMEOUT = 15000;


    protected MyJsonRequest(String methodName, boolean post) {
        this.methodName = methodName;
        isPost = post;
    }

    abstract void onSuccess(Object object);

    abstract boolean isResponceOk(JSONObject object) throws JSONException;

    public void setHandler(JsonResponseHandler handler) {
        this.handler = handler;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public void setParams(JSONObject jsonParam) {
        this.jsonParam = jsonParam;
    }

    @Override
    public void run() {
        JSONObject responce;
        try {
            if (isPost)
                responce = postMessage(JsonTransport.SERVER + methodName);
            else
                responce = getMessage(JsonTransport.SERVER + methodName);
            if (isResponceOk(responce)){
                if (responce.has("data"))
                    onSuccess(responce.get("data"));
                else
                    onSuccess(responce);
            }


            else throw new Exception(responce.getString("data"));
        } catch (Exception e) {
            new Exception("error in method " + methodName, e);
            handler.onError(e);
        }
    }

    JSONObject getMessage(String path) throws Exception {
        HttpGet httpGet = new HttpGet(path + mapToString(params));
        HttpClient client = getClient(httpGet.getParams());

        if (debug)
            Log.d(TAG, String.format("Get request: %s", path + mapToString(params)));


        httpGet.setHeader("Accept", "application/json");

        ResponseHandler responseHandler = new BasicResponseHandler();
        String responce = (String) client.execute(httpGet, responseHandler);
        if (debug)
            Log.d(TAG, String.format("Get responce: %s\n%s", path, responce));
        return new JSONObject(responce);
    }

    void enableLoging() {
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
    }

    public JSONObject postMessage(String path) throws Exception {
        enableLoging();
        HttpPost post = new HttpPost(new URI(path));
        if (jsonParam == null)
            jsonParam = mapToJson(params);
        if (jsonParam != null)
            post.setEntity(new StringEntity(jsonParam.toString()));
        if (debug && jsonParam != null)
            Log.d(TAG, String.format("Post request: %s\n%s", path, jsonParam.toString()));


        HttpClient client = getClient(post.getParams());

        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        ResponseHandler responseHandler = new BasicResponseHandler();
        String responce = (String) client.execute(post, responseHandler);
        if (debug)
            Log.d(TAG, String.format("Post responce: %s\n%s", path, responce));

        return new JSONObject(responce);
    }

    HttpClient getClient(HttpParams params) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load(null, null);
        SSLSocketFactory sslf = new TrustAllSSLSocketFactory();

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", sslf, 443));
        schemeRegistry.register(new Scheme("https", sslf, 8080));
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        SingleClientConnManager cm = new SingleClientConnManager(params, schemeRegistry);
        return new DefaultHttpClient(cm, params);
    }

    JSONObject mapToJson(Map params) throws JSONException {
        JSONObject holder = new JSONObject();

        for (Object o : params.entrySet()) {
            Map.Entry pairs = (Map.Entry) o;
            String key = (String) pairs.getKey();
            Object value = pairs.getValue();
            if (value instanceof Map)
                holder.put(key, mapToJson((Map) value));
            else
                holder.put(key, value);
        }
        return holder;
    }

    String mapToString(Map params) {

        StringBuilder builder = new StringBuilder();

        builder.append("?");
        for (Object o : params.entrySet()) {
            Map.Entry pairs = (Map.Entry) o;
            String key = (String) pairs.getKey();
            Object value = pairs.getValue();
            builder.append(key);
            builder.append("=");
            builder.append(value.toString());
            builder.append("&");
        }
        return builder.toString();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}