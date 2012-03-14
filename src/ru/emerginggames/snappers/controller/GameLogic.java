package ru.emerginggames.snappers.controller;

import com.e3roid.E3Scene;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;
import ru.emerginggames.snappers.view.BlastView;
import ru.emerginggames.snappers.view.SnapperView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.03.12
 * Time: 0:09
 */
public class GameLogic {
    private static final int MAX_GRAINS = 90;
    private static final float GRAIN_SPEED_MARGIN_PER_SECOND = 4;
    private final Pool<Blast> grainPool;
    public final List<Blast> activeBlasts;
    //public final List<Blast> grainsToKill;
    public final Snappers snappers;
    public int width;
    public int height;
    public float xSnapperMargin;
    public float ySnapperMargin;
    private float grainSpeedX;
    private float grainSpeedY;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    public Level level;
    public int tapRemains;
    ILogicListener logicListener;

    public GameLogic(int width, int height, int minX, int maxX, int minY, int maxY, ILogicListener listener) {
        this.width = width;
        this.height = height;

        PoolObjectFactory<Blast> grainFactory = new PoolObjectFactory<Blast>(){
            @Override
            public Blast createObject() {
                Blast blast = new Blast();
                GameLogic.this.logicListener.blastCreated(blast);
                return new Blast();
            }
        };
        grainPool = new Pool<Blast>(grainFactory, MAX_GRAINS);
        activeBlasts = new ArrayList<Blast>(MAX_GRAINS);
        //grainsToKill = new ArrayList<Blast>(MAX_GRAINS);
        snappers = new Snappers();

        xSnapperMargin = width/2.0f/Snappers.WIDTH;
        ySnapperMargin = height/2.0f/Snappers.HEIGHT;
        grainSpeedX = xSnapperMargin * GRAIN_SPEED_MARGIN_PER_SECOND;
        grainSpeedY = ySnapperMargin * GRAIN_SPEED_MARGIN_PER_SECOND;
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        logicListener = listener;
    }
    
    public void startLevel(Level level){
        for (Blast blast : activeBlasts)
            grainPool.free(blast);
        activeBlasts.clear();
        snappers.setSnappers(level.zappers);
        tapRemains = level.tapsCount;
        this.level = level;
        //System.gc();
    }
    
    public boolean blastHitCell(Blast blast){
        blast.x = blast.destX;
        blast.y = blast.destY;

        if (!Snappers.isValidSnapper(blast.destI, blast.destJ)){
            activeBlasts.remove(blast);
            grainPool.free(blast);
            return true;
        }
        else if( touchSnapper(blast.destI, blast.destJ) ){

            activeBlasts.remove(blast);
            grainPool.free(blast);
            return true;
        }
        else{
            setNextBlastDestination(blast);
            return false;
        }
    }

/*    public int advance(float deltaTime){
        int pops = 0;
        Blast blast;
        int i, size;
        for (i=0, size = activeBlasts.size(); i<size; i++){
            blast = activeBlasts.get(i);
            if (advanceGrain(blast, deltaTime)){//get in only if dest reached
                if (!Snappers.isValidSnapper(blast.destI, blast.destJ))
                    grainsToKill.add(blast);
                else if( touchSnapper(blast.destI, blast.destJ) ){
                    pops++;
                    grainsToKill.add(blast);
                }
                else
                    setNextBlastDestination(blast);
            }
        }

        for (i=0, size = grainsToKill.size(); i< size; i++){
            blast = grainsToKill.get(i);
            activeBlasts.remove(blast);
            grainPool.free(blast);
        }
        grainsToKill.clear();

        return pops;
    }*/

    public boolean touchSnapper(int i, int j){//return true if consumed touch
        int touchResult = snappers.touchSnapper(i, j);
        logicListener.snapperTouched(i, j);
        if (touchResult == 0){
            int xPos = getSnapperXPosision(i);
            int yPos = getSnapperYPosision(j);
            launchBlast(xPos, yPos, Blast.Direction.Up, i, j);
            launchBlast(xPos, yPos, Blast.Direction.Right, i, j);
            launchBlast(xPos, yPos, Blast.Direction.Down, i, j);
            launchBlast(xPos, yPos, Blast.Direction.Left, i, j);
            return true;
        }
        return touchResult > 0;
    }

    public int getSnapperXPosision(int i){
        return Math.round(xSnapperMargin * (2 * i + 1));
    }

    public int getSnapperYPosision(int j){
        return Math.round(ySnapperMargin * (2 * j + 1));
    }
    
/*    private boolean advanceGrain(Blast blast, float deltaTime){
        switch (blast.direction){
            case Up:
                blast.y -= grainSpeedY * deltaTime;
                return blast.y <= blast.dest;
            case Right:
                blast.x += grainSpeedX * deltaTime;
                return blast.x >= blast.dest;
            case Down:
                blast.y += grainSpeedY * deltaTime;
                return blast.y >= blast.dest;
            case Left:
                blast.x -= grainSpeedX * deltaTime;
                return blast.x <= blast.dest;
        }
        return false;//should never happen
    }*/

    private Blast launchBlast(int x, int y, Blast.Direction direction, int i, int j){
        Blast blast = grainPool.newObject();
        blast.x = x;
        blast.y = y;
        blast.direction = direction;
        blast.destI = i;
        blast.destJ = j;
        blast.destX = x;
        blast.destY = y;
        setNextBlastDestination(blast);
        activeBlasts.add(blast);
        logicListener.blastLaunched(blast);
        return blast;
    }
    
    private void setNextBlastDestination(Blast blast){
        int pos;
        switch (blast.direction){
            case Up:
                pos = blast.destJ--;
                blast.dest = pos < 0 ? minY : getSnapperYPosision(pos);
                blast.destY = blast.dest;
                break;
            case Right:
                pos = blast.destI++;
                blast.dest = pos > Snappers.WIDTH ? maxX : getSnapperXPosision(pos);
                blast.destX = blast.dest;
                break;
            case Down:
                pos = blast.destJ++;
                blast.dest = pos > Snappers.HEIGHT ? maxY : getSnapperYPosision(pos);
                blast.destY = blast.dest;
                break;
            case Left:
                pos = blast.destI++;
                blast.dest = pos < 0 ? minX : getSnapperXPosision(pos);
                blast.destX = blast.dest;
                break;
        }

    }
}
