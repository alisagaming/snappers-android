package com.emerginggames.snappers.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.emerginggames.snappers.SoundManager;
import com.emerginggames.snappers.R;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.05.12
 * Time: 16:41
 */
public class GameDialog extends Dialog {
    OnDialogEventListener btnClickListener;
    int itemSpacing;

    public GameDialog(Context context) {
        super(context, R.style.GameDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hide();
                if (btnClickListener!= null)
                    btnClickListener.onCancel();
            }
        });
    }

    public void setBtnClickListener(OnDialogEventListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    public void setTypeface(Typeface typeface){
        ((OutlinedTextView)findViewById(R.id.title)).setTypeface(typeface);
        ((OutlinedTextView)findViewById(R.id.message)).setTypeface(typeface);
    }

    public void clear(){
        ((LinearLayout)findViewById(R.id.buttonCont)).removeAllViews();
        ((LinearLayout)findViewById(R.id.viewCont)).removeAllViews();
        findViewById(R.id.title).setVisibility(View.GONE);
        findViewById(R.id.message).setVisibility(View.GONE);
        findViewById(R.id.viewCont).setVisibility(View.GONE);
    }

    public void addButton(int idUnpressed, int idPressed){
        TwoStateButton btn = new TwoStateButton(getContext());
        btn.setup(idUnpressed, idPressed, clickListener);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, itemSpacing, 0, 0);
        btn.setTag(idUnpressed);
        ((LinearLayout)findViewById(R.id.buttonCont)).addView(btn, lp);
    }

    public void addButton(int idUnpressed, int idPressed, Object tag){
        TwoStateButton btn = new TwoStateButton(getContext());
        btn.setup(idUnpressed, idPressed, clickListener);
        btn.setAdjustViewBounds(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, itemSpacing, 0, 0);
        btn.setTag(idUnpressed);
        if (tag != null)
            btn.setTag(R.id.tagId, tag);
        ((LinearLayout)findViewById(R.id.buttonCont)).addView(btn, lp);
    }

    public void addView(View v){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, itemSpacing, 0, 0);
        ((LinearLayout)findViewById(R.id.viewCont)).addView(v, lp);
        findViewById(R.id.viewCont).setVisibility(View.VISIBLE);
    }

    public void setWidth(int width){
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        int pad = width * 3 / 100;
        findViewById(R.id.buttonCont).setPadding(pad, 0, pad, 0);
    }

    @Override
    public void setTitle(int msg){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.title);
        t.setText2(msg);
        t.setVisibility(View.VISIBLE);
        t.setMaxLines2(1);
        t.setLineEnds(null);
    }

    public void setTitle(int msg, int[] lineEnds){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.title);
        t.setText2(msg);
        t.setVisibility(View.VISIBLE);
        t.setMaxLines2(lineEnds.length);
        t.setLineEnds(lineEnds);
    }

    public void setTitle(int msg, int size){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.title);
        t.setText2(msg);
        t.setVisibility(View.VISIBLE);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        t.setMaxLines2(0);
        t.setLineEnds(null);
    }

    public void setMessage(int messageId, int size){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.message);
        t.setText2(messageId);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        t.setVisibility(View.VISIBLE);
    }

    public void setMessage(CharSequence message, int size){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.message);
        t.setText2(message);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        t.setVisibility(View.VISIBLE);
    }

    final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (btnClickListener!= null)
                btnClickListener.onButtonClick((Integer)v.getTag(), v.getTag(R.id.tagId));
            SoundManager.PlayButtonSoundIfPossible();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hide();
    }

    public interface OnDialogEventListener {
        void onButtonClick(int unpressedDrawableId, Object tag);
        void onCancel();
    }
}
