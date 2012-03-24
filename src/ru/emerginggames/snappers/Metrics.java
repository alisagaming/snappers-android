package ru.emerginggames.snappers;

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
                snapperMult1 = 1.23f;
                snapperMult2 = 1.11f;
                snapperMult3 = 1;
                snapperMult4 = 0.9f;
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
    public static SizeMode sizeMode;

    public static void setSize(int width, int height){
        screenWidth = width;
        screenHeight = height;

        setScreenMode(screenWidth);
        setMetrics();
        setSnapperMult();
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
                snapperSize = 32;
                blastSize = 14;
                squareButtonSize = 64;
                menuButtonWidth = 200;
                fontSize = 32;
                break;
            case modeM:
                snapperSize = 48;
                blastSize = 18;
                squareButtonSize = 80;
                menuButtonWidth = 312;
                menuButtonHeight = 100;
                fontSize = 38;
                break;
            case modeL:
                snapperSize = 96;
                blastSize = 36;
                squareButtonSize = 128;
                menuButtonWidth = 400;
                menuButtonHeight = 128;
                fontSize = 48;
                break;
        }
        screenMargin = squareButtonSize /12;
        largeFontSize = fontSize * 3/2;
    }


}