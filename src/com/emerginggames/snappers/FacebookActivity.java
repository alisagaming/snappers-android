package com.emerginggames.snappers;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.emerginggames.snappers.data.FriendTable;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.transport.FacebookTransport;
import com.emerginggames.snappers.utils.WorkerThreads;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;
import com.emerginggames.snappers.model.FacebookFriend;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 17:34
 */
public class FacebookActivity extends BaseActivity {
    int wndWidth;
    FacebookTransport facebookTransport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_facebook);

        setupElements();

        wndWidth = Metrics.screenWidth;

        facebookTransport = new FacebookTransport(this);
        if (!facebookTransport.isLoggedIn()) {
            facebookTransport.login(new FacebookTransport.ResponseListener() {
                @Override
                public void onOk(Object data) {
                    getFriends();
                }

                @Override
                public void onError(Throwable e) {
                    finish();
                }
            }, false);
        } else {
            getFriends();
            facebookTransport.getName(null);
            facebookTransport.extendAccessTokenIfNeeded();
        }

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) findViewById(R.id.playButton).getLayoutParams();
        lp.leftMargin = lp.bottomMargin = Metrics.screenWidth / 20;
        lp.height = Metrics.screenWidth / 5;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebookTransport.getFB().authorizeCallback(requestCode, resultCode, data);
    }

    public void getFriends() {
        showProgressDialog();
        facebookTransport.getFriends(new FacebookTransport.ResponseListener() {
            @Override
            public void onOk(Object data) {
                FacebookFriend[] friends = (FacebookFriend[]) data;

                FriendTable tbl = new FriendTable(FacebookActivity.this, true);
                tbl.syncWithDb(friends);
                tbl.close();
                hideProgressDialog();
                showFriendsList(friends);
                facebookTransport.sync(null);
            }

            @Override
            public void onError(Throwable e) {
                facebookTransport.sync(new FacebookTransport.ResponseListener() {
                    @Override
                    public void onOk(Object data) {
                        play();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        play();
                        hideProgressDialog();
                    }
                });

            }
        });
    }

    void showFriendsList(final FacebookFriend[] friends) {
        findViewById(R.id.tableCont).setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();
        Typeface font = Resources.getFont(FacebookActivity.this);
        int starWidth = wndWidth * 56 / 640;
        LinearLayout table = (LinearLayout) findViewById(R.id.table);
        table.removeAllViews();

        int amount = friends.length;
        if (amount > 20)
            amount = 20;

        for (int i = 0; i < amount; i++) {
            FacebookFriend friend = friends[i];
            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.partial_friend_row, null);
            if (i % 2 == 1)
                row.setBackgroundColor(getResources().getColor(R.color.row_light));
            TextView title = (TextView) row.findViewById(R.id.title);
            title.setText(friend.first_name);
            title.setTypeface(font);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

            TextView score = (TextView) row.findViewById(R.id.score);
            score.setTypeface(font);
            score.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);

            RelativeLayout btn = (RelativeLayout) row.findViewById(R.id.btn);
            btn.setOnClickListener(friendClickListener);
            btn.setTag(friend);

            OutlinedTextView button = (OutlinedTextView) row.findViewById(R.id.btnText);
            button.setTypeface(font);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize * 8 / 10);

            if (friend.installed) {
                score.setText(Integer.toString(friend.xp_count));

                TextView star = (TextView) row.findViewById(R.id.level);
                star.setText(Integer.toString(Settings.getLevel(friend.xp_count)));
                star.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize * 2 / 3);
                star.setTypeface(font);
                star.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams lp = star.getLayoutParams();
                lp.height = lp.width = starWidth;

                if (System.currentTimeMillis() - friend.lastSendGift < Settings.GIFT_INTERVAL) {
                    button.setText(R.string.sent);
                    btn.setEnabled(false);
                } else
                    button.setText(R.string.gift);
            } else {
                score.setText(R.string.notPlaying);
                button.setText(R.string.invite);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            table.addView(row, lp);
            WorkerThreads.run(new LoadIconForView(friend.facebook_id, (ImageView)row.findViewById(R.id.iconUser)));
        }


    }

    void play() {
        ((SnappersApplication) getApplication()).setSwitchingActivities();
        startActivity(new Intent(this, SelectPackActivity.class));
    }

    public void onPlayButtonClick(View v) {
        SoundManager.getInstance(this).playButtonSound();
        play();
    }

    View.OnClickListener friendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            ImageView bar = (ImageView)v.findViewById(R.id.progress);

            int size = v.getHeight() - v.getPaddingBottom() - v.getPaddingTop();
            if (size == 0)
                size++;

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)bar.getLayoutParams();
            lp.width = lp.height = size;
            bar.setLayoutParams(lp);

            bar.setVisibility(View.VISIBLE);

            bar.setImageResource(R.drawable.spinner_white_48);
            v.findViewById(R.id.btnText).setVisibility(View.INVISIBLE);
            v.setClickable(false);

            Animation rotation = AnimationUtils.loadAnimation(FacebookActivity.this, R.anim.rotate);
            rotation.setRepeatCount(Animation.INFINITE);
            bar.startAnimation(rotation);

            FacebookFriend friend = (FacebookFriend) v.getTag();
            if (friend.installed)
                facebookTransport.gift(friend, new GiftInviteResponceListener(friend, v, bar));
            else{
                String code = UserPreferences.getInstance(getApplicationContext()).getPromoCode();
                String msg = getString(R.string.inviteMessage, friend.first_name, code);
                facebookTransport.invite(friend.id, msg, new GiftInviteResponceListener(friend, v, bar));
            }
        }
    };

    class GiftInviteResponceListener extends FacebookTransport.ResponseListener{
        FacebookFriend friend;
        View v;
        View img;

        GiftInviteResponceListener(FacebookFriend friend, View v, View bar) {
            this.friend = friend;
            this.v = v;
            img = bar;
        }

        @Override
        public void onOk(Object data) {
            OutlinedTextView text = (OutlinedTextView) v.findViewById(R.id.btnText);
            if (friend.installed)
                text.setText2(R.string.sent);
            else
                text.setText2(R.string.invited);
            text.setVisibility(View.VISIBLE);
            v.setEnabled(false);
            img.clearAnimation();
            img.setVisibility(View.INVISIBLE);

            //((ImageView)img).setImageResource(R.drawable.transparent);
        }

        @Override
        public void onError(Throwable e) {
            img.clearAnimation();
            img.setVisibility(View.INVISIBLE);
            //((ImageView)img).setImageResource(R.drawable.transparent);
            v.setClickable(true);
            v.findViewById(R.id.btnText).setVisibility(View.VISIBLE);
        }
    }

    class LoadIconForView implements Runnable {
        private static final String URL_TEMPLATE_MED = "http://graph.facebook.com/%d/picture?type=normal";
        ImageView view;
        long uid;

        LoadIconForView(long uid, ImageView view) {
            this.uid = uid;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                URL img_value = new URL(String.format(URL_TEMPLATE_MED, uid));
                Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                view.setImageBitmap(mIcon1);
            } catch (Throwable e) {
            }
        }
    }
}