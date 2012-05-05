package ru.emerginggames.snappers.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.SoundManager;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.05.12
 * Time: 16:41
 */
public class GameDialog extends Dialog {
    OnButtonClickListener btnClickListener;
    int itemSpacing;

    public GameDialog(Context context) {
        super(context, R.style.GameDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_game);
    }

    public void setBtnClickListener(OnButtonClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    public void clear(){
        ((LinearLayout)findViewById(R.id.buttonCont)).removeAllViews();
        findViewById(R.id.title).setVisibility(View.GONE);
        findViewById(R.id.message).setVisibility(View.GONE);
    }

    public void addButton(int idUnpressed, int idPressed){
        TwoStateButton btn = new TwoStateButton(getContext());
        btn.setup(idUnpressed, idPressed, clickListener);
        btn.setAdjustViewBounds(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, itemSpacing, 0, 0);
        btn.setTag(idUnpressed);
        ((LinearLayout)findViewById(R.id.buttonCont)).addView(btn, lp);
    }

    public void setWidth(int width){
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setTitle(int msg){
        OutlinedTextView t = (OutlinedTextView)findViewById(R.id.title);
        t.setText(msg);
        t.setVisibility(View.VISIBLE);
        t.setMaxLines2(1);
    }

    public void setMessage(int messageId, int size){
        TextView t = (TextView)findViewById(R.id.message);
        t.setText(messageId);
        t.setTextSize(size);
        t.setVisibility(View.VISIBLE);
    }

    public void setMessage(CharSequence message, int size){
        TextView t = (TextView)findViewById(R.id.message);
        t.setText(message);
        t.setTextSize(size);
        t.setVisibility(View.VISIBLE);
    }

    final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (btnClickListener!= null)
                btnClickListener.onButtonClick((Integer)v.getTag());
            SoundManager.PlayButtonSoundIfPossible();
        }
    };

    public interface OnButtonClickListener {
        void onButtonClick(int unpressedDrawableId);
    }
}
