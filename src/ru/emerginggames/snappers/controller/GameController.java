package ru.emerginggames.snappers.controller;

import com.e3roid.E3Scene;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;
import ru.emerginggames.snappers.view.BlastView;
import ru.emerginggames.snappers.view.SnapperView;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 23:04
 */
public class GameController implements ILogicListener{
    public E3Scene scene;
    public GameLogic logic;
    public final SnapperView[][] snapperViews;

    public GameController(E3Scene scene) {
        this.scene = scene;
        int width = scene.getWidth();
        int height = scene.getHeight();
        logic = new GameLogic(width, height, 0, width, 50, height - 100, this);

        snapperViews = new SnapperView[Snappers.WIDTH][Snappers.HEIGHT];
        for (int i=0;i<Snappers.WIDTH; i++)
            for (int j=0;j<Snappers.HEIGHT; j++){
                snapperViews[i][j] = new SnapperView(i, j, 0, this);
                snapperViews[i][j].addToScene(scene);
            }
    }


    public void blastHit(BlastView blastView){
        if (logic.blastHitCell(blastView.blast))
            blastView.hide();
        else
            blastView.flyToNext();
    }

    public void launchLevel(Level level){
        logic.startLevel(level);

        for (int i=0;i<Snappers.WIDTH; i++)
            for (int j=0;j<Snappers.HEIGHT; j++)
                snapperViews[i][j].setState(logic.snappers.getSnapper(i,j));
    }

    public void tapSnapper(SnapperView snapperView){
        if (logic.touchSnapper(snapperView.i, snapperView.j)){
            logic.tapRemains--;
        }
    }

    @Override
    public void snapperTouched(int i, int j) {
        snapperViews[i][j].tap();
    }

    @Override
    public void blastCreated(Blast blast) {
        blast.view = new BlastView(blast, this);
        blast.view.addToScene(scene);
    }

    @Override
    public void blastLaunched(Blast blast) {
        blast.view.show();
        blast.view.flyToNext();
    }
}
