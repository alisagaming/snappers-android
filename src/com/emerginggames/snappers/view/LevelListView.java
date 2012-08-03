package com.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.R;
import com.emerginggames.snappers.UserPreferences;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.LevelPack;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 03.08.12
 * Time: 9:42
 * To change this template use File | Settings | File Templates.
 */
public class LevelListView extends LinearLayout implements View.OnClickListener {
    private int startFromLevel;
    private IOnItemSelectedListener itemSelectedListener;
    private int maxAvailableLevel;
    LevelPack pack;
    SparseArray<OutlinedTextView> items = new SparseArray<OutlinedTextView>(25);


    public LevelListView(Context context, int startFromLevel, LevelPack pack1, IOnItemSelectedListener itemSelectedListener) {
        super(context);
        this.startFromLevel = startFromLevel;
        this.itemSelectedListener = itemSelectedListener;
        pack = pack1;
        maxAvailableLevel = UserPreferences.getInstance(context).getLevelUnlocked(pack);
        createContent();
    }

    void createContent(){

        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        int num = startFromLevel;

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1f);
        if (Metrics.sizeMode == Metrics.SizeMode.modeS)
            itemParams.setMargins(2,2,2,2);
        else if (Metrics.sizeMode == Metrics.SizeMode.modeM)
            itemParams.setMargins(4,4,4,4);
        else
            itemParams.setMargins(6,6,6,6);


        for (int i=0; i<5; i++){
            LinearLayout layoutRow = new LinearLayout(getContext());
            layoutRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 0, 1f));
            layoutRow.setOrientation(LinearLayout.HORIZONTAL);

            for (int j=0;j<5; j++){
                OutlinedTextView text = new OutlinedTextView(getContext());
                text.setLayoutParams(itemParams);
                //text.setPadding(4, 4, 4, 4);
                text.setGravity(Gravity.CENTER);
                setItemState(text, num);
                text.setOnClickListener(this);
                text.setTextSizeToFit(true);
                text.setSquare(true);
                text.setMaxLines2(1);
                text.setHorizontallyScrolling(false);
                if (Resources.font != null)
                    text.setTypeface(Resources.font);
                layoutRow.addView(text);

                items.put(num, text);
                num++;
            }
            addView(layoutRow);
        }
    }

    private void setItemState(OutlinedTextView text, int num){
        text.setText(Integer.toString(num));
        if (num <= maxAvailableLevel){
            text.setBackgroundResource(R.drawable.level);
            text.setTextColor(Color.WHITE);
            text.setStroke(Color.BLACK, 2);
            text.setTag(num);
        }
        else {
            text.setBackgroundResource(R.drawable.level_lock);
            text.setTextColor(Color.TRANSPARENT);
            text.setStroke(Color.TRANSPARENT, 2);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() != null && (Integer)view.getTag()<= maxAvailableLevel)
            itemSelectedListener.onItemSelected((Integer)view.getTag());
    }

    public void refresh(){
        int oldMaxAvailLevel = maxAvailableLevel;
        maxAvailableLevel = UserPreferences.getInstance(getContext()).getLevelUnlocked(pack);
        if (maxAvailableLevel != oldMaxAvailLevel){
            if (oldMaxAvailLevel < startFromLevel + 25
                    && maxAvailableLevel >= startFromLevel)
                for (int i=startFromLevel; i<startFromLevel + 25; i++)
                    setItemState(items.get(i), i);
        }
    }


}
