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

    public int getSnapper(int i, int j){
        return snappers[i][j];
    }

    public int touchSnapper(int i, int j){
        if (snappers[i][j] > 0)
            return --snappers[i][j];
        else return -1;
    }
    
    public int countSnappers(){
        int count=0;
        for (int i=0; i<WIDTH; i++)
            for (int j=0; j< HEIGHT; j++)
                if (snappers[i][j]>0)
                    count++;
        return count;
    }
    
    public void setSnappers(String snappersStr){
        for (int i=0; i<WIDTH; i++)
            for (int j=0; j< HEIGHT; j++)
                snappers[i][j] = Integer.parseInt(Character.toString(snappersStr.charAt(j * WIDTH + i)));
    }

    public static boolean isValidSnapper(int i, int j){
        return i>=0 && i<WIDTH && j>=0 && j<HEIGHT;
    }
}
