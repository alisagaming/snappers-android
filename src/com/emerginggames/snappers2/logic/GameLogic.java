package com.emerginggames.snappers2.logic;

import android.graphics.Rect;
import com.emerginggames.snappers2.model.Snappers;
import com.emerginggames.snappers2.Settings;
import com.emerginggames.snappers2.model.Blast;
import com.emerginggames.snappers2.model.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 0:09
 */
public class GameLogic {
    public final Snappers snappers;
    public int width;
    public int height;
    public float xSnapperMargin;
    public float ySnapperMargin;
    private Rect snappersRect;
    public Level level;
    public int tapRemains;
    ILogicListener snapperListener;
    public boolean hintUsed = false;

    int snapperTouchedI = -1;
    int snapperTouchedJ = -1;
    public long startTime;

    Blasts blasts;

    public GameLogic(ILogicListener listener) {
        blasts = new Blasts();
        snappers = new Snappers();
        snapperListener = listener;
    }

    public void setScreen(int width, int height, Rect snappersRect) {
        this.snappersRect = snappersRect;
        this.width = width;
        this.height = height;

        xSnapperMargin = snappersRect.width() / 2.0f / Snappers.WIDTH;
        ySnapperMargin = Math.abs(snappersRect.height() / 2.0f / Snappers.HEIGHT);
        blasts.setSpeed();
    }

    public void startLevel(Level level) {
        blasts.clear();
        snappers.setSnappers(level.zappers);
        tapRemains = level.tapsCount;
        this.level = level;
        hintUsed = false;
        startTime = System.currentTimeMillis();
    }

    public void tapSnapper(int i, int j) {
        if (snapperTouchedI >= 0 || tapRemains < 1)
            return;
        if (!snapperListener.canTap(i, j))
            return;

        tapRemains--;
        snapperListener.tap();
        snapperTouchedI = i;
        snapperTouchedJ = j;
    }

    public boolean hitSnapper(int i, int j) {//return true if consumed touch
        int touchResult = snappers.touchSnapper(i, j);
        if (touchResult < 0)
            return false;
        snapperListener.snapperHit(i, j);
        if (touchResult == 0) {
            blasts.launchBlasts(i, j);
        }
        return true;
    }

    public int getSnapperXPosision(int i) {
        return snappersRect.left + Math.round(xSnapperMargin * (2 * i + 1));
    }

    public int getSnapperYPosision(int j) {
        return snappersRect.bottom + Math.round(ySnapperMargin * (2 * j + 1));
    }

    public int advance2(float deltaTime) {
        int res = 0;

        if (snapperTouchedI >= 0) {
            hitSnapper(snapperTouchedI, snapperTouchedJ);
            snapperTouchedI = snapperTouchedJ = -1;
            res = 1;
        }

        res += blasts.advanceBlasts(deltaTime);

        return res;
    }

    public boolean isGameOver() {
        return (tapRemains < 1 && !blasts.hasActive()) || snappers.snappersCount == 0;
    }

    public boolean isGameLost() {
        return snappers.snappersCount > 0;
    }

    public int getScore(boolean isSolvedBefore) {
        int score = level.tapsCount * 10 + Snappers.countSnappers(level.zappers)  * 90;
        score *= getMult();
        if (hintUsed)
            score = Math.round(score * Settings.HINTED_MULT);
        if (isSolvedBefore)
            score = Math.round(score * Settings.REPEAT_MULT);


        //score *= 10;
        return score;
    }

    private int getMult() {
        if (level.complexity > 300)
            return 5;
        else if (level.complexity > 100)
            return 3;
        else if (level.complexity > 30)
            return 2;
        else
            return 1;
    }

    public List<Blast> getBlasts() {
        return blasts.activeBlasts;
    }

    public int countBlasts(){
        return blasts.activeBlasts.size();
    }

    private class Blasts {
        private static final int MAX_BLASTS = 90;
        private static final float BLAST_SPEED_MARGIN_PER_SECOND = 7;
        public final List<Blast> blastsToKill;
        public final Pool<Blast> blastPool;
        public final List<Blast> activeBlasts;
        public final List<Blast> newBlasts;
        private float grainSpeedX;
        private float grainSpeedY;

        private float syncTime;
        private int syncTimeInt;
        //private float timeToSyncCheck;
        private int SYNC_TIME_INT = 1000000;

        public Blasts() {
            PoolObjectFactory<Blast> blastFactory = new PoolObjectFactory<Blast>() {
                @Override
                public Blast createObject() {
                    return new Blast();
                }
            };
            blastPool = new Pool<Blast>(blastFactory, MAX_BLASTS);
            activeBlasts = new ArrayList<Blast>(MAX_BLASTS);
            blastsToKill = new ArrayList<Blast>(MAX_BLASTS);
            newBlasts = new ArrayList<Blast>(MAX_BLASTS);

            syncTime = 2 / BLAST_SPEED_MARGIN_PER_SECOND;
            syncTimeInt = (int) (syncTime * SYNC_TIME_INT);
        }

        public void setSpeed() {
            grainSpeedX = xSnapperMargin * BLAST_SPEED_MARGIN_PER_SECOND;
            grainSpeedY = ySnapperMargin * BLAST_SPEED_MARGIN_PER_SECOND;
        }

        public void clear() {
            synchronized (activeBlasts) {
                for (Blast blast : activeBlasts)
                    blastPool.free(blast);
                activeBlasts.clear();
            }
        }

        public void launchBlasts(int i, int j) {
            int xPos = getSnapperXPosision(i);
            int yPos = getSnapperYPosision(j);
            launchBlast2(xPos, yPos, Blast.Direction.Down, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Right, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Up, i, j);
            launchBlast2(xPos, yPos, Blast.Direction.Left, i, j);
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
            blast.age = blast.checkAge = 0;
            if (blast.direction == Blast.Direction.Up || blast.direction == Blast.Direction.Down)
                blast.source = y;
            else blast.source = x;
            return blast;
        }

        private void setNextBlastDestination(Blast blast) {
            int pos;
            blast.checkAge = 0;
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

        public int advanceBlasts(float delta) {
            int i, size, res = 0;
            Blast blast;
            int deltaInt = (int) (delta * SYNC_TIME_INT);
            synchronized (activeBlasts) {
                for (i = 0, size = activeBlasts.size(); i < size; i++) {
                    blast = activeBlasts.get(i);
                    if (advanceBlast(blast, delta, deltaInt) && !Snappers.isValidSnapper(blast.destI, blast.destJ))
                        blastsToKill.add(blast);
                    if (checkBlastHit(blast))
                        res++;
                }
            }

            //int res = advanceSync(delta);
            killBlasts();
            startNewBlasts();
            return res;
        }

/*        public int advanceSync(float delta){
            timeToSyncCheck -= delta;
            int res = 0;

            if (timeToSyncCheck < 0){
                timeToSyncCheck = syncTime;
                res = syncCollideSnappers();
            }

            return res;
        }*/

        public boolean checkBlastHit(Blast blast) {
            if (blast.checkAge > syncTimeInt) {
                if (Snappers.isValidSnapper(blast.destI, blast.destJ))
                    if (hitSnapper(blast.destI, blast.destJ)) {
                        blastsToKill.add(blast);
                        return true;
                    } else
                        setNextBlastDestination(blast);
            }
            return false;
        }

/*        private int syncCollideSnappers(){
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
            return blasts;
        }*/

        public void startNewBlasts() {
            Blast blast;
            for (int i = 0; i < newBlasts.size(); i++) {
                blast = newBlasts.get(i);
                activeBlasts.add(blast);
            }
            newBlasts.clear();
        }

        private void killBlasts() {
            Blast blast;
            int i, size;

            for (i = 0, size = blastsToKill.size(); i < size; i++) {
                blast = blastsToKill.get(i);
                activeBlasts.remove(blast);
                blastPool.free(blast);
            }
            blastsToKill.clear();
        }

        private boolean advanceBlast(Blast blast, float deltaTime, int deltaInt) {
            blast.age += deltaTime;
            blast.checkAge += deltaInt;
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

        public boolean hasActive() {
            return activeBlasts.size() > 0;
        }
    }
}
