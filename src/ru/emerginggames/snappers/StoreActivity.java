package ru.emerginggames.snappers;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.viewpagerindicator.CirclePageIndicator;
import ru.emerginggames.snappers.data.LevelPackTable;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.model.ImagePaginatorParam;
import ru.emerginggames.snappers.model.LevelPack;
import ru.emerginggames.snappers.utils.GInAppStore;
import ru.emerginggames.snappers.utils.IStoreListener;
import ru.emerginggames.snappers.utils.Store;
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
public class StoreActivity extends PaginatedSelectorActivity implements IOnItemSelectedListener, IStoreListener {
    LevelPack[] levelPacks;
    RotatedImagePagerAdapter adapter;
    Store mGStore;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int overlap = getWindowManager().getDefaultDisplay().getWidth() / 4;

        setPagerAdapter();
        FixedRatioPager pager = (FixedRatioPager) findViewById(R.id.pager);
        pager.setPageMargin(-overlap);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentChildOnTop(true);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        findViewById(R.id.shopButton).setVisibility(View.GONE);
        if (Settings.GoogleInAppEnabled) {
            mGStore = GInAppStore.getInstance(getApplicationContext());
            mGStore.setListener(this, new Handler());
        } else
            onBillingSupported(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mGStore != null)
            mGStore.unSetListener();
    }

    List<ImagePaginatorParam> getPaginatorParamList() {
        levelPacks = LevelPackTable.getAllByPremium(this, true);
        List<ImagePaginatorParam> params = new ArrayList<ImagePaginatorParam>(7);
        UserPreferences settings = UserPreferences.getInstance(this);

        for (int i = 0; i < levelPacks.length; i++) {
            LevelPack pack = levelPacks[i];
            if (!settings.isPackUnlocked(pack))
                params.add(new ImagePaginatorParam(pack.id, i));
        }

        params.add(new ImagePaginatorParam(ImagePaginatorParam.HINT_PACK, ImagePaginatorParam.HINT_PACK));
        if (!UserPreferences.getInstance(this).isAdFree())
            params.add(new ImagePaginatorParam(ImagePaginatorParam.ADFREE, ImagePaginatorParam.ADFREE));
        return params;
    }

    void setPagerAdapter() {
        FixedRatioPager pager = (FixedRatioPager) findViewById(R.id.pager);
        adapter = new RotatedImagePagerAdapter(this, getPaginatorParamList(), this);
        pager.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(int number) {
        SoundManager.getInstance(this).playButtonSound();
        if (number >= 0 && number < levelPacks.length) {
            buyLevelPack(levelPacks[number]);
        }
        if (number == ImagePaginatorParam.ADFREE)
            buyAdFree();
        if (number == ImagePaginatorParam.HINT_PACK)
            buyHints();
    }

    void buyLevelPack(LevelPack pack) {
        if (mGStore != null)
            mGStore.buy(Goods.getGoodsByLevelPack(pack));
    }

    void buyHints() {
        if (mGStore != null)
        if (Settings.DEBUG_BUY)
            mGStore.itemBought(Goods.HintPack10, 1);
        else
            mGStore.buy(Goods.HintPack10);
    }

    void buyAdFree() {
        if (mGStore != null){
            if (Settings.DEBUG_BUY)
                mGStore.itemBought(Goods.AdFree, 1);
            else
                mGStore.buy(Goods.AdFree);
        }
    }

    @Override
    public void onItemBought(Goods item) {
        setPagerAdapter();
    }

    @Override
    public void onItemRefunded(Goods item) {
        setPagerAdapter();
    }

    @Override
    public void onBillingSupported(boolean supported) {
        if (!supported)
            showMessage("No in-app billing supported");
    }
}