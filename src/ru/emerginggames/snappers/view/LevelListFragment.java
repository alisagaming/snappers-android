package ru.emerginggames.snappers.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.LevelPack;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 24.03.12
 * Time: 2:09
 */
public class LevelListFragment extends Fragment implements View.OnClickListener {
    private int startFromLevel;
    private IOnItemSelectedListener itemSelectedListener;
    private int maxAvailableLevel;
    LevelPack pack;
    SparseArray<OutlinedTextView> items = new SparseArray<OutlinedTextView>(25);

    public LevelListFragment() {
    }

    @Override
    public void onResume() {
        if (maxAvailableLevel != pack.levelsUnlocked){
            maxAvailableLevel = pack.levelsUnlocked;
            if (maxAvailableLevel < startFromLevel + 25
                    && pack.levelsUnlocked >= startFromLevel)
                for (int i=startFromLevel; i<startFromLevel + 25; i++)
                    setItemState(items.get(i), i);
        }
        super.onResume();

    }

    public LevelListFragment(int startFromLevel, LevelPack pack1, IOnItemSelectedListener itemSelectedListener) {
        this.startFromLevel = startFromLevel;
        this.itemSelectedListener = itemSelectedListener;
        pack = pack1;
        maxAvailableLevel = pack.levelsUnlocked;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelPaddedLinearLayout layout = new RelPaddedLinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layout.setLayoutParams(params);
        layout.setMultiplier(0.1f);

        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        int num = startFromLevel;

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1f);
        itemParams.setMargins(6,6,6,6);


        for (int i=0; i<5; i++){
            LinearLayout layoutRow = new LinearLayout(getActivity());
            layoutRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 0, 1f));
            layoutRow.setOrientation(LinearLayout.HORIZONTAL);

            for (int j=0;j<5; j++){
                OutlinedTextView text = new OutlinedTextView(getActivity());
                text.setLayoutParams(itemParams);
                text.setPadding(4, 4, 4, 4);
                text.setGravity(Gravity.CENTER);
                setItemState(text, num);
                text.setOnClickListener(this);
                text.setTextSizeToFit(true);
                text.setSquare(true);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
