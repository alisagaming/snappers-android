package ru.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.w3c.dom.Text;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.gdx.Resources;

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
    Context context;

    public ScoreCounter(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;

        Typeface font = Resources.getFont(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (RelativeLayout)inflater.inflate(R.layout.score_counter, null);

        TextView levelText = (TextView)root.findViewById(R.id.level);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)levelText.getLayoutParams();
        mlp.width = height;
        mlp.height = height;
        levelText.setLayoutParams(mlp);
        levelText.setTypeface(font);
        levelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 3);

        int bgHeight = levelText.getBackground().getIntrinsicHeight();
        float scale = ((float)height / bgHeight );
        int[] iPaddings = {32, 31, 32, 36};
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
        bar.setLayoutParams(rlp);

        View barFill = root.findViewById(R.id.bar_fill);
        mlp = (ViewGroup.MarginLayoutParams)barFill.getLayoutParams();
        mlp.width = barWidth;
        barFill.setLayoutParams(mlp);
    }

    public void setLevel(int level, int currentLevelScore, int nextLevelScore){
        this.level = level;
        this.levelScore = currentLevelScore;
        this.nextLevelScore = nextLevelScore;
        TextView levelText = (TextView)root.findViewById(R.id.level);
        levelText.setText(Integer.toString(level));
    }

    public void setScore(int score){
        float progress = Math.min(((float)score - levelScore) / (nextLevelScore - levelScore), 1);
        int filledWidth = Math.round(progress * barWidth);

        View v = root.findViewById(R.id.inner);
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = filledWidth;
        v.setLayoutParams(lp);

        ((OutlinedTextView)root.findViewById(R.id.score)).setText(Integer.toString(score));
    }

    public View getView(){
        return root;
    }

    public void setVisibility(int visibility){
        root.setVisibility(visibility);
    }
}