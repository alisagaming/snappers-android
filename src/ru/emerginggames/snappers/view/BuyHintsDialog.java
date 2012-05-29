package ru.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ru.emerginggames.snappers.Metrics;
import ru.emerginggames.snappers.R;
import ru.emerginggames.snappers.gdx.Resources;
import ru.emerginggames.snappers.model.Goods;
import ru.emerginggames.snappers.utils.GInAppStore;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.05.12
 * Time: 19:27
 */
public class BuyHintsDialog extends GameDialog {
    private static final String[] amounts = {"x 3", "x 10", "x 25", "x 75"};
    private static final String[] prices = {"$1.99", "$4.99", "$9.99", "$24.99"};
    private int bestBuy = 2;
    int wndWidth;
    public BuyHintsDialog(Context context, int width) {
        super(context);
        setWidth(width);
        setTwoButtonsARow(true);
        wndWidth = width;
        addButtons();

    }

    void addButtons(){
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout item;
        OutlinedTextView text;
        Typeface font = Resources.getFont(getContext());
        int fontSize1 = Metrics.fontSize;
        int fontSize2 = (int)(Metrics.fontSize * 1.2f);
        for (int i=0; i< amounts.length; i++){
            item = (RelativeLayout)inflater.inflate(R.layout.paitial_buy_hints_btn, null);
            text = (OutlinedTextView)item.findViewById(R.id.title);
            text.setText2(amounts[i]);
            text.setTypeface(font);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize1);

            text = (OutlinedTextView)item.findViewById(R.id.price);
            text.setText2(prices[i]);
            text.setTypeface(font);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize2);

            item.setTag(i);
            item.setOnClickListener(clickListener);

            LinearLayout square = (LinearLayout)item.findViewById(R.id.squareCont);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)square.getLayoutParams();
            lp.leftMargin = lp.rightMargin = lp.topMargin = lp.bottomMargin = Metrics.screenMargin;
            square.setLayoutParams(lp);

            View bestBuyView = item.findViewById(R.id.bestDeal);
            if (i == bestBuy){
                lp = (RelativeLayout.LayoutParams)bestBuyView.getLayoutParams();
                lp.height = lp.width = wndWidth / 8;
                bestBuyView.setLayoutParams(lp);
            }
            else bestBuyView.setVisibility(View.GONE);

            addButton(item);
        }
    }

    final View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch((Integer)v.getTag()){
                case 0:
                    GInAppStore.getInstance(getContext()).buy(Goods.HintPack3);
                    break;
                case 1:
                    GInAppStore.getInstance(getContext()).buy(Goods.HintPack10);
                    break;
                case 2:
                    GInAppStore.getInstance(getContext()).buy(Goods.HintPack25);
                    break;
                case 3:
                    GInAppStore.getInstance(getContext()).buy(Goods.HintPack75);
                    break;
            }
        }
    };

}
