package ru.emerginggames.snappers.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.R;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.05.12
 * Time: 18:02
 */
public class NewLevelDialog extends GameDialog {

    public NewLevelDialog(Context context, int width) {
        super(context);
        setWidth(width);

        int imgWidth = width * 8 / 10;
        int imgHeight = width * 17/100;

        findViewById(R.id.close_btn).setVisibility(View.GONE);

        ImageView title = new ImageView(getContext());
        title.setImageResource(R.drawable.new_level_title);
        title.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ((RelativeLayout)findViewById(R.id.root)).addView(title, lp);

        addOkButton();
    }

    public void setLevel(int level){
        setMessage(getContext().getResources().getString(R.string.new_level, level), Metrics.fontSize);
    }


}
