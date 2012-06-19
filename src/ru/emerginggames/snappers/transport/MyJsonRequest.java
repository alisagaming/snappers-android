package ru.emerginggames.snappers.transport;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class MyJsonRequest implements Runnable {
    boolean isPost;
    JSONObject jsonParam;
    Map params;
    protected JsonResponseHandler handler;
    String methodName;

    protected MyJsonRequest(String methodName, boolean post) {
        this.methodName = methodName;
        isPost = post;
        this.params = params;
        this.handler = handler;
    }

    abstract void onSuccess(JSONObject object);

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
            //if (isPost)
            responce = postMessage(JsonTransport.SERVER + methodName, params);
            //else
            //    responce = getMessage(JsonTransport.SERVER + methodName, params);
            if (responce.getString("type").equalsIgnoreCase("SyncOkMessage"))
                onSuccess(responce.getJSONObject("data"));
            else throw new Exception(responce.getString("data"));
        } catch (Exception e) {
            handler.onError(e);
        }
    }

/*    JSONObject getMessage(String path, List<NameValuePair> params) throws IOException, JSONException {
        HttpClient client = getClient(true);
        HttpGet httpGet = new HttpGet(path + mapToString(params));

        ResponseHandler responseHandler = new BasicResponseHandler();
        String responce = (String) client.execute(httpGet, responseHandler);
        return new JSONObject(responce);
    }  */

    void enableLoging() {
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
    }

    public JSONObject postMessage(String path, Map params) throws Exception {
        enableLoging();
        HttpPost post = new HttpPost(new URI(path));
        if (jsonParam == null)
            jsonParam = mapToJson(params);
        post.setEntity(new StringEntity(jsonParam.toString()));

        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load(null, null);
        SSLSocketFactory sslf = new TrustAllSSLSocketFactory();

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", sslf, 443));
        schemeRegistry.register(new Scheme("https", sslf, 8080));
        SingleClientConnManager cm = new SingleClientConnManager(post.getParams(), schemeRegistry);

        HttpClient client = new DefaultHttpClient(cm, post.getParams());

        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        ResponseHandler responseHandler = new BasicResponseHandler();
        String responce = (String) client.execute(post, responseHandler);
        return new JSONObject(responce);
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
}