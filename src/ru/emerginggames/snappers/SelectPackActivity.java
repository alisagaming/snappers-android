package ru.emerginggames.snappers;

import android.app.AlertDialog;
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

    LevelPack[] levelPacks;

    RotatedImagePagerAdapter adapter;
    AlertDialog dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RotatedImagePagerAdapter(this, getPaginatorParamList(), this);

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
        if (number == COMING_SOON)
            return;

        LevelPack pack = levelPacks[number];
        if (pack == null)
            return;
        
        if (!GameSettings.getInstance(this).isPackUnlocked(pack)){
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
        GameSettings settings = GameSettings.getInstance(this);

        for (int i=0; i< levelPacks.length; i++){
            LevelPack pack = levelPacks[i];
            if (settings.isPackUnlocked(pack))
                params.add(new ImagePaginatorParam(getLevelPackImageIds(pack.id), i));
            else if (!pack.isPremium)
                params.add(new ImagePaginatorParam(getLevelPackImageIds(LOCKED), i));
        }

        params.add(new ImagePaginatorParam(getLevelPackImageIds(COMING_SOON), COMING_SOON));

        return params;
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

/*        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout =  (LinearLayout)inflater.inflate(R.layout.unlock_dialog, null);
        String message = getResources().getString(R.string.level_locked, pack.id, pack.id+1);
        OutlinedTextView msgText = (OutlinedTextView)layout.findViewById(R.id.message);
        msgText.setText(message);
        msgText.setTypeface(Resources.getFont(this));
        int textSize = getWindowManager().getDefaultDisplay().getWidth()/10;
        msgText.setStroke(Color.BLACK, 2);
        msgText.setTextSize(textSize);
        
        layout.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        layout.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);
        dialog = builder.create();
        dialog.show();
*/
    }    
}