package ru.emerginggames.snappers;

import android.os.Bundle;
import android.view.View;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.ImagePaginatorParam;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.view.FixedRatioPager;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 4:30
 */
public class StoreActivity extends PaginatedSelectorActivity implements IOnItemSelectedListener {
    LevelPack[] levelPacks;
    RotatedImagePagerAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int overlap = getWindowManager().getDefaultDisplay().getWidth()/4;

        setPagerAdapter();
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        pager.setPageMargin(- overlap);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentChildOnTop(true);

        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        
        findViewById(R.id.shopButton).setVisibility(View.GONE);
    }

    List<ImagePaginatorParam> getPaginatorParamList(){
        levelPacks = LevelPackTable.getAllByPremium(this, true);
        List<ImagePaginatorParam> params = new ArrayList<ImagePaginatorParam>(7);
        UserPreferences settings = UserPreferences.getInstance(this);

        for (int i=0; i< levelPacks.length; i++){
            LevelPack pack = levelPacks[i];
            if (!settings.isPackUnlocked(pack))
                params.add(new ImagePaginatorParam(pack.id, i));
        }

        params.add(new ImagePaginatorParam(ImagePaginatorParam.HINT_PACK, ImagePaginatorParam.HINT_PACK));
        if (!UserPreferences.getInstance(this).isAdFree())
            params.add(new ImagePaginatorParam(ImagePaginatorParam.ADFREE, ImagePaginatorParam.ADFREE));
        return params;
    }

    void setPagerAdapter(){
        FixedRatioPager pager = (FixedRatioPager)findViewById(R.id.pager);
        adapter = new RotatedImagePagerAdapter(this, getPaginatorParamList(), this);
        pager.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(int number) {
        SoundManager.getInstance(this).playButtonSound();
        if (number >=0 && number < levelPacks.length){
            showPackLockedMessage(levelPacks[number]);
        }
        if (number == ImagePaginatorParam.ADFREE)
            buyAdFree();
        if (number == ImagePaginatorParam.HINT_PACK)
            buyHints();
    }

    protected void showPackLockedMessage(final LevelPack pack){
        String message = getResources().getString(R.string.level_locked, pack.id-1, pack.id);

        showMessageDialog(message, new int[]{18, 41, 0, 0}, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMessageDialog();
                        buyLevelPack(pack);
                    }
                }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessageDialog();
            }
        });
    }

    void buyLevelPack(LevelPack pack){
        UserPreferences.getInstance(this).unlockLevelPack(pack);
        setPagerAdapter();
    }

    void buyHints(){
        UserPreferences.getInstance(this).addHints(10);
        showMessage("bought it!");
    }

    void buyAdFree(){
        UserPreferences.getInstance(this).setAdFree(true);
        setPagerAdapter();
        showMessage("bought it!");
    }

}