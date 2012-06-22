package com.emerginggames.snappers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.emerginggames.snappers.model.ImageDrawInfo;
import com.emerginggames.snappers.model.ImagePaginatorParam;
import com.emerginggames.snappers.view.ICurrentItemSelector;
import com.emerginggames.snappers.view.IOnItemSelectedListener;
import com.emerginggames.snappers.view.RotatedImageView;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 9:04
 */
public class RotatedImagePagerAdapter extends PagerAdapter implements View.OnClickListener, ICurrentItemSelector {
    View currentView;
    Context context;
    List<ImagePaginatorParam> paramList;
    IOnItemSelectedListener listener;
    SparseArray<RotatedImageView> views;

    public RotatedImagePagerAdapter(Context context, List<ImagePaginatorParam> paramList, IOnItemSelectedListener listener) {
        this.context = context;
        this.paramList = paramList;
        this.listener = listener;
        views = new SparseArray<RotatedImageView>(paramList.size());
    }

    @Override
    public int getCount() {
        return paramList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RotatedImageView view = new RotatedImageView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        view.setLayoutParams(lp);
        view.setImageList(paramList.get(position).images);
        view.setTag(position);
        view.setOnClickListener(this);
        container.addView(view);
        views.put(position, view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof Integer)
            listener.onItemSelected(paramList.get((Integer)v.getTag()).retParam);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        views.remove(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentView = (View)object;
    }

    @Override
    public View getCurrentItemView() {
        return currentView;
    }

    public void changeImages(int retParam, ImageDrawInfo[] images){
        int pos = 0;
        for (int i=0; i<paramList.size(); i++){
            if (paramList.get(i).retParam == retParam){
                pos = i;
                break;
            }
        }
        paramList.get(pos).images = images;
        if (views.get(pos) != null)
            views.get(pos).setImageList(images);
    }
}
