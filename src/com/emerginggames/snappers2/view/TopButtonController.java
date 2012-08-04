package com.emerginggames.snappers2.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.emerginggames.snappers2.*;
import com.emerginggames.snappers2.gdx.Game;
import com.emerginggames.snappers2.gdx.Resources;
import com.emrg.view.ImageView;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 16.06.12
 * Time: 14:20
 */
public class TopButtonController {
    RelativeLayout layout;
    ImageView pauseBtn;
    OutlinedTextView hintBtn;

    ImageView nextBtn;
    ImageView restartBtn;
    ImageView menuBtn;
    ImageView helpBtn;
    RelativeLayout.LayoutParams rlpTop;
    RelativeLayout.LayoutParams rlpUnderView;
    ScoreCounter scoreCounter;
    GameActivity activity;
    UserPreferences prefs;

    public TopButtonController(RelativeLayout rootLayour, GameActivity activity){
        this.activity = activity;
        prefs = UserPreferences.getInstance(activity);

        layout = (RelativeLayout)activity.getLayoutInflater().inflate(R.layout.partial_topbuttons, null);
        pauseBtn = (ImageView)layout.findViewById(R.id.pauseBtn);
        hintBtn = (OutlinedTextView)layout.findViewById(R.id.hintBtn);
        nextBtn = (ImageView)layout.findViewById(R.id.nextBtn);
        restartBtn = (ImageView)layout.findViewById(R.id.restartBtn);
        menuBtn = (ImageView)layout.findViewById(R.id.menuBtn);
        helpBtn = (ImageView)layout.findViewById(R.id.helpBtn);

        pauseBtn.setOnClickListener(mainListener);
        hintBtn.setOnClickListener(mainListener);
        nextBtn.setOnClickListener(mainListener);
        restartBtn.setOnClickListener(mainListener);
        menuBtn.setOnClickListener(mainListener);
        helpBtn.setOnClickListener(mainListener);

        rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale));
        rlpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlpTop.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlpTop.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)hintBtn.getLayoutParams();
        int bgWidth = hintBtn.getBackground().getIntrinsicWidth();
        int bgHeight = hintBtn.getBackground().getIntrinsicHeight();
        float scale = ((float)rlpTop.height / bgHeight );
        lp.width = Math.round(bgWidth * scale);
        lp.height = rlpTop.height;
        int[] iPaddings = {27, 23, 94, 23};
        hintBtn.setPadding((int)(iPaddings[0] * scale), (int)(iPaddings[1] * scale), (int)(iPaddings[2] * scale), (int)(iPaddings[3] * scale));
        hintBtn.setLayoutParams(lp);
        hintBtn.setTypeface(Resources.getFont(activity.getApplicationContext()));
        hintBtn.setMaxLines2(1);
        updateHints();

        addScoreCounter();
        rootLayour.addView(layout, rlpTop);

        lp = (RelativeLayout.LayoutParams)pauseBtn.getLayoutParams();
        lp.width = lp.height = rlpTop.height;
    }

    public void updateHints(){
        activity.runOnUiThread(updateHints);
    }

    void addScoreCounter(){
        int size = (int)(Metrics.squareButtonSize * Metrics.squareButtonScale);
        scoreCounter = new ScoreCounter(activity, size * 32 / 10, size);
        scoreCounter.setVisibility(View.GONE);
        scoreCounter.setScore(prefs.getScore());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layout.addView(scoreCounter.getView(), lp);
    }

    public void alignTop(){
        layout.setLayoutParams(rlpTop);
    }

    public void alignUnderView(View v){
        if (rlpUnderView == null){
            rlpUnderView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) (Metrics.squareButtonSize * Metrics.squareButtonScale));
            rlpUnderView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlpUnderView.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);
        }
        rlpUnderView.addRule(RelativeLayout.BELOW, v.getId());
        layout.setLayoutParams(rlpUnderView);
    }


    public void showMainButtons(){
        activity.runOnUiThread(showMainButtons);
    }

    public void showGameWonMenu(){
        activity.runOnUiThread(showWonMenu);
    }

    public void showGameLostMenu(){
        activity.runOnUiThread(showLostMenu);
    }

    public void showScore(){
        activity.runOnUiThread(showScore);
    }

    public void hideScore(){
        activity.runOnUiThread(hideScore);
    }

    public void hideAll(){
        pauseBtn.setVisibility(View.GONE);
        hintBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        restartBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        helpBtn.setVisibility(View.GONE);
    }

    Runnable updateHints = new Runnable() {
        @Override
        public void run() {
            hintBtn.setText2(Integer.toString(prefs.getHintsRemaining()));
        }
    };

    Runnable showMainButtons = new Runnable() {
        @Override
        public void run() {
            hideAll();
            pauseBtn.setVisibility(View.VISIBLE);
            hintBtn.setVisibility(View.VISIBLE);
        }
    };

    Runnable showWonMenu = new Runnable() {
        @Override
        public void run() {
            hideAll();
            nextBtn.setVisibility(View.VISIBLE);
            menuBtn.setVisibility(View.VISIBLE);
        }
    };

    Runnable showLostMenu = new Runnable() {
        @Override
        public void run() {
            hideAll();
            restartBtn.setVisibility(View.VISIBLE);
            menuBtn.setVisibility(View.VISIBLE);
            helpBtn.setVisibility(View.VISIBLE);
        }
    };

    Runnable showScore = new Runnable() {
        @Override
        public void run() {
            scoreCounter.setScoreProlonged(prefs.getScore());
            scoreCounter.setVisibility(View.VISIBLE);
        }
    };

    Runnable hideScore = new Runnable() {
        @Override
        public void run() {
            scoreCounter.setVisibility(View.GONE);
        }
    };

    View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SoundManager.getInstance(activity).playButtonSound();
            Game game = activity.getGame();
            switch (v.getId()){
                case R.id.pauseBtn:
                    activity.pauseGame();
                    break;
                case R.id.hintBtn:
                    if (game.isHinting())
                        return;

                    if (game.isFreeHint())
                        game.useHint();
                    else
                        activity.showHintMenu();
                    break;
                case R.id.nextBtn:
                    game.nextLevel();
                    game.setStage(Game.Stages.MainStage);
                    break;
                case R.id.restartBtn:
                    game.restartLevel();
                    game.setStage(Game.Stages.MainStage);
                    break;
                case R.id.menuBtn:
                    activity.finish();
                    break;
                case R.id.helpBtn:
                    activity.showHelp();
                    break;
            }
        }
    };
}
