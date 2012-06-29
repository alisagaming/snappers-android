package com.emerginggames.snappers2.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import com.emerginggames.snappers2.model.Level;
import com.emerginggames.snappers2.model.LevelPack;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 3:02
 */
public class LevelDbLoader {
    private static final String LEVELS_LOADED_VERSION_TAG = "LEVELS DATABASE VERSION";
    private static final String PATH = "levels-2/%s.xml";

    private Context context;
    private AssetManager assets;
    LevelPackTable levelPackTable;
    LevelTable levelTable;
    SQLiteOpenHelper helper;
    SQLiteDatabase db;
    
    public LevelDbLoader(Context context) {
        this.context = context;
        assets = context.getAssets();
        helper = new DbCreatorOpenHelper(context);
    }

    public LevelDbLoader(Context context, SQLiteDatabase db) {
        this.context = context;
        assets = context.getAssets();
        this.db = db;
    }

    public LevelDbLoader(Context context, SQLiteOpenHelper helper) {
        this.context = context;
        this.helper = helper;
        assets = context.getAssets();
    }

    public static void checkAndLoad(Context context, SharedPreferences prefs){
        if (prefs.getInt(LEVELS_LOADED_VERSION_TAG, 0) != DbCopyOpenHelper.DATABASE_VERSION){
            LevelDbLoader loader = new LevelDbLoader(context);
            loader.load();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(LEVELS_LOADED_VERSION_TAG, DbCopyOpenHelper.DATABASE_VERSION);
            editor.commit();
        }
    }

    public void load(){
        if (db != null){
            levelTable = new LevelTable(db);
            levelPackTable = new LevelPackTable(db);
        } else {
            levelTable  = new LevelTable(helper);
            levelTable.open(true);
            levelPackTable = new LevelPackTable(helper);
            levelPackTable.open(true);
        }
        loadLevelPack("LevelPack1");
        loadLevelPack("LevelPack2");
        loadLevelPack("LevelPack3");
        loadLevelPack("LevelPack4");
        loadLevelPack("LevelPack5");
        loadLevelPack("PremiumLevelPack1");
        if (db != null)
            db.close();
        else {
            levelTable.close();
            levelPackTable.close();
        }
    }

    public LevelPack loadLevelPack(String filename){
        LevelPack pack = new LevelPack();
        try{
            InputStream stream = assets.open(String.format(PATH, filename));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(stream, "UTF-8");

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("LevelPack"))
                            parseAndSaveLevelPack(xpp, pack);
                        else if (xpp.getName().equals("level"))
                            parseAndSaveLevel(xpp, pack);
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e){
            Log.e("SNAPPERS", "err", e);
        }

        return pack;
    }

    private void parseAndSaveLevelPack(XmlPullParser xpp, LevelPack pack){
        for (int i=0; i< xpp.getAttributeCount(); i++){
            if (xpp.getAttributeName(i).equalsIgnoreCase("background"))
                pack.background = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("shadows"))
                pack.shadows = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
            else if (xpp.getAttributeName(i).equalsIgnoreCase("title"))
                pack.title = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("isGold"))
                pack.isGold = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
            else if (xpp.getAttributeName(i).equalsIgnoreCase("isPremium"))
                pack.isPremium = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
            else if (xpp.getAttributeName(i).equalsIgnoreCase("name"))
                pack.name = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("levelIcon"))
                pack.levelIcon = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("sound"))
                pack.soundtrack = xpp.getAttributeValue(i);
        }

        levelPackTable.insert(pack);
    }

    private void parseAndSaveLevel(XmlPullParser xpp, LevelPack pack){
        Level level = new Level();
        level.packNumber = pack.id;

        for (int i=0; i< xpp.getAttributeCount(); i++){
            if (xpp.getAttributeName(i).equalsIgnoreCase("number"))
                level.number = Integer.parseInt(xpp.getAttributeValue(i));
            else if (xpp.getAttributeName(i).equalsIgnoreCase("complexity"))
                level.complexity = Integer.parseInt(xpp.getAttributeValue(i));
            else if (xpp.getAttributeName(i).equalsIgnoreCase("zappers"))
                level.zappers = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("solutions"))
                level.solutions = xpp.getAttributeValue(i);
            else if (xpp.getAttributeName(i).equalsIgnoreCase("tapsCount"))
                level.tapsCount = Integer.parseInt(xpp.getAttributeValue(i));
        }
        levelTable.insert(level);
    }

}
