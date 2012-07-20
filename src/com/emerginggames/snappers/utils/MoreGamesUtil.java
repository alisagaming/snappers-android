package com.emerginggames.snappers.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.emerginggames.snappers.Settings;
import com.emerginggames.snappers.data.FileHelper;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.emerginggames.snappers.model.MoreGame;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 20.07.12
 * Time: 0:34
 * To change this template use File | Settings | File Templates.
 */
public class MoreGamesUtil {
    private static final int RETRY_INTERVAL = 1 * 60 * 1000;
    private static final long JSON_REFRESH_INTERVAL = 24 * 60 * 60 * 1000;
    private static final long BITMAP_REFRESH_INTERVAL = 24 * 60 * 60 * 1000;
    Context mContext;
    private static final String MORE_GAMES_FILE = "moreGames.json";

    private MoreGamesUtil(Context context) {
        this.mContext = context;
    }

    public static void download(Context context) {
        MoreGamesUtil util = new MoreGamesUtil(context);
        util.new MoreGamesGetter(load(context)).exec();
    }

    private static List<MoreGame> parseJson(JSONObject data, Context context) {
        List<MoreGame> games = new ArrayList<MoreGame>();
        String packageName = context.getPackageName();
        try {
            JSONArray gamesArr = data.getJSONArray(Settings.IS_AMAZON ? "amazon" : "googleplay");
            for (int i = 0; i < gamesArr.length(); i++) {
                JSONObject jsonGame = gamesArr.getJSONObject(i);
                MoreGame moreGame = new MoreGame(jsonGame);
                if (!packageName.equals(moreGame.id))
                    games.add(moreGame);
            }

        } catch (JSONException e) {
        }
        return games;
    }

    void saveJson(JSONObject data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(mContext.getFileStreamPath(MORE_GAMES_FILE)));
            writer.write(data.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    void touchJsonFile(){
        File file = mContext.getFileStreamPath(MORE_GAMES_FILE);
        file.setLastModified(System.currentTimeMillis());
    }

    public static List<MoreGame> load(Context mContext) {
        FileInputStream instrm = null;
        try {
            instrm = new FileInputStream(mContext.getFileStreamPath(MORE_GAMES_FILE));
            byte total[] = new byte[instrm.available()];
            instrm.read(total, 0, total.length);
            String asString = new String(total, 0, total.length, "utf-8");
            JSONObject data = new JSONObject(asString);

            return parseJson(data, mContext);
        } catch (FileNotFoundException e) {}
        catch (UnsupportedEncodingException e) {}
        catch (IOException e) {}
        catch (JSONException e) {}
        finally {
            if (instrm != null)
                try {
                    instrm.close();
                } catch (IOException ignored) {}
        }

        return null;
    }

    public static Bitmap getIcon(MoreGame game, Context context){
        File file = getImageFileS(game, context);
        if (!file.exists())
            return null;
        try {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    protected File getImageFile(MoreGame game){
        return getImageFileS(game, mContext);
    }

    protected static File getImageFileS(MoreGame game, Context context){
        return context.getFileStreamPath(game.id + ".png");
    }

    private class MoreGamesGetter implements Runnable {
        HandlerThread thread;
        Handler handler;
        List<MoreGame> currentMoreGames;

        private MoreGamesGetter(List<MoreGame> currentMoreGames) {
            this.currentMoreGames = currentMoreGames;
            thread = new HandlerThread("MoreGamesDownloader");
            thread.start();
            handler = new Handler(thread.getLooper());
        }

        void exec() {
            handler.post(this);
        }

        @Override
        public void run() {
            File jsonFile = mContext.getFileStreamPath(MORE_GAMES_FILE);
            if (currentMoreGames == null || !jsonFile.exists() || (System.currentTimeMillis() - jsonFile.lastModified() > JSON_REFRESH_INTERVAL))
                getAndSave();
            else
                saveImages(currentMoreGames);
        }

        void getAndSave(){
            try {
                JSONObject data = download();
                List<MoreGame> games = parseJson(data, mContext);

                if (!games.equals(currentMoreGames))
                    saveJson(data);
                else
                    touchJsonFile();

                saveImages(games);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        thread.quit();
                    }
                });
                return;
            } catch (IOException e) {
                Log.e("Snappers", e.getMessage(), e);
            } catch (JSONException e) {
                Log.e("Snappers", e.getMessage(), e);
            }
            handler.postDelayed(this, RETRY_INTERVAL);
        }

        JSONObject download() throws IOException, JSONException {
            HttpGet get = new HttpGet(Settings.MoreGamesURL);
            HttpClient client = new DefaultHttpClient(get.getParams());
            return new JSONObject(client.execute(get, new BasicResponseHandler()));
        }

        void saveImages(List<MoreGame> games) {
            for (MoreGame game : games)
                handler.post(new MoreGameIconDownloader(game));
        }

    }

    private class MoreGameIconDownloader implements Runnable {
        MoreGame moreGame;

        private MoreGameIconDownloader(MoreGame moreGame) {
            this.moreGame = moreGame;
        }

        @Override
        public void run() {
            try {
                File imgFile = getImageFile(moreGame);
                long lastUpdateTime = imgFile.lastModified();
                if (System.currentTimeMillis() - lastUpdateTime < BITMAP_REFRESH_INTERVAL)
                    return;

                URL url = new URL(moreGame.icon);
                InputStream responseStream = url.openConnection().getInputStream();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                FileHelper.copyFile(responseStream, outputStream);
                byte [] newFileBytes = outputStream.toByteArray();
                outputStream.reset();

                byte oldFileBytes[] = null;

                if (imgFile.exists()){
                    FileInputStream oldFileStream = new FileInputStream(imgFile);
                    oldFileBytes = new byte[oldFileStream.available()];
                    oldFileStream.read(oldFileBytes, 0, oldFileBytes.length);
                    oldFileStream.close();
                }

                if (!Arrays.equals(newFileBytes, oldFileBytes)){
                    FileOutputStream outFile = new FileOutputStream(imgFile);
                    outFile.write(newFileBytes);
                    outFile.flush();
                    outFile.close();
                } else
                    imgFile.setLastModified(System.currentTimeMillis());
            }
            catch (ClientProtocolException e) {}
            catch (IOException e) {}
        }
    }

}
