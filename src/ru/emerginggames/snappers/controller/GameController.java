package ru.emerginggames.snappers.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.Resources;
import ru.emerginggames.snappers.model.Blast;
import ru.emerginggames.snappers.model.ILogicListener;
import ru.emerginggames.snappers.model.Level;
import ru.emerginggames.snappers.model.Snappers;
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;
import ru.emerginggames.snappers.view.BlastView;
import ru.emerginggames.snappers.view.SnapperView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 14.03.12
 * Time: 23:04
 */
public class GameController implements ILogicListener{
    public E3Scene scene;
    public Layer layer;
    public GameLogic logic;
    public SnapperView[][] snapperViews;
    private List<SnapperView> snappersTouched;
    private int width;
    private int height;
    private OutlinedTextSprite levelText;
    private OutlinedTextSprite tapsRemainingText;
    private IGameOverListener gameOverListener;
    private Context context;

    public GameController(IGameOverListener gameOverListener, Context context) {
        this.gameOverListener = gameOverListener;
        this.context = context;
        logic = new GameLogic(this);
    }

    public void setScene(E3Scene scene, int width, int height){
        logic.setScreen(width, height, defineGameRect(width, height));
        this.scene = scene;
        this.width = width;
        this.height = height;

        layer = new Layer();
        scene.addLayer(layer);

        snapperViews = new SnapperView[Snappers.WIDTH][Snappers.HEIGHT];
        for (int i=0;i<Snappers.WIDTH; i++)
            for (int j=0;j<Snappers.HEIGHT; j++){
                snapperViews[i][j] = new SnapperView(i, j, 0, this);
                snapperViews[i][j].addToScene(scene, layer);
            }

        snappersTouched = new ArrayList<SnapperView>(4);

        levelText = new OutlinedTextSprite("", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);
        tapsRemainingText = new OutlinedTextSprite("", Metrics.fontSize, Color.WHITE, Color.BLACK, Color.TRANSPARENT, Resources.font, context);
        levelText.move(Metrics.screenMargin, Metrics.screenMargin);
        tapsRemainingText.move(Metrics.screenMargin, Metrics.screenMargin + levelText.getTextHeight());
        layer.add(levelText);
        layer.add(tapsRemainingText);
    }

    public Rect defineGameRect(int width, int height){
        int marginTop = Math.round(Metrics.squareButtonSize * 1.1f);
        int snapperAreaHeight = height - 2 * marginTop;
        int marginBottom = Math.round(Metrics.squareButtonSize * 0.7f);;
        if (snapperAreaHeight < width){
            snapperAreaHeight = width;
            marginBottom = height - marginTop - marginBottom;
            if (marginBottom<0){
                snapperAreaHeight+= marginBottom;
                marginBottom = 0;
            }
        }

        return new Rect(0, height - marginBottom, width, marginTop);
    }

    public void launchLevel(Level level){
        logic.startLevel(level);

        for (int i=0;i<Snappers.WIDTH; i++)
            for (int j=0;j<Snappers.HEIGHT; j++)
                snapperViews[i][j].setState(logic.snappers.getSnapper(i,j));
        
        levelText.setText(String.format("Level: %d-%d", level.packNumber, level.number));
        updateTapsLeft();
    }

    private void updateTapsLeft(){
        tapsRemainingText.setText(String.format("Taps left: %d", logic.tapRemains));
    }

    public void restartLevel(){
        launchLevel(logic.level);
    }

    public void tapSnapper(SnapperView snapperView){
        if (snapperView.state>0 && logic.tapRemains>0){
            snappersTouched.add(snapperView);
            logic.tapRemains--;
            updateTapsLeft();
        }
    }

    public void update(long elapsedMsec){
        synchronized (this){
            logic.advance2(elapsedMsec/1000.0f);
            for (int i=0; i< logic.activeBlasts.size(); i++)
                logic.activeBlasts.get(i).view.advance();

            for (int i=0; i<snappersTouched.size(); i++){
                SnapperView snapperView = snappersTouched.get(i);
                logic.touchSnapper2(snapperView.i, snapperView.j);
            }
            snappersTouched.clear();

            if (logic.isGameOver()){
                if (logic.isGameLost())
                    gameOverListener.gameLost();
                else
                    gameOverListener.gameWon();
            }
        }
    }

    public Level getLevel(){
        return logic.level;
    }
    
    public int getScore(){
        return logic.level.complexity*100;
    }

    @Override
    public void snapperTouched(int i, int j) {
        snapperViews[i][j].tap();
    }

    @Override
    public void blastCreated(Blast blast) {
        //blast.view = new BlastView(blast, this);
        //blast.view.addToLayer(layer);
    }

    @Override
    public void blastLaunched(Blast blast) {
        blast.view = new BlastView(blast, this);
        blast.view.addToLayer(layer);
        blast.view.show();
    }

    @Override
    public void blastRemoved(Blast blast) {
        //blast.view.hide();
        blast.view.removeFromLayer(layer);
    }


}
