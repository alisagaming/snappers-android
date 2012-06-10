package ru.emerginggames.snappers;

import android.content.Intent;
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
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.view.*;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 15:01
 */
public class PaginatedSelectorActivity extends FragmentActivity {
    GameDialog dlg;
    ScoreCounter scoreCounter;
    BuyHintsDialog buyHintsDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_selector);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        int defPadding = width/40;

        findViewById(R.id.backButton).setPadding(defPadding * 2, 0, 0, defPadding * 2);
        
        findViewById(R.id.indicator).setPadding(defPadding, defPadding, defPadding, 0);

        Rect wndRect = new Rect();
        findViewById(R.id.root).getWindowVisibleDisplayFrame(wndRect);

        if (wndRect.width()>0)
            Metrics.setSize(wndRect.width(), wndRect.height(), this);

        setupScore();
        setupHints();

        SoundManager.getInstance(this).setUp();
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

    @Override
    protected void onResume() {
        super.onResume();
        ((SnappersApplication)getApplication()).activityResumed(this);


        OutlinedTextView hintBtn = (OutlinedTextView)findViewById(R.id.hintBtn);
        hintBtn.setText2(Integer.toString(UserPreferences.getInstance(this).getHintsRemaining()));

        scoreCounter.setScore(UserPreferences.getInstance(this).getScore());

        setupBackPosition();
    }

    void setupBackPosition(){
        ImageView back = (ImageView)findViewById(R.id.bgImage);
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
            m.setScale(scale, scale);
            m.postTranslate(-(imgW * scale - r.width())/2, 0);
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

    public void onBackButtonClick(View v){
        SoundManager.getInstance(this).playButtonSound();
        finish();
    }

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

    View.OnClickListener hintsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buyHintsDialog == null)
                buyHintsDialog = new BuyHintsDialog(PaginatedSelectorActivity.this, Metrics.screenWidth * 95 / 100);
            buyHintsDialog.show();
        }
    };
}