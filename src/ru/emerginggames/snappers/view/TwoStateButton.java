package ru.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.05.12
 * Time: 16:31
 */
public class TwoStateButton extends Button {
    public TwoStateButton(Context context) {
        super(context);
    }

    public TwoStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoStateButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setStates(int idUnpressed, int idPressed){
        Drawable b1 = getResources().getDrawable(idUnpressed);
        Drawable b2 = getResources().getDrawable(idPressed);

        StateListDrawable dr = new StateListDrawable();
        dr.addState(new int[]{-android.R.attr.state_pressed}, b1);
        dr.addState(new int[]{android.R.attr.state_pressed}, b2);
        setBackgroundDrawable(dr);
    }

    public void setup(int idUnpressed, int idPressed, OnClickListener onClickListener){
        setStates(idUnpressed, idPressed);
        setOnClickListener(onClickListener);
    }
}
