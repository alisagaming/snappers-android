package ru.emerginggames.snappers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.ImageDrawInfo;
import ru.emerginggames.snappers.model.ImagePaginatorParam;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;
import ru.emerginggames.snappers.view.FixedRatioPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 8:40
 */
public class SelectPackActivity extends PaginatedSelectorActivity  implements IOnItemSelectedListener{
    private static final int LOCKED = -1;
    private static final int COMING_SOON = -2;
    private static final int NOTHING_CHANGED = 0;
    private static final int INVISIBLE_PACK_UNLOCKED = -1;
    private static final int VISIBLE_PACK_UNLOCKED = -2;

    LevelPack[] levelPacks;
    RotatedImagePagerAdapter adapter;
    boolean[] levelPackStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int overlap = getWindowManager().getDefaultDisplay().getWidth()/4;

        setPagerAdapter();
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        pager.setPageMargin(- overlap);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentChildOnTop(true);


        com.viewpagerindicator.CirclePageIndicator
                mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }

    @Override
    public void onItemSelected(int number) {
        if (number == COMING_SOON)
            return;

        LevelPack pack = levelPacks[number];
        if (pack == null)
            return;
        
        if (!UserPreferences.getInstance(this).isPackUnlocked(pack)){
            showPackLockedMessage(pack);
            return;
        }

        Intent intent = new Intent(this, SelectLevelActivity.class);
        intent.putExtra(SelectLevelActivity.LEVEL_PACK_TAG, levelPacks[number]);
        startActivity(intent);
    }

    List<ImagePaginatorParam> getPaginatorParamList(){
        levelPacks = LevelPackTable.getAll(this);
        List<ImagePaginatorParam> params = new ArrayList<ImagePaginatorParam>(8);
        UserPreferences settings = UserPreferences.getInstance(this);
        levelPackStatus = new boolean[levelPacks.length];

        for (int i=0; i< levelPacks.length; i++){
            LevelPack pack = levelPacks[i];
            if (settings.isPackUnlocked(pack)){
                params.add(new ImagePaginatorParam(getLevelPackImageIds(pack.id), i));
                levelPackStatus[i] = true;
            }
            else if (!pack.isPremium)
                params.add(new ImagePaginatorParam(getLevelPackImageIds(LOCKED), i));
        }

        params.add(new ImagePaginatorParam(getLevelPackImageIds(COMING_SOON), COMING_SOON));

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
                if (!levelPackStatus[i] && settings.isPackUnlocked(levelPacks[i].id))
                    updatePackCover(i);

        }
    }

    void updatePackCover(int i){
        int id = levelPacks[i].id;
        adapter.changeImages(i, getLevelPackImageIds(id));
        levelPackStatus[i] = true;
    }

    void setPagerAdapter(){
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        adapter = new RotatedImagePagerAdapter(this, getPaginatorParamList(), this);
        pager.setAdapter(adapter);
    }

    ImageDrawInfo[] getLevelPackImageIds(int id){
        switch (id){
            case LOCKED:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.locked, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case COMING_SOON:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.coming, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 1:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack1, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 2:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack2, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 3:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack3, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 4:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack4, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 5:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack5, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 6:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack6, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 7:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.pack7, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 8:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack1, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 9:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack2, true, true)
                        };
            case 10:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack3, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
            case 11:
                return new ImageDrawInfo[]{
                        new ImageDrawInfo(R.drawable.premium_pack4, false, false),
                        new ImageDrawInfo(R.drawable.shadow_pack, true, true)};
        }

        return null;
    }

    protected void showPackLockedMessage(LevelPack pack){
        String message = getResources().getString(R.string.level_locked, pack.id-1, pack.id);

        showMessageDialog(message, new int[]{18, 41, 0, 0}, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageDialog();
            }
        });
    }
}