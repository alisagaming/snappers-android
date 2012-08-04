package com.emerginggames.snappers2.utils;

import android.content.Context;
import android.util.Log;
import com.emerginggames.snappers2.Settings;
import com.emerginggames.snappers2.data.FileHelper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 04.08.12
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public class CachedFile implements Runnable {
    Context context;
    String path;
    String urlStr;
    CachedFileListener listener;
    private static final long REFRESH_INTERVAL = 24 * 60 * 60 * 1000;

    public CachedFile(Context context, String path, String url, CachedFileListener listener) {
        this.context = context;
        this.path = path;
        this.urlStr = url;
        this.listener = listener;
    }

    public CachedFile(Context context) {
        this.context = context;
    }

    public void setData(String path, String url){
        this.path = path;
        this.urlStr = url;
    }

    public void setListener(CachedFileListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        File file = context.getFileStreamPath(path);

        if (file.exists())
            listener.onGotFile(file);

        if (!shouldUpdate(file))
            return;

        try {
            URL url = new URL(urlStr);

            InputStream responseStream = url.openConnection().getInputStream();

            ByteArrayOutputStream downloadStream = new ByteArrayOutputStream();
            FileHelper.copyFile(responseStream, downloadStream);
            byte [] newFileBytes = downloadStream.toByteArray();
            downloadStream.reset();

            byte oldFileBytes[] = null;

            if (file.exists()){
                FileInputStream oldFileStream = new FileInputStream(file);
                oldFileBytes = new byte[oldFileStream.available()];
                oldFileStream.read(oldFileBytes, 0, oldFileBytes.length);
                oldFileStream.close();
            }

            if (!Arrays.equals(newFileBytes, oldFileBytes)){
                FileOutputStream outFile = new FileOutputStream(file);
                outFile.write(newFileBytes);
                outFile.flush();
                outFile.close();
                listener.onUpdateFile(file);
            } else
                file.setLastModified(System.currentTimeMillis());

        } catch (MalformedURLException e) {
            listener.onError(e);
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    boolean shouldUpdate(File file){
        return ! (file.exists() && System.currentTimeMillis() - file.lastModified() < REFRESH_INTERVAL);
    }

    public static interface CachedFileListener{
        public void onGotFile(File file);
        public void onUpdateFile(File file);
        public void onError(Throwable t);
    }
}
