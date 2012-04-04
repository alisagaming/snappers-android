package ru.emerginggames.snappers;

import ru.emerginggames.snappers.gdx.Resources;

public class Metrics {
    static void setSnapperMult(){
        switch (sizeMode){
            case modeS:
                snapperMult1 = 1;
                snapperMult2 = 0.9f;
                snapperMult3 = 0.81f;
                snapperMult4 = 0.73f;
                break;
            case modeM:
                /*snapperMult1 = 1.37f;
                snapperMult2 = 1.23f;
                snapperMult3 = 1.11f;
                snapperMult4 = 1;*/
                snapperMult1 = 1;
                snapperMult2 = 0.9f;
                snapperMult3 = 0.81f;
                snapperMult4 = 0.73f;
                break;
            case modeL:
                snapperMult1 = 1;
                snapperMult2 = 0.9f;
                snapperMult3 = 0.81f;
                snapperMult4 = 0.73f;
                break;
        }
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

    public static boolean initDone = false;
    

    public static void setSize(int width, int height){
        screenWidth = width;
        screenHeight = height;

        setScreenMode(screenWidth);
        setMetrics();
        setSnapperMult();
        Resources.init();
        initDone = true;
    }

    static void setScreenMode(int width){
        if (width <320)
            sizeMode = SizeMode.modeS;
        else if (width < 600)
            sizeMode = SizeMode.modeM;
        else
            sizeMode = SizeMode.modeL;
    }

    static void setMetrics(){
        switch (sizeMode){
            case modeS:
                bangSize = snapperSize = 32;
                blastSize = 14;
                squareButtonSize = 64;
                menuButtonWidth = 200;
                fontSize = 32;
                break;
            case modeM:
                snapperSize = 81;
                bangSize = 48;
                blastSize = 18;
                squareButtonSize = 80;
                menuButtonWidth = 287;
                menuButtonHeight = 92;
                fontSize = 38;
                menuWidth = 343;
                menuHeight = 512;
                //512*343 - long menu, size *.92
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
                break;
        }
        screenMargin = squareButtonSize /12;
        largeFontSize = Math.round(fontSize * 1.38f);
    }


}