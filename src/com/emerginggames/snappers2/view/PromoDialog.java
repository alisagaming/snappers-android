package com.emerginggames.snappers2.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.emerginggames.snappers2.Metrics;
import com.emerginggames.snappers2.R;
import com.emerginggames.snappers2.SoundManager;
import com.emerginggames.snappers2.UserPreferences;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.transport.FacebookTransport;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 30.06.12
 * Time: 1:08
 */
public class PromoDialog extends Dialog {

    int width;
    FacebookTransport facebookTransport;
    GameDialog messageDialog;

    public PromoDialog(Context context) {
        super(context, R.style.GameDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_promo);
        findViewById(R.id.close_btn).setOnClickListener(closeBtnListener);
        width = Metrics.screenWidth * 95 / 100;
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

        setupControls();
    }

    void setupControls(){
        Typeface font = Resources.getFont(getContext());

        OutlinedTextView enterCode = (OutlinedTextView) findViewById(R.id.message);
        enterCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(Metrics.fontSize * 1.5f ));
        enterCode.setTypeface(font);

        EditText editPromo = (EditText)findViewById(R.id.editCode);
        editPromo.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
        editPromo.setTypeface(font);

        ImageView okBtn = (ImageView)findViewById(R.id.okBtn);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)okBtn.getLayoutParams();
        lp.width = (int)(width * 0.4f);
        okBtn.setOnClickListener(okBtnListener);

        OutlinedTextView yourCode = (OutlinedTextView)findViewById(R.id.yourCode);
        yourCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(Metrics.fontSize * 1.3f));
        yourCode.setTypeface(font);
        String code = UserPreferences.getInstance(getContext()).getPromoCode();
        yourCode.setText(getContext().getResources().getString(R.string.yourCodeIs, code));

        OutlinedTextView wait = (OutlinedTextView)findViewById(R.id.wait);
        wait.setTypeface(font);
        wait.setTextSize(Metrics.fontSize);

        View spinner = findViewById(R.id.progress);

        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        spinner.startAnimation(rotation);
    }

    View.OnClickListener closeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SoundManager.playButtonSoundIfPossible();
            cancel();
        }
    };

    View.OnClickListener okBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String code = ((EditText)findViewById(R.id.editCode)).getText().toString();
            String myCode = UserPreferences.getInstance(getContext()).getPromoCode();

            if (code.equals(myCode)){
                showMessageDialog(getContext().getString(R.string.cantUseOwnCode));
                return;
            }

            RelativeLayout shade = (RelativeLayout)findViewById(R.id.shade);
            LinearLayout dialog = (LinearLayout)findViewById(R.id.dialog);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)shade.getLayoutParams();
            lp.height = dialog.getHeight();
            shade.setLayoutParams(lp);
            shade.setVisibility(View.VISIBLE);

            if (facebookTransport == null)
                facebookTransport = new FacebookTransport(getOwnerActivity());


            facebookTransport.checkPromo(code, senderListener);
        }
    };

    FacebookTransport.ResponseListener senderListener = new FacebookTransport.ResponseListener(){
        @Override
        public void onOk(Object data) {
            findViewById(R.id.shade).setVisibility(View.INVISIBLE);
            int amount = (Integer)data;
            UserPreferences.getInstance(getContext()).addHints(amount);
            if (amount == 1)
                showMessageDialog(getContext().getString(R.string.youVeGotHint));
            else
                showMessageDialog(getContext().getString(R.string.youVeGotHints, amount));
        }

        @Override
        public void onError(Throwable e) {
            findViewById(R.id.shade).setVisibility(View.INVISIBLE);
            String shortMessage = e.getMessage();
            if (shortMessage.equals(getContext().getString(R.string.noSuchCode)))
                showMessageDialog(getContext().getString(R.string.noSuchCodeLong));
            else
                showMessageDialog(e.getMessage());
        }
    };


    void showMessageDialog(String message){
        if (messageDialog == null){
            messageDialog = new GameDialog(getContext(), width);
            messageDialog.addOkButton();
        }

        messageDialog.setMessage(message, Metrics.fontSize);
        messageDialog.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (messageDialog != null)
            messageDialog.dismiss();
    }
}
