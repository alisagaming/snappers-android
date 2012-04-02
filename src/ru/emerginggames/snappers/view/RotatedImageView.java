package ru.emerginggames.snappers.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

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
    protected Context mContext;
    int sourceId;
    int sourceBgId;
    int[] imageList;
    //BitmapDrawable source;
    //BitmapDrawable sourceBg;
    Bitmap preparedBitmap;
    protected float rotatedWidth = 0;
    protected float rotatedHeight = 0;
    float angle;
    float scale;
    int shiftX;
    int shiftY;
    int rotatedScaledWidth;
    int rotatedScaledHeight;
    boolean calculated = false;

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
    }

    public float getAngle() {
        return angle;
    }

    public void setImageList(int[] imageList) {
        this.imageList = imageList;
    }

    public void setImage(int id){
        sourceId = id;
        calculated = false;
    }
    
    public void setImageBg(int id){
        sourceBgId = id;
        calculated = false;
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
        drawBitmap(sourceId, scale, canvas, p, Bitmap.Config.RGB_565);
        if (sourceBgId != 0)
            drawBitmap(sourceBgId, scale, canvas, p, Bitmap.Config.ARGB_8888);
    }

    private void drawBitmap(int id, float scale, Canvas canvas, Paint p, Bitmap.Config config){
        Bitmap img = getBitmapUnscaled(id, config);

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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);


        int maxShift = Math.min(wSize, hSize) /50;
        if (!calculated){
            calculateRotatedSize();
            shiftX = (int)Math.round(Math.random()*maxShift*2 - maxShift);
            shiftY = (int)Math.round(Math.random()*maxShift*2 - maxShift);
        }
        float imgWidth = rotatedWidth;
        float imgHeight = rotatedHeight;
        
        if (hMode == MeasureSpec.UNSPECIFIED && wMode == MeasureSpec.UNSPECIFIED){
            wSize = Math.max(Math.round(imgWidth), getSuggestedMinimumWidth());
            hSize = Math.max(Math.round(imgHeight), getSuggestedMinimumHeight());

            setMeasuredDimension(wSize, hSize);
            return;
        }

        int h = rotatedScaledHeight = hSize = Math.max(hSize - getPaddingTop() - getPaddingBottom() - maxShift, getSuggestedMinimumHeight());
        int w = rotatedScaledWidth = wSize = Math.max(wSize - getPaddingLeft() - getPaddingRight() - maxShift, getSuggestedMinimumWidth());

        if ((hMode == MeasureSpec.EXACTLY && wMode == MeasureSpec.EXACTLY) ){
            if (imgWidth/w > imgHeight/h)
                rotatedScaledHeight = Math.round(imgHeight * w/imgWidth);
             else
                rotatedScaledWidth = Math.round(imgWidth * h/imgHeight);
        } else if (hMode == MeasureSpec.EXACTLY){
            rotatedScaledWidth = w = Math.round(imgWidth * h/imgHeight);
            if (wMode == MeasureSpec.AT_MOST && w >wSize){
                rotatedScaledWidth = w = wSize;
                rotatedScaledHeight = Math.round(imgHeight * w/imgWidth);
            }
        } else if (wMode == MeasureSpec.EXACTLY){
            h = Math.round(imgHeight * w/imgHeight);
            if (hMode == MeasureSpec.AT_MOST && h >hSize){
                rotatedScaledHeight = h = hSize;
                rotatedScaledWidth = w = Math.round(imgWidth * h/imgHeight);
            }
        } else if (wMode == MeasureSpec.AT_MOST && hMode == MeasureSpec.AT_MOST){
            if (imgWidth/w > imgHeight/h)
                rotatedScaledHeight = h = Math.round(imgHeight * w/imgWidth);
            else
                rotatedScaledWidth = w = Math.round(imgWidth * h/imgHeight);
        }
        scale = rotatedScaledWidth / rotatedWidth;
        if (scale > 0.98 && scale < 1.1)
            scale = 1;

        setMeasuredDimension(wSize, hSize);
    }

/*    protected float getScale(int widthMeasureSpec, int heightMeasureSpec, int imgWidth, int imgHeight){
        boolean isWidthCounts = true;

        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);


        int maxShift = Math.min(wSize, hSize) /50;
        if (!calculated){
            calculateRotatedSize();
            shiftX = (int)Math.round(Math.random()*maxShift*2 - maxShift);
            shiftY = (int)Math.round(Math.random()*maxShift*2 - maxShift);
        }

        if (hMode == MeasureSpec.UNSPECIFIED && wMode == MeasureSpec.UNSPECIFIED)
            return 1;

        int rotatedScaledHeight, rotatedScaledWidth, w, h;

        rotatedScaledHeight =  Math.max(hSize - getPaddingTop() - getPaddingBottom() - maxShift, getSuggestedMinimumHeight());
        rotatedScaledWidth = Math.max(wSize - getPaddingLeft() - getPaddingRight() - maxShift, getSuggestedMinimumWidth());


        if ((hMode == MeasureSpec.EXACTLY && wMode == MeasureSpec.EXACTLY) ||
            (wMode == MeasureSpec.AT_MOST && hMode == MeasureSpec.AT_MOST)){
             isWidthCounts = imgWidth/rotatedScaledWidth > imgHeight/rotatedScaledHeight;
        } else if (hMode == MeasureSpec.EXACTLY){
            w =  Math.round(imgWidth * rotatedScaledHeight/imgHeight);
            isWidthCounts = wMode == MeasureSpec.AT_MOST && w >rotatedScaledHeight;
        } else if (wMode == MeasureSpec.EXACTLY){
            h = Math.round(imgHeight * rotatedScaledWidth/imgHeight);
            isWidthCounts = !(hMode == MeasureSpec.AT_MOST && h >rotatedScaledHeight);
        }
        
        float scale = isWidthCounts ? rotatedScaledWidth / imgWidth : rotatedScaledHeight / imgHeight;
        if (scale > 0.98 && scale < 1.1)
            scale = 1;

        return scale;
    }*/

    protected void calculateRotatedSize(){
        int id = sourceBgId == 0? sourceId: sourceBgId;
        if (id == 0)
            return;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), id, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;
        float rAngle = (float)(angle / 180 * Math.PI);

        rotatedWidth = (float)(Math.abs(width * Math.cos(rAngle)) + Math.abs(height * Math.sin(rAngle)));
        rotatedHeight = (float)(Math.abs(height * Math.cos(rAngle)) + Math.abs(width * Math.sin(rAngle)));
        calculated = true;
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
