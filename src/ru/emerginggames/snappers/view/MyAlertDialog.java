package ru.emerginggames.snappers.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.05.12
 * Time: 13:06
 */
public class MyAlertDialog extends Dialog {
    public MyAlertDialog(Context context) {
        super(context, R.style.RCAlertDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert);
        setCancelable(true);

        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        OutlinedTextView messageText = (OutlinedTextView)findViewById(R.id.message);
        messageText.setTypeface(Resources.getFont(getContext()));
        messageText.setTextSize(width/12);
        messageText.setLineSpacing(0, 1.2f);
        getWindow().setLayout( width * 8/10, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setMessage(String msg, int[] lineEnds) {
        OutlinedTextView msgText = (OutlinedTextView) findViewById(R.id.message);
        msgText.setText2(msg);
        if (lineEnds != null) {
            msgText.setMaxLines2(lineEnds.length);
            msgText.setLineEnds(lineEnds);
        }
        msgText.setTextSizeToFit(true);
    }

    public void setLeftButton(int backId,  View.OnClickListener listener){
        setButton(R.id.leftButton, backId, listener);
    }

    public void setRightButton(int backId,  View.OnClickListener listener){
        setButton(R.id.rightButton, backId, listener);
    }

    void setButton(int id, int drawableBack, View.OnClickListener listener) {
        if (drawableBack == 0 || listener == null)
            return;
        View v = findViewById(id);
        v.setBackgroundResource(drawableBack);
        v.setVisibility(View.VISIBLE);
        v.setOnClickListener(listener);
        setSpacerVisibility();
    }

    void setSpacerVisibility(){
        if (findViewById(R.id.leftButton).getVisibility() != View.VISIBLE || findViewById(R.id.leftButton).getVisibility() != View.VISIBLE)
            findViewById(R.id.spacer).setVisibility(View.GONE);
    }
}
