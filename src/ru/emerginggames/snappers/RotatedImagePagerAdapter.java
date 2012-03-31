package ru.emerginggames.snappers;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import ru.emerginggames.snappers.view.ICurrentItemSelector;
import ru.emerginggames.snappers.view.IOnItemSelectedListener;
import ru.emerginggames.snappers.view.RotatedImageView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 9:04
 */
public class RotatedImagePagerAdapter extends PagerAdapter implements View.OnClickListener, ICurrentItemSelector {
    View currentView;
    Context context;
    int[] imageIds;
    int[] shadowIds;
    IOnItemSelectedListener listener;

    public RotatedImagePagerAdapter(Context context, int[] imageIds, int[] shadowIds, IOnItemSelectedListener listener) {
        this.context = context;
        this.imageIds = imageIds;
        this.shadowIds = shadowIds;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RotatedImageView view = new RotatedImageView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        view.setLayoutParams(lp);
        view.setImage(imageIds[position]);
        view.setImageBg(shadowIds[position]);
        view.setTag(position);
        view.setOnClickListener(this);
        container.addView(view, position);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof Integer)
            listener.onItemSelected((Integer)v.getTag());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentView = (View)object;
    }

    @Override
    public View getCurrentItemView() {
        return currentView;
    }
}
