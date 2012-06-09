package ru.emerginggames.snappers.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import ru.emerginggames.snappers.*;
import ru.emerginggames.snappers.gdx.Resources;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 30.05.12
 * Time: 3:39
 */
public class SettingsDialog extends Dialog {
    int width;

    public SettingsDialog(Context context, int width) {
        super(context, R.style.GameDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);
        setWidth(width);
        int padding = width / 31;

        UserPreferences prefs = UserPreferences.getInstance(getContext());

        Typeface font = Resources.getFont(getContext());

        OutlinedTextView otv = (OutlinedTextView)findViewById(R.id.title);

        otv.setTypeface(font);
        otv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);

        otv = (OutlinedTextView)findViewById(R.id.loginFbBtn);
        otv.setTypeface(font);
        otv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
        otv.setOnClickListener(loginClickListener);

        setupRow(padding, font, R.id.iconUser, R.id.loginFbText, R.id.loginFbBtn);
        setupRow(padding, font, R.id.iconFb, R.id.shareFbText, R.id.shareToFbCheckbox);
        setupRow(padding, font, R.id.iconSound, R.id.soundText, R.id.soundCheckbox);
        setupRow(padding, font, R.id.iconMusic, R.id.musicText, R.id.musicCheckbox);

        ((CheckBox)findViewById(R.id.soundCheckbox)).setChecked(prefs.getSound());
        ((CheckBox)findViewById(R.id.musicCheckbox)).setChecked(prefs.getMusic());

        findViewById(R.id.backButton).setOnClickListener(backClickListener);
        findViewById(R.id.soundCheckbox).setOnClickListener(soundClickListener);
        findViewById(R.id.musicCheckbox).setOnClickListener(musicClickListener);
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

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

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


}
