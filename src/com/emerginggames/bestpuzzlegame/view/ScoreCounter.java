package com.emerginggames.bestpuzzlegame.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.emerginggames.bestpuzzlegame.R;
import com.emerginggames.bestpuzzlegame.gdx.Resources;
import com.emerginggames.bestpuzzlegame.Metrics;
import com.emerginggames.bestpuzzlegame.Settings;
import com.emrg.view.OutlinedTextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 27.05.12
 * Time: 1:50
 */
public class ScoreCounter {
    int width;
    int height;
    RelativeLayout root;
    int level;
    int currentScore;
    int levelScore;
    int nextLevelScore;
    int barWidth;
    int barCoveredWidth;
    Context context;
    Handler handler;
    int scoreStep;
    int scoreDest;

    public ScoreCounter(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;

        Typeface font = Resources.getFont(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (RelativeLayout)inflater.inflate(R.layout.score_counter, null);

        OutlinedTextView levelText = (OutlinedTextView)root.findViewById(R.id.level);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)levelText.getLayoutParams();
        barCoveredWidth = mlp.width = height;
        mlp.height = height;
        levelText.setTypeface(font);
        //levelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 3);
        levelText.setMaxLines2(1);

        int bgHeight = levelText.getBackground().getIntrinsicHeight();
        float scale = ((float)height / bgHeight );
        int[] iPaddings = {33, 30, 33, 36};
        levelText.setPadding((int)(iPaddings[0] * scale), (int)(iPaddings[1] * scale), (int)(iPaddings[2] * scale), (int)(iPaddings[3] * scale));


        OutlinedTextView otv = (OutlinedTextView)root.findViewById(R.id.score);
        otv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Metrics.fontSize);
        otv.setTypeface(font);

        RelativeLayout bar = (RelativeLayout)root.findViewById(R.id.bar_back);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams)bar.getLayoutParams();
        rlp.leftMargin = height * 12 / 20;
        rlp.width = barWidth = width - rlp.leftMargin;
        rlp.height = height * 27 / 40;
        rlp.addRule(RelativeLayout.CENTER_VERTICAL);
        barCoveredWidth -= rlp.leftMargin;
        barCoveredWidth = Math.round(barCoveredWidth * 0.9f);

        View barFill = root.findViewById(R.id.bar_fill);
        mlp = (ViewGroup.MarginLayoutParams)barFill.getLayoutParams();
        mlp.width = barWidth;
        handler = new Handler();
    }

    public void setLevel(int level, int currentLevelScore, int nextLevelScore){
        this.level = level;
        this.levelScore = currentLevelScore;
        this.nextLevelScore = nextLevelScore;
        TextView levelText = (TextView)root.findViewById(R.id.level);
        levelText.setText(Integer.toString(level));
    }

    public void setScoreProlonged(int score){
        scoreStep = Math.max(1, (score - currentScore) / 15);
        scoreDest = score;
        updateScore.run();
    }

    public void setScore(int score){
        int newLevel = Settings.getLevel(score);
        if (newLevel != level)
            setLevel(newLevel, Settings.getLevelXp(newLevel), Settings.getLevelXp(newLevel+1));

        boolean isMaxLevel = Settings.isMaxLevel(newLevel);

        float progress = isMaxLevel ? 1 : Math.min(((float)score - levelScore) / (nextLevelScore - levelScore), 1);
        int filledWidth = Math.round(progress * (barWidth - barCoveredWidth)) + barCoveredWidth;

        View v = root.findViewById(R.id.inner);
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = filledWidth;
        v.setLayoutParams(lp);

        if (isMaxLevel)
            ((OutlinedTextView)root.findViewById(R.id.score)).setText2(R.string.maxLevel);
        else
            ((OutlinedTextView)root.findViewById(R.id.score)).setText2(Integer.toString(score));
        currentScore = score;
    }

    public View getView(){
        return root;
    }

    public void setVisibility(int visibility){
        root.setVisibility(visibility);
    }

    Runnable updateScore = new Runnable() {
        @Override
        public void run() {
            currentScore += scoreStep;
            if (currentScore > scoreDest)
                currentScore = scoreDest;
            setScore(currentScore);
            if (currentScore < scoreDest)
                handler.postDelayed(updateScore, 40);

        }
    };
}
