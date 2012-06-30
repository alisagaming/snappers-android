package com.emerginggames.snappers2;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.transport.FacebookTransport;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 30.05.12
 * Time: 3:39
 */
public class SettingsDialog extends Dialog {
    int width;
    FacebookTransport facebookTransport;
    Activity context;
    UserPreferences prefs;
    boolean syncDone = false;
    boolean nameDone = false;

    public SettingsDialog(Activity context, int width) {
        super(context, R.style.GameDialog);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);
        setWidth(width);
        int padding = width / 31;
        int scrWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();

        prefs = UserPreferences.getInstance(getContext());

        Typeface font = Resources.getFont(getContext());

        OutlinedTextView otv = (OutlinedTextView)findViewById(R.id.title);

        otv.setTypeface(font);
        otv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);

        otv = (OutlinedTextView)findViewById(R.id.loginFbBtn);
        otv.setTypeface(font);
        otv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
        otv.setOnClickListener(loginClickListener);

        setupRow(padding, font, R.id.iconUser, R.id.loginFbText, R.id.loginBtnCont);
        setupRow(padding, font, R.id.iconFb, R.id.shareFbText, R.id.shareToFbCheckbox);
        setupRow(padding, font, R.id.iconSound, R.id.soundText, R.id.soundCheckbox);
        setupRow(padding, font, R.id.iconMusic, R.id.musicText, R.id.musicCheckbox);

        ((CheckBox)findViewById(R.id.soundCheckbox)).setChecked(prefs.getSound());
        ((CheckBox)findViewById(R.id.musicCheckbox)).setChecked(prefs.getMusic());
        ((CheckBox)findViewById(R.id.shareToFbCheckbox)).setChecked(prefs.getShareToFb());


        findViewById(R.id.soundCheckbox).setOnClickListener(soundClickListener);
        findViewById(R.id.musicCheckbox).setOnClickListener(musicClickListener);
        findViewById(R.id.shareToFbCheckbox).setOnClickListener(shareClickListener);

        findViewById(R.id.backButton).setOnClickListener(backClickListener);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)findViewById(R.id.backButton).getLayoutParams();
        lp.width = lp.height = scrWidth / 5;
        lp.leftMargin -= (scrWidth - width)/2;

        lp = (RelativeLayout.LayoutParams)findViewById(R.id.titleCont).getLayoutParams();
        lp.height = (int)(Metrics.largeFontSize * 1.5f);

        lp = (RelativeLayout.LayoutParams)findViewById(R.id.footerCont).getLayoutParams();
        lp.height = (int)(Metrics.largeFontSize * 1.5f);

        float scale = 1;
        int screenSizeMode = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSizeMode == Configuration.SCREENLAYOUT_SIZE_LARGE){
            scale = 0.8f;
        } else if (Build.VERSION.SDK_INT >= 9 && screenSizeMode == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            scale = 0.65f;
        }

        if (scale != 1 ){
            scaleItem(R.id.shareToFbCheckbox, scale);
            scaleItem(R.id.soundCheckbox, scale);
            scaleItem(R.id.musicCheckbox, scale);

            scaleItem(R.id.iconUser, scale);
            scaleItem(R.id.iconFb, scale);
            scaleItem(R.id.iconSound, scale);
            scaleItem(R.id.iconMusic, scale);
        }
    }

    void setupLoginRow(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoginProgress();
                unblock();
                findViewById(R.id.loginFbBtn).setVisibility(View.VISIBLE);
                if (facebookTransport.isLoggedIn()){
                    String userName = prefs.getFacebookUserName();
                    if (userName != null)
                        ((TextView)findViewById(R.id.loginFbText)).setText(context.getString(R.string.loggedToFb, userName));
                    else
                        ((TextView)findViewById(R.id.loginFbText)).setText(R.string.loggedToFbNoName);
                    ((OutlinedTextView)findViewById(R.id.loginFbBtn)).setText(R.string.logout);
                } else {
                    ((TextView)findViewById(R.id.loginFbText)).setText(R.string.loginToFb);
                    ((OutlinedTextView)findViewById(R.id.loginFbBtn)).setText(R.string.login);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        facebookTransport = new FacebookTransport(context);
        setupLoginRow();
    }

    void scaleItem(int id, float scale){
        LinearLayout.LayoutParams lpl = (LinearLayout.LayoutParams)findViewById(id).getLayoutParams();
        lpl.width = (int)(lpl.width / scale);
        lpl.height = (int)(lpl.height / scale);
    }

    void block(){
        findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        setCancelable(false);
    }

    void unblock(){
        findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        setCancelable(true);
    }

    void setupRow(int padding, Typeface font, int iconId, int textId, int btnId){
        TextView text = (TextView)findViewById(textId);
        text.setTypeface(font);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)findViewById(iconId).getLayoutParams();
        mlp.leftMargin = padding;
        mlp.topMargin = mlp.bottomMargin = padding * 3 /2;

        mlp = (ViewGroup.MarginLayoutParams)findViewById(btnId).getLayoutParams();
        mlp.rightMargin = padding * 3 / 2;

    }

    public void setWidth(int width){
        this.width = width;
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    void showProgress(){
        int size = findViewById(R.id.loginFbBtn).getHeight();
        if (size == 0)
            size = 30;

        ImageView bar = (ImageView)findViewById(R.id.progress);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)bar.getLayoutParams();
        lp.width = lp.height = size;
        bar.setLayoutParams(lp);
        bar.setVisibility(View.VISIBLE);
        bar.setImageResource(R.drawable.spinner_white_48);

        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        bar.startAnimation(rotation);

        findViewById(R.id.loginFbBtn).setVisibility(View.INVISIBLE);
    }

    void hideLoginProgress(){
        View img = findViewById(R.id.progress);
        img.clearAnimation();
        img.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (facebookTransport.isLoggedIn())
                logoff();
            else
                login();
        }
    };

    void login(){
            facebookTransport.login(new FacebookTransport.ResponseListener() {
                @Override
                public void onOk(Object data) {
                    showProgress();
                    getUInfo();
                }

                @Override
                public void onError(Throwable e) {
                    setupLoginRow();
                }
            });
    }

    void getUInfo(){
        facebookTransport.getUserInfo(new FacebookTransport.ResponseListener(){
            @Override
            public void onOk(Object data) {
                sync();
            }

            @Override
            public void onError(Throwable e) {
                setupLoginRow();
            }
        });
    }

    void sync(){
        facebookTransport.sync(new FacebookTransport.ResponseListener(){
            @Override
            public void onOk(Object data) {
                setupLoginRow();
            }

            @Override
            public void onError(Throwable e) {
                setupLoginRow();
            }
        });
    }

    void logoff(){
        showProgress();
        block();
        facebookTransport.logoff(new FacebookTransport.ResponseListener(){
            @Override
            public void onOk(Object data) {
                setupLoginRow();
            }

            @Override
            public void onError(Throwable e) {
                setupLoginRow();
            }
        });
    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
            ((MainScreenActivity)getOwnerActivity()).onSettingsDialogClosed();
            SoundManager.playButtonSoundIfPossible();
        }
    };

    View.OnClickListener soundClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserPreferences.getInstance(getContext()).setSound(((CheckBox) v).isChecked());
            SoundManager.playButtonSoundIfPossible();
        }
    };

    View.OnClickListener musicClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserPreferences.getInstance(getContext()).setMusic(((CheckBox)v).isChecked());
            ((SnappersApplication)getOwnerActivity().getApplication()).musicStatusChanged();

            SoundManager.playButtonSoundIfPossible();
        }
    };

    View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserPreferences.getInstance(getContext()).setShareToFb(((CheckBox) v).isChecked());
            SoundManager.playButtonSoundIfPossible();
        }
    };

}
