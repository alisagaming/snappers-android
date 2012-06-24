package com.emerginggames.snappers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.transport.FacebookTransport;
import com.emrg.view.OutlinedTextView;
import com.emerginggames.snappers.model.FacebookFriend;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 17:34
 */
public class FacebookActivity extends Activity {
    int wndWidth;
    FacebookTransport facebookTransport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_facebook);

        Metrics.setSizeByView(getWindow().getDecorView(), getApplicationContext());
        wndWidth = Metrics.screenWidth;

        facebookTransport = new FacebookTransport(this);
        if (!facebookTransport.isLoggedIn()){
            facebookTransport.login(new FacebookTransport.ResponseListener(){
                @Override
                public void onOk(Object data) {
                    getFriends();
                    facebookTransport.sync(null);

                }

                @Override
                public void onError(Throwable e) {
                    finish();
                }
            });
        }
        else {
            getFriends();
            facebookTransport.sync(null);
            facebookTransport.getName(null);
            facebookTransport.extendAccessTokenIfNeeded();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebookTransport.getFB().authorizeCallback(requestCode, resultCode, data);
    }

    public void getFriends(){
        facebookTransport.getFriends(new FacebookTransport.ResponseListener() {
            @Override
            public void onOk(Object data) {
                showFriendsList((FacebookFriend[]) data);
            }

            @Override
            public void onError(Throwable e) {
                play();
            }
        });
    }

    void showFriendsList(final FacebookFriend[] friends) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getLayoutInflater();
                Typeface font = Resources.getFont(FacebookActivity.this);
                int starWidth = wndWidth * 56 /640;
                LinearLayout table = (LinearLayout)findViewById(R.id.table);
                for (int i = 1; i < friends.length; i++) {
                    FacebookFriend friend = friends[i];
                    LinearLayout row = (LinearLayout) inflater.inflate(R.layout.partial_friend_row, null);
                    TextView title = (TextView) row.findViewById(R.id.title);
                    title.setText(friend.first_name);
                    title.setTypeface(font);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

                    TextView score = (TextView) row.findViewById(R.id.score);
                    if (friend.installed)
                        score.setText(Integer.toString(friend.xp_count));
                    else
                        score.setText(R.string.notPlaying);
                    score.setTypeface(font);
                    score.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

                    OutlinedTextView button = (OutlinedTextView) row.findViewById(R.id.btn);
                    button.setText(friend.installed ? R.string.gift : R.string.invite);
                    button.setTypeface(font);

                    if (friend.installed) {
                        TextView star = (TextView) row.findViewById(R.id.level);
                        star.setText(Integer.toString(Settings.getLevel(friend.xp_count)));
                        star.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
                        star.setTypeface(font);
                        star.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams lp = star.getLayoutParams();
                        lp.height = lp.width = starWidth;
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    table.addView(row, lp);
                }
            }
        });

    }

    void play() {
        startActivity(new Intent(this, SelectPackActivity.class));
    }

    public void onBackButtonClick(View v){
        finish();
    }


}