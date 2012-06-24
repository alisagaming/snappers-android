package com.emrg.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.View;
import com.emerginggames.snappers.model.ImageDrawInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 8:12
 */
public class RotatedImageView extends View {
    private static final float MAX_ANGLE = 5;
    protected boolean useMaxAngle = true;
    protected Context mContext;
    ImageDrawInfo[] imageList;
    int [][] rotatedDimensions;
    int maxShift;
    Bitmap preparedBitmap;
    float angle;
    float scale;
    int shiftX;
    int shiftY;

    public RotatedImageView(Context context) {
        super(context);
        mContext = context;
        angle = (float)Math.random() * 2 * MAX_ANGLE - MAX_ANGLE;
    }

    public RotatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        angle = (float)Math.random() * 2 * MAX_ANGLE - MAX_ANGLE;
    }

    public RotatedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        angle = (float)Math.random() * 2 * MAX_ANGLE - MAX_ANGLE;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        rotatedDimensions = null;
        useMaxAngle = false;
    }

    public float getAngle() {
        return angle;
    }

    public void setImageList(ImageDrawInfo[] imageList) {
        this.imageList = imageList;
        rotatedDimensions = null;
        if (preparedBitmap != null){
            preparedBitmap.recycle();
            preparedBitmap = null;
            invalidate();
        }
    }

    public void setImage(int id){
        if (imageList == null)
            imageList = new ImageDrawInfo[2];
        imageList[0] = new ImageDrawInfo(id, false, false);
        rotatedDimensions = null;
        invalidate();
    }
    
    public void setImageBg(int id){
        if (imageList == null)
            imageList = new ImageDrawInfo[2];
        imageList[1] = new ImageDrawInfo(id, true, true);
        rotatedDimensions = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (preparedBitmap == null)
            prepareDrawable();
        if (preparedBitmap == null)
            return;
        canvas.drawBitmap(preparedBitmap, 0, 0, null);
    }

    protected void prepareDrawable(){
        preparedBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(preparedBitmap);
        Paint p = new Paint();
        p.setFilterBitmap(true);
        if (imageList.length == 1)
            p.setAntiAlias(true);
        for (ImageDrawInfo imgInfo: imageList)
            drawBitmap(imgInfo, scale, canvas, p);
    }

    private void drawBitmap(ImageDrawInfo imgInfo, float scale, Canvas canvas, Paint p){
        if (imgInfo == null)
            return;
        Bitmap img = getBitmapUnscaled(imgInfo.id, imgInfo.getConfig());
        if (img == null)
            return;

        Matrix matr = new Matrix();

        int resW = getMeasuredWidth();
        int resH = getMeasuredHeight();
        float newW = img.getWidth() * scale;
        float newH = img.getHeight() * scale;

        RectF sourceRect = new RectF(0, 0, img.getWidth(), img.getHeight());
        RectF destRect = new RectF( (resW - newW)/2, (resH - newH)/2, (resW + newW)/2, (resH + newH)/2 );
        matr.setRectToRect(sourceRect, destRect, Matrix.ScaleToFit.CENTER);
        matr.postRotate(angle, resW/2, resH/2);
        matr.postTranslate(shiftX, shiftY);

        canvas.drawBitmap(img, matr, p);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if (rotatedDimensions == null){
            calculateRotatedDimensions();

            if (rotatedDimensions == null){ // if we don't know how to measure - we'll use what we have
                int w = wMode == MeasureSpec.EXACTLY ? wSize: getSuggestedMinimumWidth();
                int h = hMode == MeasureSpec.EXACTLY ? hSize : getSuggestedMinimumHeight();
                setMeasuredDimension(w, h);
                return;
            }

            if (wMode != MeasureSpec.UNSPECIFIED || hMode != MeasureSpec.UNSPECIFIED)
                maxShift = Math.min(wSize, hSize) /50;
            else{
                scale = 1;
                maxShift = Math.min(getMaxWidth(), getMaxHeight()) /50;
            }
            shiftX = (int)Math.round(Math.random()*maxShift*2 - maxShift);
            shiftY = (int)Math.round(Math.random()*maxShift*2 - maxShift);
        }

        scale = getScale(widthMeasureSpec, heightMeasureSpec);

        int wResult, hResult;

        if (wMode == MeasureSpec.EXACTLY)
            wResult = wSize;
        else if (wMode == MeasureSpec.AT_MOST)
            wResult = Math.min(getMaxWidth(), wSize);
        else
            wResult = getMaxWidth();
        
        if (hMode == MeasureSpec.EXACTLY)
            hResult = hSize;
        else if (hMode == MeasureSpec.AT_MOST)
            hResult = Math.min(getMaxHeight(), hSize);
        else
            hResult = getMaxHeight();

        setMeasuredDimension(wResult, hResult);
    }

    protected float getScale(int widthMeasureSpec, int heightMeasureSpec){
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if (wMode == MeasureSpec.UNSPECIFIED && hMode == MeasureSpec.UNSPECIFIED)
            return 1;

        int proposedWidth =  (wMode == MeasureSpec.UNSPECIFIED) ?
                Integer.MAX_VALUE :
                Math.max(wSize - getPaddingLeft() - getPaddingRight() - maxShift, getSuggestedMinimumWidth());

        int proposedHeight =  (hMode == MeasureSpec.UNSPECIFIED) ?
                Integer.MAX_VALUE :
                Math.max(hSize - getPaddingTop() - getPaddingBottom() - maxShift, getSuggestedMinimumHeight());

        float scale = Float.MAX_VALUE;
        for (int[] dim : rotatedDimensions){
            if (dim == null)
                continue;

            float wScale = proposedWidth/(float)dim[0];
            float hScale = proposedHeight/(float)dim[1];
            float localScale = Math.min(wScale, hScale);

            if (localScale < scale)
                scale = localScale;
        }

        return scale;
    }


    protected int getMaxWidth(){
        int result = 0;
        for (int[] dim : rotatedDimensions){
            if (dim == null)
                continue;

            int w = Math.round(dim[0] * scale);
            if (result < w)
                result = w;
        }
        return result;
    }

    protected int getMaxHeight(){
        int result = 0;
        for (int[] dim : rotatedDimensions){
            if (dim == null)
                continue;

            int h = Math.round(dim[1] * scale);
            if (result < h)
                result = h;
        }
        return result;
    }


    protected void calculateRotatedDimensions(){
        if (imageList == null)
            return;

        rotatedDimensions = new int[imageList.length][];

        for (int i=0; i< imageList.length; i++)
            rotatedDimensions[i] = calcRotatedSize(imageList[i].id);
    }

    protected int[] calcRotatedSize(int id){
        if (id == 0)
            return null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), id, opts);

        int w = opts.outWidth;
        int h = opts.outHeight;
        float rAngle = (float)((useMaxAngle ? MAX_ANGLE : angle) / 180 * Math.PI);

        float sin = FloatMath.sin(rAngle);
        float  cos = FloatMath.cos(rAngle);

        float rWidth = (Math.abs(w * cos) + Math.abs(h * sin));
        float rHeight = (Math.abs(h * cos) + Math.abs(w * sin));

        return new int[]{ Math.round(rWidth), Math.round(rHeight)};
    }

    protected float calcHeight(float w, float h, float scale){
        float rAngle = (float)(angle / 180 * Math.PI);
        return  (float)(Math.abs(h * Math.cos(rAngle)) + Math.abs(w * Math.sin(rAngle))) * scale;
    }


    private Bitmap getBitmapUnscaled(int id, Bitmap.Config config){
        Resources res = mContext.getResources();
        Bitmap bm = null;
        InputStream is = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = config;

        try {
            final TypedValue value = new TypedValue();
            is = res.openRawResource(id, value);
            value.density = TypedValue.DENSITY_NONE;
            bm = BitmapFactory.decodeResourceStream(res, value, is, null, opts);
        } catch (Exception e) {
            /*  do nothing.
                If the exception happened on open, bm will be null.
                If it happened on close, bm is still valid.
            */
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        return bm;
    }
}
