package com.emerginggames.snappers.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

    public MoreGamesAdapter(Context context, List<MoreGame> moreGames) {
        this.context = context;
        this.moreGames = moreGames;
    }

    @Override
    public int getCount() {
        return moreGames.size();
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
        if (bitmap != null)
            ((android.widget.ImageView)view.findViewById(R.id.icon)).setImageBitmap(bitmap);

        ((OutlinedTextView)view.findViewById(R.id.title)).setText2(game.name);
        ((OutlinedTextView)view.findViewById(R.id.descr)).setText2(game.description);

        Resources.applyTypeface(view, new int[]{R.id.title, R.id.descr}, Resources.getFont(context));
        view.setTag(game);

        return view;
    }
}