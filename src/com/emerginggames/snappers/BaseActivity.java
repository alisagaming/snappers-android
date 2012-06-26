package com.emerginggames.snappers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.emerginggames.snappers.gdx.Resources;
import com.emrg.view.*;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 25.06.12
 * Time: 21:44
 */
public class BaseActivity extends FragmentActivity {
    ScoreCounter scoreCounter;
    BuyHintsDialog buyHintsDialog;
    GameDialog dlg;
    int defPadding;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        Metrics.setSizeByView(getWindow().getDecorView(), getApplicationContext());

        int width = getWindowManager().getDefaultDisplay().getWidth();
        defPadding = width/40;
        SoundManager.getInstance(this).setUp();

    }

    void setupElements(){
        setupScore();
        setupHints();
        View v = findViewById(R.id.backButton);
        if (v != null){
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)v.getLayoutParams();
            lp.width = lp.height = Metrics.screenWidth / 5;
            lp.leftMargin = lp.bottomMargin = Metrics.screenWidth /20;
        }
    }

    void setupBackPosition(){
        ImageView back = (ImageView)findViewById(R.id.bgImage);
        if (back == null)
            return;
        Drawable bgImage = back.getDrawable();
        Rect r = new Rect();
        back.getWindowVisibleDisplayFrame(r);
        if (bgImage instanceof BitmapDrawable)
        {
            int imgW, imgH;
            imgW = bgImage.getIntrinsicWidth();
            imgH = bgImage.getIntrinsicHeight();
            float scale = (float)r.height()/imgH ;

            Matrix m = new Matrix();
            float xShift = -(imgW * scale - r.width())/2;
            if (xShift > 0)
                m.setScale((float)r.width()/imgW, scale);
            else {
                m.setScale(scale, scale);
                m.postTranslate(xShift, 0);
            }
            back.setImageMatrix(m);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ((SnappersApplication)getApplication()).setSwitchingActivities();
        ((SnappersApplication)getApplication()).activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((SnappersApplication)getApplication()).activityResumed(this);

        OutlinedTextView hintBtn = (OutlinedTextView)findViewById(R.id.hintBtn);
        hintBtn.setText2(Integer.toString(UserPreferences.getInstance(this).getHintsRemaining()));

        scoreCounter.setScore(UserPreferences.getInstance(this).getScore());

        setupBackPosition();
    }

    public void onBackButtonClick(View v){
        SoundManager.getInstance(this).playButtonSound();
        ((SnappersApplication)getApplication()).setSwitchingActivities();
        finish();
    }

    void setupHints(){
        OutlinedTextView hintBtn = (OutlinedTextView)findViewById(R.id.hintBtn);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)hintBtn.getLayoutParams();
        int bgWidth = hintBtn.getBackground().getIntrinsicWidth();
        int bgHeight = hintBtn.getBackground().getIntrinsicHeight();

        lp.height = Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale);
        float scale = ((float)lp.height / bgHeight );
        lp.width = Math.round(bgWidth * scale);
        lp.rightMargin = lp.topMargin = Metrics.screenMargin;

        int[] iPaddings = {27, 23, 94, 23};
        hintBtn.setPadding((int)(iPaddings[0] * scale), (int)(iPaddings[1] * scale), (int)(iPaddings[2] * scale), (int)(iPaddings[3] * scale));
        hintBtn.setLayoutParams(lp);
        hintBtn.setTypeface(Resources.getFont(getApplicationContext()));
        hintBtn.setMaxLines2(1);
        hintBtn.requestLayout();

        hintBtn.setOnClickListener(hintsClickListener);
    }

    void setupScore(){
        RelativeLayout root = (RelativeLayout)findViewById(R.id.root);
        int size = (int)(Metrics.squareButtonSize * Metrics.squareButtonScale);
        scoreCounter = new ScoreCounter(this, size * 32 / 10, size);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.leftMargin = lp.topMargin = Metrics.screenMargin;
        root.addView(scoreCounter.getView(), lp);
    }


    View.OnClickListener hintsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buyHintsDialog == null)
                buyHintsDialog = new BuyHintsDialog(BaseActivity.this, Metrics.screenWidth * 95 / 100);
            buyHintsDialog.show();
        }
    };

    public void showMessageDialog(String message, int[] lineEnds){
        if (dlg == null){
            dlg = new GameDialog(this, Metrics.screenWidth * 95 / 100);
            dlg.addOkButton();
        }

        dlg.setMessage(message, Metrics.fontSize);
        dlg.show();
    }

    public void showMessage(String msg){
        showMessageDialog(msg, null);
    }

    public void hideMessageDialog(){
        dlg.hide();
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.hide();
        progressDialog.dismiss();
        progressDialog = null;
    }
}
