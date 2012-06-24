package com.emrg.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.R;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.05.12
 * Time: 18:02
 */
public class NewLevelDialog extends GameDialog {

    public NewLevelDialog(Context context, int width) {
        super(context);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setWidth(width);

        int imgWidth = width * 8 / 11;
        int imgHeight = width * 17/110;

        findViewById(R.id.close_btn).setVisibility(View.GONE);

        ImageView title = new ImageView(getContext());
        title.setImageResource(R.drawable.new_level_title);
        title.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ((RelativeLayout)findViewById(R.id.root)).addView(title, lp);

        addOkButton();

        lp = (RelativeLayout.LayoutParams)findViewById(R.id.dialog).getLayoutParams();
        lp.topMargin = imgHeight / 2 - 30;
    }

    public void setLevel(int level){
        setMessage(getContext().getString(R.string.new_level, level), Metrics.fontSize);
    }


}
