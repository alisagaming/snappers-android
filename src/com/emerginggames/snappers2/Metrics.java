package com.emerginggames.snappers2;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import com.emerginggames.snappers2.gdx.Resources;

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
        snapperMult0 = snapperMult1 / 0.85f;
    }

    public static enum SizeMode {
        modeS, modeM, modeL
    }

    public static int snapperSize;
    public static int squareButtonSize;
    public static int fontSize;
    public static int largeFontSize;
    public static int screenMargin;
    public static float snapperMult0;
    public static float snapperMult1;
    public static float snapperMult2;
    public static float snapperMult3;
    public static float snapperMult4;
    public static int screenWidth;
    public static int screenHeight;
    public static SizeMode sizeMode;
    public static int hintSize;
    public static int screenSizeMode;
    public static float squareButtonScale = 1;
    public static int bgSourceWidth;
    public static int bgSourceHeight;

    public static boolean initDone = false;

    public static void setSizeByView(View v, Context context){
        Rect r = new Rect();
        v.getWindowVisibleDisplayFrame(r);
        Metrics.setSize(r.width(), r.height(), context);
    }

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
                snapperSize = 48;
                squareButtonSize = 48;
                fontSize = 22;
                hintSize=64;
                bgSourceWidth = 320;
                bgSourceHeight = 480;
                break;
            case modeM:
                snapperSize = 80;
                squareButtonSize = 80;
                fontSize = 34;
                hintSize=128;
                bgSourceWidth = 512;
                bgSourceHeight = 854;
                break;
            case modeL:
                snapperSize = 108;
                squareButtonSize = 106;
                fontSize = 48;
                hintSize=128;
                bgSourceWidth = 640;
                bgSourceHeight = 960;
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