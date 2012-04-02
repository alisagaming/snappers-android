package ru.emerginggames.snappers;

import android.content.Intent;
import android.os.Bundle;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;
import ru.emerginggames.snappers.view.FixedRatioPager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 8:40
 */
public class SelectPackActivity extends PaginatedSelectorActivity  implements IOnItemSelectedListener{
    int imageIds[];
    int shadowIds[];
    
    LevelPack[] levelPacks;
    
    //int[][] imageIds2;
    RotatedImagePagerAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareImageArrays();
        //prepareImageArray();

        adapter = new RotatedImagePagerAdapter(this, imageIds, shadowIds, this);

        int overlap = getWindowManager().getDefaultDisplay().getWidth()/4;

        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setPageMargin(- overlap);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentChildOnTop(true);

        com.viewpagerindicator.CirclePageIndicator
                mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

    @Override
    public void onItemSelected(int number) {
        if (levelPacks[number] == null)
            return;

        Intent intent = new Intent(this, SelectLevelActivity.class);
        intent.putExtra(SelectLevelActivity.LEVEL_PACK_TAG, levelPacks[number]);
        startActivity(intent);
    }

/*    void prepareImageArray(){
        levelPacks = LevelPackTable.getAll(this);
        int[][] ids = new int[levelPacks.length][];
        LevelPack[] selectedPacks = new LevelPack[levelPacks.length];
        
        int i=0;
        for (LevelPack pack: levelPacks){
            if (pack.isUnlocked){
                ids[i] = getLevelPackImageIds(pack);
                selectedPacks[i] = pack;
                i++;
            }
            else if (!pack.isPremium){
                ids[i] = new int[]{R.drawable.locked, R.drawable.shadow_pack};
                selectedPacks[i] = null;
                i++;
            }
        }

        int size = i+1;
        imageIds2 = new int[size][];
        levelPacks = new LevelPack[size];
        for (i=0; i<size-1;i++){
            imageIds2[i] = ids[i];
            levelPacks[i] = selectedPacks[i];
        }
        imageIds2[i] = new int[]{R.drawable.coming, R.drawable.shadow_pack};
        levelPacks[i] = null;
    }*/

    void prepareImageArrays(){
        levelPacks = LevelPackTable.getAll(this);
        int[] ids = new int[levelPacks.length];
        LevelPack[] selectedPacks = new LevelPack[levelPacks.length];

        int i=0;
        for (LevelPack pack: levelPacks){
            if (pack.isUnlocked){
                ids[i] = getLevelPackCoverId(pack);
                selectedPacks[i] = pack;
                i++;
            }
            else if (!pack.isPremium){
                ids[i] = R.drawable.locked;
                selectedPacks[i] = null;
                i++;
            }
        }
        
        int size = i+1;
        imageIds = new int[size];
        shadowIds = new int[size];
        levelPacks = new LevelPack[size];
        for (i=0; i<size-1;i++){
            imageIds[i] = ids[i];
            shadowIds[i] = R.drawable.shadow_pack;
            levelPacks[i] = selectedPacks[i];
        }
        imageIds[i] = R.drawable.coming;
        shadowIds[i] = R.drawable.shadow_pack;
        levelPacks[i] = null;
    }

/*    int[] getLevelPackImageIds(LevelPack pack){
        switch (pack.id){
            case 1:
                return new int[]{R.drawable.pack1, R.drawable.shadow_pack};
            case 2:
                return new int[]{R.drawable.pack2, R.drawable.shadow_pack};
            case 3:
                return new int[]{R.drawable.pack3, R.drawable.shadow_pack};
            case 4:
                return new int[]{R.drawable.pack4, R.drawable.shadow_pack};
            case 5:
                return new int[]{R.drawable.pack5, R.drawable.shadow_pack};
            case 6:
                return new int[]{R.drawable.pack6, R.drawable.shadow_pack};
            case 7:
                return new int[]{R.drawable.pack7, R.drawable.shadow_pack};
            case 8:
                return new int[]{R.drawable.premium_pack1, R.drawable.shadow_pack};
            case 9:
                return new int[]{R.drawable.shadow_pack, R.drawable.premium_pack2};
            case 10:
                return new int[]{R.drawable.premium_pack3, R.drawable.shadow_pack};
            case 11:
                return new int[]{R.drawable.premium_pack4, R.drawable.shadow_pack};
        }

        return null;
    }*/
    
    int getLevelPackCoverId(LevelPack pack){
        switch (pack.id){
            case 1:
                return R.drawable.pack1;
            case 2:
                return R.drawable.pack2;
            case 3:
                return R.drawable.pack3;
            case 4:
                return R.drawable.pack4;
            case 5:
                return R.drawable.pack5;
            case 6:
                return R.drawable.pack6;
            case 7:
                return R.drawable.pack7;
            case 8:
                return R.drawable.premium_pack1;
            case 9:
                return R.drawable.premium_pack2;
            case 10:
                return R.drawable.premium_pack3;
            case 11:
                return R.drawable.premium_pack4;
        }
        return 0;
    }
}