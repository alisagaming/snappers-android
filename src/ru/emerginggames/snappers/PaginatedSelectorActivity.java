package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 15:01
 */
public class PaginatedSelectorActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_selector);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();

        OutlinedTextView scoreView = (OutlinedTextView)findViewById(R.id.score);
        scoreView.setStroke(Color.BLACK, 2);
        scoreView.setTypeface(Resources.getFont(this));
        scoreView.setTextSize(width/10);
        scoreView.setPadding(0, width/40, width/40, -width/40);

        findViewById(R.id.root).setPadding(0, 0 , 0, height/40);

        findViewById(R.id.footer).setPadding(0, height/40, 0, 0);
        
        OutlinedTextView messageText = (OutlinedTextView)findViewById(R.id.message);
        messageText.setStroke(Color.BLACK, 2);
        messageText.setTypeface(Resources.getFont(this));
        messageText.setTextSize(width/12);
        messageText.setLineSpacing(0, 1.2f);

        View dialog = findViewById(R.id.dialog);
        ViewGroup.LayoutParams lp = dialog.getLayoutParams();
        lp.width = width * 8/10;
        dialog.setLayoutParams(lp);
    }

    public void onBackButtonClick(View v){
        finish();
    }

    public void onStoreButtonClick(View v){
        startActivity(new Intent(this, StoreActivity.class));
    }
    
    public void showMessageDialog(String message, int[] lineEnds, View.OnClickListener leftListener, View.OnClickListener rightListener){

        findViewById(R.id.dialogCont).setVisibility(View.VISIBLE);

        OutlinedTextView msgText = (OutlinedTextView)findViewById(R.id.message);
        msgText.setText(message);
        msgText.setMaxLines2(lineEnds.length);
        msgText.setLineEnds(lineEnds);
        msgText.setTextSizeToFit(true);

        findViewById(R.id.leftButton).setOnClickListener(leftListener);

        findViewById(R.id.rightButton).setOnClickListener(rightListener);


    }

    public void hideMessageDialog(){
        findViewById(R.id.dialogCont).setVisibility(View.GONE);
    }
}