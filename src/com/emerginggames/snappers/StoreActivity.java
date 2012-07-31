package com.emerginggames.snappers;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.emerginggames.snappers.model.Goods;
import com.emerginggames.snappers.model.ImagePaginatorParam;
import com.emerginggames.snappers.utils.AmazonStore;
import com.emerginggames.snappers.utils.GInAppStore;
import com.emerginggames.snappers.utils.Store;
import com.emerginggames.snappers.view.FixedRatioPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.emerginggames.snappers.data.LevelPackTable;
import com.emerginggames.snappers.model.LevelPack;
import com.emerginggames.snappers.utils.IStoreListener;
import com.emerginggames.snappers.view.IOnItemSelectedListener;

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
    Store mStore;

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
        //findViewById(R.id.shopButton).setVisibility(View.GONE);
        findViewById(R.id.moreGamesButton).setVisibility(View.GONE);
        findViewById(R.id.storeButton).setVisibility(View.GONE);

        if (Settings.BILLING_SUPPORTED) {
            if (Settings.IS_AMAZON)
                mStore = AmazonStore.getInstance(getApplicationContext());
            else
                mStore = GInAppStore.getInstance(getApplicationContext());
            mStore.setListener(this, new Handler());
        } else
            onBillingSupported(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mStore != null)
            mStore.unSetListener();
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
        if (mStore != null)
            mStore.buy(Goods.getGoodsByLevelPack(pack));
    }

    void buyHints() {
        if (mStore != null)
        if (Settings.DEBUG_BUY)
            mStore.itemBought(Goods.HintPack10, 1);
        else
            mStore.buy(Goods.HintPack10);
    }

    void buyAdFree() {
        if (mStore != null){
            if (Settings.DEBUG_BUY)
                mStore.itemBought(Goods.AdFree, 1);
            else
                mStore.buy(Goods.AdFree);
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