package com.emerginggames.snappers;

import android.content.Intent;
import android.os.Bundle;
import com.emrg.view.FixedRatioPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.emerginggames.snappers.data.LevelPackTable;
import com.emerginggames.snappers.model.ImagePaginatorParam;
import com.emerginggames.snappers.model.LevelPack;
import com.emrg.view.IOnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 8:40
 */
public class SelectPackActivity extends PaginatedSelectorActivity  implements IOnItemSelectedListener{
    private static final int NOTHING_CHANGED = 0;
    private static final int INVISIBLE_PACK_UNLOCKED = -1;
    private static final int VISIBLE_PACK_UNLOCKED = -2;

    LevelPack[] levelPacks;
    ImagePagerAdapter adapter;
    boolean[] levelPackStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int overlap = getWindowManager().getDefaultDisplay().getWidth()/3;

        setPagerAdapter();
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        pager.setPageMargin(-overlap);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentChildOnTop(true);

        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

    @Override
    public void onItemSelected(int number) {
        SoundManager.getInstance(this).playButtonSound();
        if (number == ImagePaginatorParam.COMING_SOON)
            return;

        LevelPack pack = levelPacks[number];
        if (pack == null)
            return;
        
        if (!UserPreferences.getInstance(this).isPackUnlocked(pack)){
            showPackLockedMessage(pack);
            return;
        }

        ((SnappersApplication)getApplication()).setSwitchingActivities();
        Intent intent = new Intent(this, SelectLevelActivity.class);
        intent.putExtra(SelectLevelActivity.LEVEL_PACK_TAG, levelPacks[number]);
        startActivity(intent);
    }

    List<ImagePaginatorParam> getPaginatorParamList(){
        levelPacks = LevelPackTable.getAll(this);
        List<ImagePaginatorParam> params = new ArrayList<ImagePaginatorParam>(7);
        UserPreferences settings = UserPreferences.getInstance(this);
        levelPackStatus = new boolean[levelPacks.length];

        for (int i=0; i< levelPacks.length; i++){
            LevelPack pack = levelPacks[i];
            if (settings.isPackUnlocked(pack)){
                params.add(new ImagePaginatorParam((int)pack.id, i));
                levelPackStatus[i] = true;
            } else if (!pack.isPremium)
                params.add(new ImagePaginatorParam(ImagePaginatorParam.LOCKED, i));
        }

        params.add(new ImagePaginatorParam(ImagePaginatorParam.COMING_SOON, ImagePaginatorParam.COMING_SOON));
        return params;
    }
    
    int checkPackUnlocked(){
        UserPreferences settings = UserPreferences.getInstance(this);
        for (int i=0; i< levelPacks.length; i++){
            LevelPack pack = levelPacks[i];
            if (settings.isPackUnlocked(pack) && !levelPackStatus[i]){
                if (pack.isPremium)
                    return INVISIBLE_PACK_UNLOCKED;
                else return VISIBLE_PACK_UNLOCKED;
            }
        }
        return NOTHING_CHANGED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        int checkResult = checkPackUnlocked();
        if (checkResult ==INVISIBLE_PACK_UNLOCKED){
            setPagerAdapter();
            return;
        }
        if (checkResult == VISIBLE_PACK_UNLOCKED){
            UserPreferences settings = UserPreferences.getInstance(this);
            for (int i=0; i<levelPacks.length; i++)
                if (!levelPackStatus[i] && settings.isPackUnlocked((int)levelPacks[i].id))
                    updatePackCover(i);
        }
    }

    void updatePackCover(int i){
        int id = (int)levelPacks[i].id;
        adapter.changeImages(i, ImagePaginatorParam.getLevelImageIds(id));
        levelPackStatus[i] = true;
    }

    void updatePackCoverById(int id){
        int i;
        for (i=0; i<levelPacks.length; i++)
            if (levelPacks[i].id == id){
                adapter.changeImages(i, ImagePaginatorParam.getLevelImageIds(id));
                levelPackStatus[i] = true;
                return;
            }
    }

    void setPagerAdapter(){
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        int width = Math.round(getWindowManager().getDefaultDisplay().getWidth() * 0.55f);
        adapter = new ImagePagerAdapter(this, getPaginatorParamList(), this);
        adapter.setImageWidth(width);
        pager.setAdapter(adapter);
    }

    protected void showPackLockedMessage(final LevelPack pack){
        LevelPack prevPack = LevelPackTable.get((int)pack.id-1, this);
        String message = getString(R.string.level_locked, prevPack.title);
        showMessageDialog(message, new int[]{18, 38});
    }

    void buyLevelPack(LevelPack pack){
        if (Settings.DEBUG){
            UserPreferences.getInstance(this).unlockLevelPack(pack);
            updatePackCoverById((int)pack.id);
        }
    }
}