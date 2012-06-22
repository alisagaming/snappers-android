package com.emerginggames.snappers.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.emerginggames.snappers.Metrics;

import com.emerginggames.snappers.R;
import com.emerginggames.snappers.SoundManager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.05.12
 * Time: 16:41
 */
public class GameDialog extends Dialog {
    OnDialogEventListener btnClickListener;
    int itemSpacing;
    boolean isTwoButtonsARow;
    int buttons = 0;
    LinearLayout lastRow;
    int width;

    public GameDialog(Context context) {
        super(context, R.style.GameDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game);
        findViewById(R.id.close_btn).setOnClickListener(closeBtnListener);
    }

    public GameDialog(Context context, int width) {
        this(context);
        this.width = width;
    }

    public void setBtnClickListener(OnDialogEventListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    public void setTwoButtonsARow(boolean twoButtonsARow) {
        isTwoButtonsARow = twoButtonsARow;
    }

    public void addOkButton(){
        addButton(R.drawable.button_ok, 0.4f);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)findViewById(R.id.buttonCont).getLayoutParams();
        lp.topMargin = width / 20;
        findViewById(R.id.buttonCont).setLayoutParams(lp);
    }

    public void setTypeface(Typeface typeface){
        ((OutlinedTextView)findViewById(R.id.title)).setTypeface(typeface);
        ((OutlinedTextView)findViewById(R.id.message)).setTypeface(typeface);
    }

    public void clear(){
        ((LinearLayout)findViewById(R.id.buttonCont)).removeAllViews();
        findViewById(R.id.title).setVisibility(View.GONE);
        findViewById(R.id.message).setVisibility(View.GONE);
        buttons = 0;
    }

    public void addButton(int idUnpressed, int idPressed){
        TwoStateButton btn = new TwoStateButton(getContext());
        btn.setup(idUnpressed, idPressed, clickListener);
        btn.setAdjustViewBounds(true);
        btn.setTag(idUnpressed);

        addButton(btn);
    }

    public ImageView addButton(int drawable_id){
        ImageView btn = new ImageView(getContext());
        btn.setImageResource(drawable_id);
        btn.setOnClickListener(clickListener);
        btn.setAdjustViewBounds(true);
        btn.setTag(drawable_id);

        addButton(btn);
        return btn;
    }

    public ImageView addButton(int drawableId, boolean enabled){
        ImageView btn = addButton(drawableId);
        btn.setEnabled(enabled);
        return btn;
    }



    public void addButton(int drawable_id, float widthShare){
        ImageView btn = new ImageView(getContext());
        btn.setImageResource(drawable_id);
        btn.setOnClickListener(clickListener);
        btn.setAdjustViewBounds(true);
        btn.setTag(drawable_id);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(widthShare * width), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, itemSpacing, 0, 0);
        ((LinearLayout)findViewById(R.id.buttonCont)).addView(btn, lp);

    }

    void addButton(View btn){
        if (isTwoButtonsARow){
            if (buttons %2 == 0 ){
                lastRow = new LinearLayout(getContext());
                lastRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((LinearLayout)findViewById(R.id.buttonCont)).addView(lastRow, llp);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, itemSpacing, itemSpacing, 0);
            lp.weight = 1;
            lastRow.addView(btn, lp);
        }
        else{
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, itemSpacing, 0, 0);
            ((LinearLayout)findViewById(R.id.buttonCont)).addView(btn, lp);
        }
        buttons ++;

    }

    public void setWidth(int width){
        this.width = width;
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setTitle(int msg){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.title);
        t.setText2(msg);
        t.setVisibility(View.VISIBLE);
        //t.setMaxLines2(1);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
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

    public void setMessage(CharSequence message, int[] lineEnds){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.message);
        t.setText2(message);
        t.setVisibility(View.VISIBLE);

        if (lineEnds != null) {
            t.setMaxLines2(lineEnds.length);
            t.setLineEnds(lineEnds);
        }
        t.setTextSizeToFit(true);
    }

    protected View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (btnClickListener!= null)
                btnClickListener.onButtonClick((Integer)v.getTag());
            SoundManager.playButtonSoundIfPossible();
            if ((Integer)v.getTag() == R.drawable.button_ok)
                hide();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (btnClickListener!= null)
            btnClickListener.onCancel();
    }

    View.OnClickListener closeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SoundManager.playButtonSoundIfPossible();
            cancel();
            if (btnClickListener!= null)
                btnClickListener.onCancel();
        }
    };

    public interface OnDialogEventListener {
        void onButtonClick(int unpressedDrawableId);
        void onCancel();
    }


}
