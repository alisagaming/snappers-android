package ru.emerginggames.snappers.model;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 11.03.12
 * Time: 23:37
 */
public class Snappers {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 6;
    public int[][] snappers = new int[WIDTH][HEIGHT];
    public int snappersCount;

    public int getSnapper(int i, int j){
        return snappers[i][j];
    }

    public int touchSnapper(int i, int j){
        if (snappers[i][j] > 0){
            if (--snappers[i][j] == 0)
                snappersCount--;
            return snappers[i][j];
        }
        else return -1;
    }
    
    public void setSnappers(String snappersStr){
        snappersCount = 0;
        for (int i=0; i<WIDTH; i++)
            for (int j=0; j< HEIGHT; j++){
                snappers[i][j] = Integer.parseInt(Character.toString(snappersStr.charAt(j * WIDTH + i)));
                if (snappers[i][j]>0)
                    snappersCount++;
            }
    }

    public static boolean isValidSnapper(int i, int j){
        return i>=0 && i<WIDTH && j>=0 && j<HEIGHT;
    }
}
