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
    RotatedImagePagerAdapter adapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int[] imageIds = new int[]{R.drawable.pack1, R.drawable.pack2, R.drawable.pack3,
                                   R.drawable.pack4, R.drawable.pack5, R.drawable.pack6,
                                   R.drawable.pack7};
        int[] shadowIds = new int[]{R.drawable.shadow_pack, R.drawable.shadow_pack, R.drawable.shadow_pack,
                                    R.drawable.shadow_pack, R.drawable.shadow_pack, R.drawable.shadow_pack,
                                    R.drawable.shadow_pack};
        adapter = new RotatedImagePagerAdapter(this, imageIds, shadowIds, this);


        int overlap = getWindowManager().getDefaultDisplay().getWidth()/3;

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
        LevelPack pack =  LevelPackTable.get(number + 1, this);
        Intent intent = new Intent(this, SelectLevelActivity.class);
        intent.putExtra(SelectLevelActivity.LEVEL_PACK_TAG, pack);
        startActivity(intent);
    }
}