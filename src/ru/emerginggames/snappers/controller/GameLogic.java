package ru.emerginggames.snappers.controller;

import android.graphics.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 0:09
 */
public class GameLogic {
    private static final int MAX_BLASTS = 90;
    private static final float GRAIN_SPEED_MARGIN_PER_SECOND = 6;
    public final List<Blast> blastsToKill;
    public final Pool<Blast> blastPool;
    public final List<Blast> activeBlasts;
    public final List<Blast> newBlasts;
    public final Snappers snappers;
    public int width;
    public int height;
    public float xSnapperMargin;
    public float ySnapperMargin;
    private Rect snappersRect;
    public Level level;
    public int tapRemains;
    ILogicListener snapperListener;
    private float grainSpeedX;
    private float grainSpeedY;

    private float timeToSyncCheck;

    public GameLogic(ILogicListener listener) {

        PoolObjectFactory<Blast> grainFactory = new PoolObjectFactory<Blast>() {
            @Override
            public Blast createObject() {
                Blast blast = new Blast();
                return blast;
            }
        };
        blastPool = new Pool<Blast>(grainFactory, MAX_BLASTS);
        activeBlasts = new ArrayList<Blast>(MAX_BLASTS);
        blastsToKill = new ArrayList<Blast>(MAX_BLASTS);
        newBlasts = new ArrayList<Blast>(MAX_BLASTS);
        snappers = new Snappers();

        snapperListener = listener;
    }

    public void setScreen(int width, int height, Rect snappersRect){
        this.snappersRect = snappersRect;
        this.width = width;
        this.height = height;

        xSnapperMargin = snappersRect.width() / 2.0f / Snappers.WIDTH;
        ySnapperMargin = Math.abs(snappersRect.height() / 2.0f / Snappers.HEIGHT);
        grainSpeedX = xSnapperMargin * GRAIN_SPEED_MARGIN_PER_SECOND;
        grainSpeedY = ySnapperMargin * GRAIN_SPEED_MARGIN_PER_SECOND;


    }

    public void startLevel(Level level) {
        for (Blast blast : activeBlasts)
            blastPool.free(blast);
        activeBlasts.clear();
        snappers.setSnappers(level.zappers);
        tapRemains = level.tapsCount;
        this.level = level;
        timeToSyncCheck = 0;
    }

    public boolean touchSnapper2(int i, int j) {//return true if consumed touch
        int touchResult = snappers.touchSnapper(i, j);
        if (touchResult <0)
            return false;
        snapperListener.snapperTouched(i, j);
        if (touchResult == 0) {
            int xPos = getSnapperXPosision(i);
            int yPos = getSnapperYPosision(j);
            launchBlast2(xPos, yPos, Blast.Direction.Down, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Right, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Up, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Left, i, j);
            return true;
        }
        return true;
    }

    public int getSnapperXPosision(int i) {
        return snappersRect.left + Math.round(xSnapperMargin * (2 * i + 1));
    }

    public int getSnapperYPosision(int j) {
        return snappersRect.bottom + Math.round(ySnapperMargin * (2 * j + 1));
    }

    private Blast launchBlast2(int x, int y, Blast.Direction direction, int i, int j) {
        Blast blast = blastPool.newObject();
        blast.x = x;
        blast.y = y;
        blast.direction = direction;
        blast.destI = i;
        blast.destJ = j;
        setNextBlastDestination(blast);
        newBlasts.add(blast);
        blast.age = 0;
        return blast;
    }

    private void setNextBlastDestination(Blast blast) {
        int pos;
        switch (blast.direction) {
            case Up:
                pos = ++blast.destJ;
                blast.dest = pos >= Snappers.HEIGHT ? height : getSnapperYPosision(pos);
                break;
            case Right:
                pos = ++blast.destI;
                blast.dest = pos >= Snappers.WIDTH ? width : getSnapperXPosision(pos);
                break;
            case Down:
                pos = --blast.destJ;
                blast.dest = pos < 0 ? 0 : getSnapperYPosision(pos);
                break;
            case Left:
                pos = --blast.destI;
                blast.dest = pos < 0 ? 0 : getSnapperXPosision(pos);
                break;
        }
    }


    private int syncCollideSnappers(){
        blastsToKill.clear();
        newBlasts.clear();
        Blast blast;
        int blasts;
        for (int i=0; i<activeBlasts.size(); i++){
            blast = activeBlasts.get(i);
            if (Snappers.isValidSnapper(blast.destI, blast.destJ))
                if (touchSnapper2(blast.destI, blast.destJ))
                    blastsToKill.add(blast);
                else
                    setNextBlastDestination(blast);
        }

        killBlasts();

        blasts = newBlasts.size();

        return blasts;
    }
    
    public int advance2(float deltaTime){
        int i, size;
        Blast blast;

        for (i = 0, size = activeBlasts.size(); i < size; i++) {
            blast = activeBlasts.get(i);
            if (advanceBlast(blast, deltaTime) && !Snappers.isValidSnapper(blast.destI, blast.destJ))
                blastsToKill.add(blast);
        }

        killBlasts();

        startNewBlasts();

        timeToSyncCheck -= deltaTime;

        if (timeToSyncCheck < 0){
            timeToSyncCheck = 2/GRAIN_SPEED_MARGIN_PER_SECOND;
            return syncCollideSnappers();
        }

        return 0;
    }

    public void startNewBlasts(){
        Blast blast;
        for (int i=0; i<newBlasts.size(); i++){
            blast = newBlasts.get(i);
            activeBlasts.add(blast);
        }
        newBlasts.clear();
    }

    private void killBlasts(){
        Blast blast;
        int i, size;

        for (i = 0, size = blastsToKill.size(); i < size; i++) {
            blast = blastsToKill.get(i);
            activeBlasts.remove(blast);
            blastPool.free(blast);
        }
        blastsToKill.clear();
    }

    private boolean advanceBlast(Blast blast, float deltaTime) {
        blast.age += deltaTime;
        switch (blast.direction) {
            case Up:
                blast.y += grainSpeedY * deltaTime;
                return blast.y >= blast.dest;
            case Right:
                blast.x += grainSpeedX * deltaTime;
                return blast.x >= blast.dest;
            case Down:
                blast.y -= grainSpeedY * deltaTime;
                return blast.y <= blast.dest;
            case Left:
                blast.x -= grainSpeedX * deltaTime;
                return blast.x <= blast.dest;
        }
        return false;//should never happen
    }

    public boolean isGameOver(){
        return (tapRemains<1 || snappers.snappersCount == 0) && activeBlasts.size() == 0 ;
    }

    public boolean isGameLost(){
        return snappers.snappersCount>0;
    }

}
