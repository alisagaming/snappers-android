package ru.emerginggames.snappers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import ru.emerginggames.snappers.gdx.Resources;

public class Metrics {
    static void setSnapperMult(){
        /*                1.37f; 1.23f; 1.11f; 1; 0.9f; 0.81f; 0.73f */
        switch (sizeMode){
            case modeS:
                if (screenWidth < 300)
                    snapperMult1 = 0.9f;
                else
                    snapperMult1 = 1.11f;
                break;
            case modeM:

                if (screenWidth > 400)
                    snapperMult1 = 1;
                else
                    snapperMult1 = 0.9f;
                break;
            case modeL:

                snapperMult1 = 1;
                break;
        }
        snapperMult2 = snapperMult1 * 0.9f;
        snapperMult3 = snapperMult2 * 0.9f;
        snapperMult4 = snapperMult3 * 0.9f;
    }

    public static enum SizeMode {
        modeS, modeM, modeL
    }

    public static int snapperSize;
    public static int blastSize;
    public static int squareButtonSize;
    public static int menuButtonWidth;
    public static int menuButtonHeight;
    public static int fontSize;
    public static int largeFontSize;
    public static int screenMargin;
    public static float snapperMult1;
    public static float snapperMult2;
    public static float snapperMult3;
    public static float snapperMult4;
    public static int screenWidth;
    public static int screenHeight;
    public static int menuWidth;
    public static int menuHeight;
    public static SizeMode sizeMode;
    public static int bangSize;
    public static int hintSize;
    public static int menuMargin;
    public static final int instructionsWidth = 480;
    public static final int instructionsHeight = 640;
    public static int screenSizeMode;
    public static float squareButtonScale = 1;


    public static boolean initDone = false;
    

    public static void setSize(int width, int height, Context context){
        screenWidth = width;
        screenHeight = height;

        setScreenMode(screenWidth);
        setMetrics();
        setSnapperMult();
        Resources.init();
        initDone = true;
        if (context != null)
            screenSizeMode = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    static void setScreenMode(int width){
        if (width <400)
            sizeMode = SizeMode.modeS;
        else if (width < 600)
            sizeMode = SizeMode.modeM;
        else
            sizeMode = SizeMode.modeL;
    }

    static void setMetrics(){
        switch (sizeMode){
            case modeS:
                bangSize = 48;
                snapperSize = 48;
                blastSize = 18;
                squareButtonSize = 48;
                menuButtonWidth = 200;
                menuButtonHeight = 64;
                fontSize = 24;
                menuWidth = 236;
                menuHeight = 353;
                hintSize=64;
                menuMargin = 32;
                break;
            case modeM:
                snapperSize = 81;
                bangSize = 72;
                blastSize = 27;
                squareButtonSize = 96;
                menuButtonWidth = 300;
                menuButtonHeight = 96;
                fontSize = 38;
                menuWidth = 343;
                menuHeight = 512;
                hintSize=128;
                menuMargin = 32;
                break;
            case modeL:
                snapperSize = 108;
                bangSize = 96;
                blastSize = 36;
                squareButtonSize = 128;
                menuButtonWidth = 400;
                menuButtonHeight = 128;
                menuWidth = 472;
                menuHeight = 705;
                fontSize = 48;
                hintSize=128;
                menuMargin = 64;
                break;
        }
        screenMargin = squareButtonSize /12;
        largeFontSize = Math.round(fontSize * 1.38f);

        if (screenSizeMode == Configuration.SCREENLAYOUT_SIZE_LARGE){
            squareButtonScale = 0.8f;
        } else if (Build.VERSION.SDK_INT >= 9 && screenSizeMode == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            squareButtonScale = 0.65f;
        }

    }


}