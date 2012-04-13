package ru.emerginggames.snappers.logic;

import android.graphics.Rect;
import ru.emerginggames.snappers.Settings;
import ru.emerginggames.snappers.model.Blast;
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
    public boolean hintUsed = false;

    private float timeToSyncCheck;
    private float syncTime;
    int snapperTouchedI = -1;
    int snapperTouchedJ = -1;
    public long startTime;

    public GameLogic(ILogicListener listener) {

        PoolObjectFactory<Blast> grainFactory = new PoolObjectFactory<Blast>() {
            @Override
            public Blast createObject() {
                return new Blast();
            }
        };
        blastPool = new Pool<Blast>(grainFactory, MAX_BLASTS);
        activeBlasts = new ArrayList<Blast>(MAX_BLASTS);
        blastsToKill = new ArrayList<Blast>(MAX_BLASTS);
        newBlasts = new ArrayList<Blast>(MAX_BLASTS);
        snappers = new Snappers();

        snapperListener = listener;
        syncTime = 2/GRAIN_SPEED_MARGIN_PER_SECOND;
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
        hintUsed = false;
        startTime = System.currentTimeMillis();
    }

    public void tapSnapper(int i, int j){
        if (snapperTouchedI>=0 || tapRemains<1)
            return;
        tapRemains--;
        snapperListener.tap();
        snapperTouchedI = i;
        snapperTouchedJ = j;
    }

    public boolean hitSnapper(int i, int j) {//return true if consumed touch
        int touchResult = snappers.touchSnapper(i, j);
        if (touchResult <0)
            return false;
        snapperListener.snapperHit(i, j);
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
        if (blast.direction == Blast.Direction.Up || blast.direction ==Blast.Direction.Down)
            blast.source = y;
        else blast.source = x;
        return blast;
    }

    private void setNextBlastDestination(Blast blast) {
        int pos;
        switch (blast.direction) {
            case Up:
                pos = ++blast.destJ;
                blast.source = blast.y;
                blast.dest = pos >= Snappers.HEIGHT ? height : getSnapperYPosision(pos);
                break;
            case Right:
                pos = ++blast.destI;
                blast.source = blast.x;
                blast.dest = pos >= Snappers.WIDTH ? width : getSnapperXPosision(pos);
                break;
            case Down:
                blast.source = blast.y;
                pos = --blast.destJ;
                blast.dest = pos < 0 ? 0 : getSnapperYPosision(pos);
                break;
            case Left:
                pos = --blast.destI;
                blast.source = blast.x;
                blast.dest = pos < 0 ? 0 : getSnapperXPosision(pos);
                break;
        }
    }


    private int syncCollideSnappers(){
        Blast blast;
        int blasts=0;
        for (int i=0; i<activeBlasts.size(); i++){
            blast = activeBlasts.get(i);
            if (Snappers.isValidSnapper(blast.destI, blast.destJ))
                if (hitSnapper(blast.destI, blast.destJ)){
                    blastsToKill.add(blast);
                    blasts++;
                }
                else
                    setNextBlastDestination(blast);
        }

        killBlasts();
        return blasts;
    }
    
    public int advance2(float deltaTime){
        int i, size, res=0;
        Blast blast;

        for (i = 0, size = activeBlasts.size(); i < size; i++) {
            blast = activeBlasts.get(i);
            if (advanceBlast(blast, deltaTime) && !Snappers.isValidSnapper(blast.destI, blast.destJ))
                blastsToKill.add(blast);
        }

        killBlasts();

        if (snapperTouchedI >=0) {
            hitSnapper(snapperTouchedI, snapperTouchedJ);
            snapperTouchedI = snapperTouchedJ = -1;
            res=1;
        }

        timeToSyncCheck -= deltaTime;

        if (timeToSyncCheck < 0){
            timeToSyncCheck = syncTime;
            res += syncCollideSnappers();
        }

        startNewBlasts();

        return res;
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
        return (tapRemains<1 && activeBlasts.size() == 0) || snappers.snappersCount == 0;
    }

    public boolean isGameLost(){
        return snappers.snappersCount>0;
    }

    public int getScore(boolean isSolvedBefore){
        int score = level.tapsCount * 100 + (Snappers.countSnappers(level.zappers) - level.tapsCount) * 90;
        score *= getMult();
        if (hintUsed)
            score = Math.round(score * Settings.HINTED_MULT);
        if (isSolvedBefore)
            score = Math.round(score * Settings.REPEAT_MULT);

        return score;
    }
    
    private int getMult(){
        if (level.complexity > 300)
            return 5;
        else if (level.complexity > 100)
            return 3;
        else if (level.complexity > 30)
            return 2;
        else
            return 1;
    }

}