package com.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.emerginggames.snappers.Metrics;
import com.emerginggames.snappers.R;
import com.emerginggames.snappers.gdx.Resources;
import com.emerginggames.snappers.model.MoreGame;
import com.emerginggames.snappers.utils.MoreGamesUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: babay
 * Date: 21.07.12
 * Time: 4:39
 * To change this template use File | Settings | File Templates.
 */
public class MoreGamesAdapter extends BaseAdapter {
    private Context context;
    private List<MoreGame> moreGames;
    boolean isLargerText;

    public MoreGamesAdapter(Context context, List<MoreGame> moreGames, boolean isLargerText) {
        this.context = context;
        this.moreGames = moreGames;
        this.isLargerText = isLargerText;
    }

    @Override
    public int getCount() {
        return moreGames == null ? 0 : moreGames.size();
    }

    @Override
    public Object getItem(int position) {
        return moreGames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            view = LayoutInflater.from(context).inflate(R.layout.partial_more_games_cell_view, null);
        } else {
            view = convertView;
        }
        MoreGame game = moreGames.get(position);


        Bitmap bitmap = MoreGamesUtil.getIcon(game, context);
        if (bitmap != null) {
            ((android.widget.ImageView) view.findViewById(R.id.icon)).setImageBitmap(bitmap);
            ViewGroup.LayoutParams lp = view.findViewById(R.id.icon).getLayoutParams();
            lp.width = lp.height = (int) (Metrics.snapperSize * Metrics.snapperMult1);
        }

        OutlinedTextView title = ((OutlinedTextView) view.findViewById(R.id.title));
        OutlinedTextView description = ((OutlinedTextView) view.findViewById(R.id.descr));
        title.setText2(game.name);
        description.setText2(game.description);

        int fontSizeBase = Metrics.screenWidth / 14;

        if (isLargerText) {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeBase * 1.3f);
            description.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeBase);
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeBase * 1.1f);
            description.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeBase * 0.8f);
        }


        Resources.applyTypeface(view, new int[]{R.id.title, R.id.descr}, Resources.getFont(context));
        view.setTag(game);

        return view;
    }
}