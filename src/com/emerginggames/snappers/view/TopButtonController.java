package com.emerginggames.snappers.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.emerginggames.snappers.GameActivity;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.R;
import com.emerginggames.snappers.SoundManager;
import com.emerginggames.snappers.gdx.Game;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 18.07.12
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
public class TopButtonController {
    RelativeLayout layout;
    ImageView pauseBtn;
    ImageView hintBtn;
    ImageView shopBtn;
    ImageView nextBtn;
    ImageView restartBtn;
    ImageView menuBtn;
    ImageView helpBtn;
    RelativeLayout.LayoutParams rlpTop;
    RelativeLayout.LayoutParams rlpUnderView;
    GameActivity activity;
    Game game;
    View helpView;
    RelativeLayout rootLayout;

    public TopButtonController(RelativeLayout rootLayout, GameActivity activity, Game game){
        this.activity = activity;
        this.game = game;
        this.rootLayout = rootLayout;
        layout = (RelativeLayout)activity.getLayoutInflater().inflate(R.layout.partial_topbuttons, null);
        pauseBtn = (ImageView)layout.findViewById(R.id.pauseBtn);
        hintBtn = (ImageView)layout.findViewById(R.id.hintBtn);
        shopBtn = (ImageView)layout.findViewById(R.id.shopBtn);
        nextBtn = (ImageView)layout.findViewById(R.id.nextBtn);
        restartBtn = (ImageView)layout.findViewById(R.id.restartBtn);
        menuBtn = (ImageView)layout.findViewById(R.id.menuBtn);
        helpBtn = (ImageView)layout.findViewById(R.id.helpBtn);

        pauseBtn.setOnClickListener(mainListener);
        hintBtn.setOnClickListener(mainListener);
        shopBtn.setOnClickListener(mainListener);
        nextBtn.setOnClickListener(mainListener);
        restartBtn.setOnClickListener(mainListener);
        menuBtn.setOnClickListener(mainListener);
        helpBtn.setOnClickListener(mainListener);

        rlpTop = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, Math.round(Metrics.squareButtonSize * Metrics.squareButtonScale));
        rlpTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rlpTop.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlpTop.setMargins(Metrics.screenMargin, Metrics.screenMargin, Metrics.screenMargin, 0);

        rootLayout.addView(layout, rlpTop);
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

    void hideAll(){
        pauseBtn.setVisibility(View.GONE);
        hintBtn.setVisibility(View.GONE);
        shopBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        restartBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        helpBtn.setVisibility(View.GONE);
    }

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
            shopBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            restartBtn.setVisibility(View.VISIBLE);
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

    View.OnClickListener mainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!game.initDone)
                return;
            SoundManager.getInstance(activity).playButtonSound();
            switch (v.getId()){
                case R.id.pauseBtn:
                    activity.showPausedDialog();
                    break;
                case R.id.hintBtn:
                    if (game.isHinting())
                        return;

                    if (game.isFreeHint())
                        game.useHint();
                    else
                        activity.showHintMenu();
                    break;
                case R.id.shopBtn:
                    activity.launchStore();
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
                    showHelp();
                    break;
            }
        }
    };

    public void showHelp(){
        helpView = activity.getLayoutInflater().inflate(R.layout.partial_help, null);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeView(v);
                game.setStage(Game.Stages.GameOverStage);
                helpView = null;
                SoundManager.getInstance(activity).playButtonSound();
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rootLayout.addView(helpView, lp);
        game.setStage(Game.Stages.HelpStage);
        hideAll();
        activity.getLevelInfo().hideText();
    }

    public void hideHelpIfNeeded(){
        if (helpView != null){
            rootLayout.removeView(helpView);
            helpView = null;
        }
    }
}
