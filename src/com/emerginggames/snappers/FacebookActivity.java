package com.emerginggames.snappers;

import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.emerginggames.snappers.data.FriendTable;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.SyncData;
import com.emerginggames.snappers.transport.FacebookTransport;
import com.emerginggames.snappers.utils.Utils;
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
    private static final int SYNC_DONE = 1;
    private static final int SYNC_FAILED = 2;
    private static final int FRIENDS_DONE = 4;
    private static final int FRIENDS_FAILED = 8;
    private static final int UINFO_DONE = 16;
    private static final int UINFO_FAILED = 32;
    private static final int JUST_LOGGED = 64;
    private static final int ALL_DONE = SYNC_DONE | FRIENDS_DONE | UINFO_DONE;
    int wndWidth;
    FacebookTransport facebookTransport;
    int flags;
    FacebookFriend[] friends;
    UserPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_facebook);

        setupElements();

        wndWidth = Metrics.screenWidth;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) findViewById(R.id.playButton).getLayoutParams();
        lp.leftMargin = lp.bottomMargin = Metrics.screenWidth / 20;
        lp.height = Metrics.screenWidth / 5;

        prefs = UserPreferences.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        facebookTransport = new FacebookTransport(this);
        if (!facebookTransport.isLoggedIn()) {
            facebookTransport.login(new FacebookTransport.ResponseListener() {
                @Override
                public void onOk(Object data) {
                    gotFlag(JUST_LOGGED);
                    showProgressDialog(getString(R.string.loading));
                }

                @Override
                public void onError(Throwable e) {
                    if (e != null)
                        Log.e(TAG, e.getMessage(), e);
                    finish();
                }
            });
        } else {
            if (prefs.hadFbSync())
                gotFlag(JUST_LOGGED);
            else{
                getFriends();
                getUInfo();
                sync();
            }
            FacebookFriend[] friends = FriendTable.getAll(this);
            if (friends == null || friends.length == 0)
                showProgressDialog(getString(R.string.loading));
            else
                showFriendsList(friends);
            facebookTransport.extendAccessTokenIfNeeded();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebookTransport.getFB().authorizeCallback(requestCode, resultCode, data);
    }

    void getUInfo(){
        if (checkFlag(UINFO_DONE) && !checkFlag(UINFO_FAILED))
            return;
        facebookTransport.getUserInfo(new FacebookTransport.ResponseListener() {
            @Override
            public void onOk(Object data) {
                gotFlag(UINFO_DONE);
            }

            @Override
            public void onError(Throwable e) {
                gotFlag(UINFO_FAILED);
            }
        });
    }

    void sync(){
        if (checkFlag(SYNC_DONE) && !checkFlag(SYNC_FAILED))
            return;
        facebookTransport.sync(new FacebookTransport.ResponseListener(){
            @Override
            public void onOk(Object data) {
                gotFlag(SYNC_DONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                gotFlag(SYNC_FAILED);
            }
        });
    }

    void getFriends(){
        facebookTransport.getFriends(new FacebookTransport.ResponseListener() {
            @Override
            public void onOk(Object data) {
                friends = (FacebookFriend[]) data;
                gotFlag(FRIENDS_DONE);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                gotFlag(FRIENDS_FAILED);
            }
        });
    }

    boolean checkFlag(int flag){
        return (flags & flag) == flag;
    }


    void gotFlag(int flag){
        flags |= flag;
        switch (flag){
            case JUST_LOGGED:
                getFriends();
                getUInfo();
                break;
            case UINFO_DONE:
                flags = flags & ~UINFO_FAILED;
                if ((flags & JUST_LOGGED) == JUST_LOGGED)
                    sync();
                break;
            case UINFO_FAILED:
                if ((flags & JUST_LOGGED) == JUST_LOGGED){
                    showToast(getString(R.string.failedToGetUserInfo));
                    finish();
                } else
                    flags |= UINFO_DONE;
                break;
            case FRIENDS_DONE:
                flags = flags & ~FRIENDS_FAILED;
                FriendTable.syncWithDb(getApplicationContext(), friends);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        showFriendsList(friends);
                    }
                });
                break;
            case FRIENDS_FAILED:
                if ((flags & JUST_LOGGED) == JUST_LOGGED)
                    showToast(getString(R.string.failedToGetFriends));
                flags |= FRIENDS_DONE;
                break;
            case SYNC_DONE:
                flags = flags & ~SYNC_FAILED;
                updateHints();
                updateScore();
                break;
            case SYNC_FAILED:
                if ((flags & JUST_LOGGED) == JUST_LOGGED)
                    showToast(getString(R.string.failedToGetUserInfo));
                flags |= SYNC_DONE;
                break;
        }
        if ((flags & ALL_DONE) == ALL_DONE)
            hideProgressDialog();
    }

    void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FacebookActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    void showGifts(SyncData data){
        if (data == null || data.gifts == null || data.gifts.length == 0)
        return;

        showMessageDialog(Utils.getGiftsMessage(this, data));
    }

    void showFriendsList(final FacebookFriend[] friends) {
        if (friends == null || friends.length == 0)
            return;

        this.friends = friends;

        LinearLayout table = (LinearLayout) findViewById(R.id.table);
        table.removeAllViewsInLayout();
        showFriendsRows(0,  20);
        showControls();
    }

    void showControls(){
        findViewById(R.id.tableCont).setVisibility(View.VISIBLE);
        findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        findViewById(R.id.playButton).setVisibility(View.VISIBLE);
    }

    void play() {
        ((SnappersApplication) getApplication()).setSwitchingActivities();
        startActivity(new Intent(this, SelectPackActivity.class));
    }

    public void onPlayButtonClick(View v) {
        SoundManager.getInstance(this).playButtonSound();
        play();
    }

    void showFriendsRows(int start, int max){
        LayoutInflater inflater = getLayoutInflater();
        Typeface font = Resources.getFont(FacebookActivity.this);
        int starWidth = wndWidth * 56 / 640;
        LinearLayout table = (LinearLayout) findViewById(R.id.table);

        int amount = Math.min(max, friends.length - start);

        Runnable loaders[] = new Runnable[amount];

        for (int i = 0; i < amount; i++) {
            FacebookFriend friend = friends[start + i];
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
            loaders[i] = new LoadIconForView(friend.facebook_id, (ImageView)row.findViewById(R.id.iconUser));
        }

        if (start + amount < friends.length){
            TextView more = new TextView(this);
            more.setText(R.string.showMore);
            more.setTypeface(font);
            more.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
            more.setTextColor(getResources().getColor(R.color.textColor));
            more.setGravity(Gravity.CENTER);
            more.setOnClickListener(moreClickListener);
            more.setBackgroundColor(R.color.row_dark);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 3 * Metrics.fontSize);
            table.addView(more, lp);
        }

        getWindow().getDecorView().requestLayout();
        getWindow().getDecorView().invalidate();

        WorkerThreads.run(loaders);
    }

    View.OnClickListener friendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            ImageView bar = (ImageView)v.findViewById(R.id.progress);

            int size = v.getHeight() - v.getPaddingBottom() - v.getPaddingTop();
            if (size == 0)
                size = 30;

            showProgress(bar, size);

            //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)bar.getLayoutParams();
            //lp.width = lp.height = size;
            //bar.setLayoutParams(lp);

            //bar.setVisibility(View.VISIBLE);

            //bar.setImageResource(R.drawable.spinner_white_48);
            v.findViewById(R.id.btnText).setVisibility(View.INVISIBLE);
            v.setClickable(false);

            //Animation rotation = AnimationUtils.loadAnimation(FacebookActivity.this, R.anim.rotate);
            //rotation.setRepeatCount(Animation.INFINITE);
            //bar.startAnimation(rotation);

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

    void showProgress(ImageView bar, int size){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)bar.getLayoutParams();
        lp.width = lp.height = size;
        bar.setLayoutParams(lp);
        bar.setVisibility(View.VISIBLE);
        bar.setImageResource(R.drawable.spinner_white_48);

        Animation rotation = AnimationUtils.loadAnimation(FacebookActivity.this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        bar.startAnimation(rotation);
    }

    View.OnClickListener moreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout table = ((LinearLayout)findViewById(R.id.table));
            table.removeViewInLayout(v);
            int currentShown = ((LinearLayout) findViewById(R.id.table)).getChildCount();
            showFriendsRows(currentShown, 20);
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