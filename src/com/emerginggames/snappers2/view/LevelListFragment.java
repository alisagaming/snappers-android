package com.emerginggames.snappers2.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.emerginggames.snappers2.UserPreferences;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.Metrics;
import com.emerginggames.snappers2.R;
import com.emerginggames.snappers2.model.LevelPack;
import com.emrg.view.IOnItemSelectedListener;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.03.12
 * Time: 2:09
 */
public class LevelListFragment extends Fragment implements View.OnClickListener {
    private static final int[] bgPaddings = {16,16,16,23};
    private int startFromLevel;
    private IOnItemSelectedListener itemSelectedListener;
    private int maxAvailableLevel;
    LevelPack pack;
    SparseArray<OutlinedTextView> items = new SparseArray<OutlinedTextView>(25);

    int innerPaddingLeft = 0;
    int innerPaddingTop = 0;
    int innerPaddingRight = 0;
    int innerPaddingBottom = 0;

    int backId;

    public LevelListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pack == null){
            getActivity().finish();
            return;
        }
        int oldMaxAvailLevel = maxAvailableLevel;
        maxAvailableLevel = UserPreferences.getInstance(getActivity()).getLevelUnlocked(pack);
        if (maxAvailableLevel != oldMaxAvailLevel){
            if (oldMaxAvailLevel < startFromLevel + 25
                    && maxAvailableLevel >= startFromLevel)
                for (int i=startFromLevel; i<startFromLevel + 25; i++)
                    setItemState(items.get(i), i);
        }
    }

    public void setInnerPaddings(int innerPaddingLeft, int innerPaddingTop, int innerPaddingRight, int innerPaddingBottom) {
        this.innerPaddingLeft = innerPaddingLeft;
        this.innerPaddingTop = innerPaddingTop;
        this.innerPaddingRight = innerPaddingRight;
        this.innerPaddingBottom = innerPaddingBottom;
    }

    public LevelListFragment(int startFromLevel, LevelPack pack1, IOnItemSelectedListener itemSelectedListener) {
        this.startFromLevel = startFromLevel;
        this.itemSelectedListener = itemSelectedListener;
        pack = pack1;
        maxAvailableLevel = UserPreferences.getInstance(getActivity()).getLevelUnlocked(pack);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        backId = getResources().getIdentifier(pack.levelIcon, "drawable", getActivity().getPackageName());
        if (backId == 0)
            backId = R.drawable.level1;


        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layout.setLayoutParams(params);
        layout.setPadding(innerPaddingLeft, innerPaddingTop, innerPaddingRight, innerPaddingBottom);

        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        int num = startFromLevel;

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1f);
        switch (Metrics.sizeMode){
            case modeS:
                itemParams.setMargins(2,2,2,2);
                break;
            case modeM:
                itemParams.setMargins(4,4,4,4);
                break;
            case modeL:
                itemParams.setMargins(6,6,6,6);
                break;
        }

        for (int i=0; i<5; i++){
            LinearLayout layoutRow = new LinearLayout(getActivity());
            layoutRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 0, 1f));
            layoutRow.setOrientation(LinearLayout.HORIZONTAL);

            for (int j=0;j<5; j++){
                OutlinedTextView text = new OutlinedTextView(getActivity());
                text.setLayoutParams(itemParams);
                //text.setPadding(4, 4, 4, 4);
                text.setBackgroundPaddings(bgPaddings);
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
            layout.addView(layoutRow);
        }

        return layout;
    }

    private void setItemState(OutlinedTextView text, int num){
        text.setText(Integer.toString(num));
        if (num <= maxAvailableLevel){
            text.setBackgroundResource(backId);
            text.setTextColor(Color.WHITE);
            text.setStroke(Color.BLACK, 2);
            text.setTag(num);
        }
        else {
            text.setBackgroundResource(R.drawable.level_locked);
            text.setTextColor(Color.TRANSPARENT);
            text.setStroke(Color.TRANSPARENT, 2);
        }

    }
    

    @Override
    public void onClick(View view) {
        if (view.getTag() != null && (Integer)view.getTag()<= maxAvailableLevel)
            itemSelectedListener.onItemSelected((Integer)view.getTag());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
