package com.emerginggames.snappers2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.emerginggames.snappers2.data.FriendTable;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.model.FacebookFriend;
import com.emerginggames.snappers2.model.SyncData;
import com.emerginggames.snappers2.transport.FacebookTransport;
import com.emerginggames.snappers2.utils.CachedFile;
import com.emerginggames.snappers2.utils.FacebookIconLoader;
import com.emerginggames.snappers2.utils.Utils;
import com.emerginggames.snappers2.utils.WorkerThreads;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;

import java.io.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 01.07.12
 * Time: 6:44
 */
public class InviteActivity extends BaseActivity {
    int wndWidth;
    FacebookTransport facebookTransport;
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

        OutlinedTextView bottomText = (OutlinedTextView)findViewById(R.id.bottomMessage);
        bottomText.setTypeface(Resources.getFont(getApplicationContext()));

        OutlinedTextView title =  (OutlinedTextView)findViewById(R.id.title);
        title.setTypeface(Resources.getFont(getApplicationContext()));

        prefs = UserPreferences.getInstance(getApplicationContext());
        facebookTransport = new FacebookTransport(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFriends();

        FacebookFriend[] friends = FriendTable.getUnregistered(this);
        if (friends == null || friends.length == 0)
            showProgressDialog(getString(R.string.loading));
        else
            showFriendsList(friends);
    }

    void getFriends(){
        facebookTransport.getFriends(new FacebookTransport.ResponseListener() {
            @Override
            public void onOk(Object data) {
                friends = (FacebookFriend[]) data;
                FriendTable.syncWithDb(getApplicationContext(), friends);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        showFriendsList(FriendTable.getUnregistered(InviteActivity.this));
                    }
                });

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    void showFriendsList(FacebookFriend[] friends) {
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
    }

    void showFriendsRows(int start, int max){
        LayoutInflater inflater = getLayoutInflater();
        Typeface font = Resources.getFont(this);
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
            loaders[i] = new FacebookIconLoader(this, friend.facebook_id, (ImageView)row.findViewById(R.id.iconUser));
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

            v.findViewById(R.id.btnText).setVisibility(View.INVISIBLE);
            v.setClickable(false);

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

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
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
            else{
                text.setText2(R.string.invited);
                prefs.addHints(Settings.BONUS_FOR_INVITE);
                updateHints();
            }
            text.setVisibility(View.VISIBLE);
            v.setEnabled(false);
            img.clearAnimation();
            img.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError(Throwable e) {
            img.clearAnimation();
            img.setVisibility(View.INVISIBLE);
            v.setClickable(true);
            v.findViewById(R.id.btnText).setVisibility(View.VISIBLE);
        }
    }

/*    class LoadIconForView extends CachedFile  implements CachedFile.CachedFileListener{
        private static final String URL_TEMPLATE_MED = "http://graph.facebook.com/%d/picture?type=normal";
        private static final String FILE_TEMPLATE = "fbicon_%d.png";
        ImageView view;
        long uid;

        LoadIconForView(long uid, ImageView view) {
            super(getApplicationContext());
            this.uid = uid;
            this.view = view;
            setData(String.format(FILE_TEMPLATE, uid), String.format(URL_TEMPLATE_MED, uid));
            setListener(this);
        }

        @Override
        public void onGotFile(final File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream input = new FileInputStream(file);
                        Bitmap mIcon1 = BitmapFactory.decodeStream(input);
                        view.setImageBitmap(mIcon1);
                        input.close();

                    } catch (FileNotFoundException e) {
                        onError(e);
                    } catch (IOException e) {
                        onError(e);
                    }
                }
            });
        }

        @Override
        public void onUpdateFile(File file) {
            onGotFile(file);
        }

        @Override
        public void onError(Throwable t) {
            Log.e(Settings.TAG, t.getMessage(), t);
        }
    }*/
}