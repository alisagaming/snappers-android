package ru.emerginggames.snappers.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.LevelPack;


import java.io.InputStream;
import java.util.logging.ConsoleHandler;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 3:02
 */
public class LevelDbLoader {
    private static final String LEVELS_LOADED_VERSION_TAG = "LEVELS DATABASE VERSION";

    private Context context;
    private SharedPreferences prefs;
    private AssetManager assets;
    LevelPackTable levelPackTable;
    LevelTable levelTable;
    
    public LevelDbLoader(Context context, SharedPreferences prefs) {
        this.context = context;
        this.prefs = prefs;
        assets = context.getAssets();
    }

    public static void checkAndLoad(Context context, SharedPreferences prefs){
        if (prefs.getInt(LEVELS_LOADED_VERSION_TAG, 0) != DbOpenHelper.DATABASE_VERSION){
            LevelDbLoader loader = new LevelDbLoader(context, prefs);
            LevelPack pack1 = loader.loadLevelPack("LevelPack1");
            loader.levelPackTable.unlockLevelPack(pack1.id);
            loader.loadLevelPack("LevelPack2");
            loader.loadLevelPack("LevelPack3");
            loader.loadLevelPack("LevelPack4");
            loader.loadLevelPack("LevelPack5");
            loader.loadLevelPack("LevelPack6");
            loader.loadLevelPack("LevelPack7");
            loader.loadLevelPack("PremiumLevelPack1");
            loader.loadLevelPack("PremiumLevelPack2");
            loader.loadLevelPack("PremiumLevelPack3");
            loader.loadLevelPack("PremiumLevelPack4");
            loader.levelTable.close();
            loader.levelPackTable.close();
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(LEVELS_LOADED_VERSION_TAG, DbOpenHelper.DATABASE_VERSION);
            editor.commit();
        }
    }

    public LevelPack loadLevelPack(String filename){
        LevelPack pack = new LevelPack();
        try{
            InputStream stream = assets.open(String.format("levels/%s.xml", filename));
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
                pack.shadows = xpp.getAttributeValue(i).equalsIgnoreCase("yes");
        }

        pack.isUnlocked = false;
        pack.levelsUnlocked = 0;

        pack.id = (int) levelPackTable.insert(pack);
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
