package com.emerginggames.snappers2.view;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.emerginggames.snappers2.R;
import com.emerginggames.snappers2.gdx.Resources;
import com.emerginggames.snappers2.GameActivity;
import com.emerginggames.snappers2.Metrics;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.06.12
 * Time: 14:32
 */
public     class GameOverMessageController {
    private static final int WIN_TITLES = 7;
    RelativeLayout layout;
    OutlinedTextView title;
    OutlinedTextView message;
    boolean isWon;
    int msgValue;
    StarsController stars;
    GameActivity mActivity;
    RelativeLayout mRootLayout;


    public GameOverMessageController(RelativeLayout rootLayout, GameActivity activity) {
        mRootLayout = rootLayout;
        mActivity = activity;
        layout = (RelativeLayout)mActivity.getLayoutInflater().inflate(R.layout.partial_level_result, null);
        title = (OutlinedTextView)layout.findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.largeFontSize);
        title.setTypeface(Resources.getFont(mActivity));
        message = (OutlinedTextView)layout.findViewById(R.id.message);
        message.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
        message.setTypeface(Resources.getFont(mActivity));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)title.getLayoutParams();
        lp.bottomMargin = - Metrics.largeFontSize / 3;
        title.setLayoutParams(lp);

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        mRootLayout.addView(layout, rlp);
        layout.setVisibility(View.GONE);

        stars = new StarsController();
    }

    public void show(boolean isWon, int msgValue){
        this.isWon = isWon;
        this.msgValue = msgValue;
        mActivity.runOnUiThread(show);
    }

    public void show(){
        mActivity.runOnUiThread(show);
    }

    public void setResult(boolean isWon, int msgValue){
        this.isWon = isWon;
        this.msgValue = msgValue;
    }

    public void hide(){
        mActivity.runOnUiThread(hide);
    }

    public void setAdVisible(boolean visible){
        stars.onAdVisibilityChanged(visible);
    }

    int getWinTitleId(){
        int n = (int)(Math.random()*WIN_TITLES) + 1;
        String resourceName = String.format("game_won_%d", n);
        return mActivity.getResources().getIdentifier(resourceName, "string", mActivity.getPackageName());
    }

    int getLostTitleId(){
        return R.string.game_lost_1;
    }

    Runnable show = new Runnable() {
        @Override
        public void run() {
            int titleId = isWon ? getWinTitleId() : getLostTitleId();
            title.setText2(titleId);
            String msg;
            if (isWon){
                msg = mActivity.getString(R.string.score, msgValue);
                stars.showScoreStars(msgValue);
            }
            else if (msgValue == 1)
                msg = mActivity.getString(R.string.possible_in_1_touch);
            else
                msg = mActivity.getString(R.string.possible_in_touches, msgValue);
            message.setText2(msg);
            layout.setVisibility(View.VISIBLE);
        }
    };

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            layout.setVisibility(View.GONE);
        }
    };

    class StarsController{
        RelativeLayout.LayoutParams starLP;
        private Array<ImageView> activeStars ;
        private Pool<ImageView> starsPool;
        int activeStarsCount;
        Interpolator interpolatorIn = new AccelerateInterpolator();
        Interpolator interpolatorOut = new LinearInterpolator();

        StarsController() {
            int size = (int)(Metrics.squareButtonSize * Metrics.squareButtonScale);
            starLP = new RelativeLayout.LayoutParams(size, size);

            starsPool = new Pool<ImageView>(5, 10){
                @Override
                protected ImageView newObject() {
                    ImageView img = new ImageView(mActivity);
                    img.setImageResource(R.drawable.star);
                    img.setLayoutParams(starLP);
                    return img;
                }
            };
            activeStars = new Array<ImageView>(10);
        }

        Runnable freeStars = new Runnable() {
            @Override
            public void run() {
                for (int i=0; i< activeStars.size; i++)
                    mRootLayout.removeView(activeStars.get(i));
                starsPool.free(activeStars);
            }
        };

        Animation.AnimationListener animListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                activeStarsCount--;
                if (activeStarsCount == 0)
                    mActivity.runOnUiThread(freeStars);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        };

        public void onAdVisibilityChanged(boolean visible){
            starLP.topMargin = visible ? mActivity.getAdController().getAdHeight() : 0;
        }

        public void showScoreStars(int score){
            float size = Metrics.squareButtonSize * Metrics.squareButtonScale;
            int amount = 3 + score / 1500;
            if (mActivity.getAdController()!= null)
                starLP.topMargin = mActivity.getAdController().getAdHeight();

            for (int i=0; i< amount; i++){
                ImageView img = starsPool.obtain();
                float endScale = (float)(Math.random() *0.3 + 0.7);
                int endX = (int)(Math.random() * size * (1.2 - endScale));
                int endY = (int)(Math.random() * size * (1.2 - endScale));
                float startX = (float)(Math.random() *0.7 + 0.3);
                float startY = (float)(Math.random() *0.7 + 0.3);
                int timeDev = (int)(Math.random() * 400);
                img.setAnimation(getAnimation(startX, startY, endX, endY, endScale, timeDev));
                mRootLayout.addView(img);
                activeStarsCount++;
            }
        }

        Animation getAnimation(float startX, float startY, int endX, int endY, float endScale, int timeDeviation){

            TranslateAnimation moveInAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, startX, Animation.ABSOLUTE, endX,
                    Animation.RELATIVE_TO_PARENT, startY, Animation.ABSOLUTE, endY);
            moveInAnim.setDuration(600 + timeDeviation);
            moveInAnim.setStartOffset(0);
            moveInAnim.setInterpolator(interpolatorIn);

            ScaleAnimation scaleInAnim = new ScaleAnimation(0, endScale, 0, endScale);
            scaleInAnim.setDuration(600 + timeDeviation);
            scaleInAnim.setInterpolator(interpolatorIn);

            ScaleAnimation scaleOutAnim = new ScaleAnimation(endScale, 0, endScale, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f  );
            scaleOutAnim.setDuration(500);
            scaleOutAnim.setStartOffset(1300 + timeDeviation);
            scaleOutAnim.setInterpolator(interpolatorOut);

            AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 0);
            fadeOutAnimation.setDuration(500);
            fadeOutAnimation.setStartOffset(1300 + timeDeviation);
            fadeOutAnimation.setInterpolator(interpolatorOut);

            AnimationSet animSet = new AnimationSet(true);
            animSet.addAnimation(scaleInAnim);
            animSet.addAnimation(moveInAnim);
            animSet.addAnimation(scaleOutAnim);
            animSet.addAnimation(fadeOutAnimation);

            animSet.setAnimationListener(animListener);
            animSet.setFillAfter(true);

            return animSet;
        }
    }
}

