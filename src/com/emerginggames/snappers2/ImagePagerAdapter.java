package com.emerginggames.snappers2;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.emerginggames.snappers2.model.ImageDrawInfo;
import com.emerginggames.snappers2.model.ImagePaginatorParam;
import com.emrg.view.IOnItemSelectedListener;
import com.emrg.view.ImageView;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 29.05.12
 * Time: 1:07
 */
public class ImagePagerAdapter extends PagerAdapter implements View.OnClickListener/*, ICurrentItemSelector */{
    View currentView;
    Context context;
    List<ImagePaginatorParam> paramList;
    IOnItemSelectedListener listener;
    SparseArray<RelativeLayout> views;
    int imageWidth;

    public ImagePagerAdapter(Context context, List<ImagePaginatorParam> paramList, IOnItemSelectedListener listener) {
        this.context = context;
        this.paramList = paramList;
        this.listener = listener;
        views = new SparseArray<RelativeLayout>(paramList.size());
    }

    @Override
    public int getCount() {
        return paramList.size();
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView image = new ImageView(context);
        Rect r = new Rect();
        image.getGlobalVisibleRect(r);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        image.setImageResource(paramList.get(position).images[0].id);
        image.setId(R.id.image);
        image.setOnClickListener(this);
        image.setTag(position);

        RelativeLayout view = new RelativeLayout(context);
        view.addView(image, lp);

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
            ((ImageView)views.get(pos).findViewById(R.id.image)).setImageResource(images[0].id);
    }
}