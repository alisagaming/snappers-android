package ru.emerginggames.snappers.gdx.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 06.04.12
 * Time: 12:46
 */
public class MenuStage extends DimBackStage{
    int menuWidth;
    int menuHeight;
    int menuX;
    int menuY;

    public MenuStage(float width, float height, boolean stretch, SpriteBatch batch) {
        super(width, height, stretch, batch);
    }

    public void setMenuSize(int width, int height){
        this.menuWidth = width;
        this.menuHeight = height;
        calcMenuPosition();
    }

    @Override
    public void setViewport(float width, float height) {
        super.setViewport(width, height);
        calcMenuPosition();
    }

    @Override
    public void draw() {
        drawBack();
        float opacity = getOpacity();
        batch.begin();
        batch.setColor(1, 1, 1, opacity);
        Resources.dialog9.draw(batch, menuX, menuY, menuWidth, menuHeight);
        root.draw(batch, opacity                                                                                                                                                                                                                );
        batch.end();
    }

    private void calcMenuPosition(){
        menuX = Math.round((width - menuWidth)/2);
        menuY = Math.round((height - menuHeight)/2);
    }
    
    public int getMenuBottom(){
        return menuY + Metrics.menuMargin;
    }
    
    public int getMenuTop(){
        return menuY + menuHeight - Metrics.menuMargin;
    }

    public void setInnerMenuHeight(int innerHeight){
        setMenuSize(menuWidth, innerHeight + 2 * Metrics.menuMargin);
    }
    
    public int getInnerWidth(){
        return menuWidth - Metrics.menuMargin * 2;
    }
    
    public int getInnerHeight(){
        return menuHeight - Metrics.menuMargin * 2;
    }
}
