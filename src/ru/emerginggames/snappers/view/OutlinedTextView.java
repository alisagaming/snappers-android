package ru.emerginggames.snappers.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 23.03.12
 * Time: 17:24
 */
public class OutlinedTextView extends TextView{
    protected int strokeColor = Color.TRANSPARENT;
    protected int strokeWidth = 0;
    protected Bitmap textBitmap;

    private Paint textPaint = new Paint();
    private Paint outlinePaint = new Paint();
    private Paint.FontMetrics fontMetrics;
    protected Rect drawRect;

    private boolean needResize = true;
    private boolean needRedraw = true;
    private boolean setTextSizeToFit = false;
    private boolean isSquare = true;

    public OutlinedTextView(Context context) {
        super(context);
    }

    public OutlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlinedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setStroke(int color, int width){
        strokeColor = color;
        strokeWidth = width;
        needRedraw = true;
    }

    public void prepareBitmap(){
        if (setTextSizeToFit){
            preparePaint();
            setTextSizeToFit();
        }
        preparePaint();
        textBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(textBitmap);
        if (getBackground() != null)
            getBackground().draw(canvas);

        CharSequence text = getText();
        
        int posx = getPaddingLeft() + strokeWidth;
        int posy = getPaddingTop() + strokeWidth;
        int gravity = getGravity();
        Rect rect = new Rect();
        textPaint.getTextBounds(text.toString(), 0, text.length(), rect);
        if ((gravity & Gravity.CENTER_HORIZONTAL) != 0)
            posx = (getWidth() - rect.width())/2 - rect.left;
        if ((gravity & Gravity.CENTER_VERTICAL) != 0)
            posy = (getHeight() -rect.top)/2;

        canvas.drawText(text, 0, text.length(), posx, posy, outlinePaint);
        canvas.drawText(text, 0, text.length(), posx, posy, textPaint);

        needRedraw = false;
    }

    public void setTextSizeToFit(boolean toFit){
        setTextSizeToFit = toFit;
    }



    protected void setTextSizeToFit(){
        CharSequence text = getText();
        if (text.length() <1)
            return;
        
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        float size = getTextSize();
        
        float textWidth = outlinePaint.measureText(text, 0, text.length()) + strokeWidth * 2;
        if (isSquare){

            float textHeight = getLineHeight();
            textWidth = Math.max(textWidth, textHeight);
            size = (float)Math.ceil(size * width/ textWidth) * 0.7f;
            setTextSize(size);
            return;
        }


/*        outlinePaint.setTextSize(size);
        fontMetrics = outlinePaint.getFontMetrics();
        if (getTextHeight() > height)
            size = size * height/ getLineHeight();*/
        setTextSize(size);
        super.onMeasure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int newHeight = getMeasuredHeight();
        if (newHeight > height){
            size = (float)Math.ceil(size * height/newHeight);
        }
        setTextSize(size * 0.8f);

        //int height = super.
        needRedraw = true;
        
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        needRedraw = true;
    }

    public void resizeBitmap(){
        if (textBitmap != null)
            textBitmap.recycle();
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0)
            return;
               
        textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawRect = new Rect(0,0, getWidth(), getHeight());
        needResize = false;
        needRedraw = true;
    }

    protected void preparePaint(){
        Typeface typeface = getTypeface();
        ColorStateList colors = getTextColors();

        textPaint.setColor(colors.getDefaultColor());
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);

        outlinePaint.setColor(strokeColor);
        outlinePaint.setTypeface(typeface);
        outlinePaint.setTextSize(getTextSize());
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(strokeWidth);

        fontMetrics = outlinePaint.getFontMetrics();
    }
    

    @Override
    protected void onDraw(Canvas canvas) {
        if (needResize || getWidth() != textBitmap.getWidth() || getHeight() != textBitmap.getHeight())
            resizeBitmap();
        if (needResize)
            return;
        if (needRedraw)
            prepareBitmap();


        canvas.drawBitmap(textBitmap, drawRect, drawRect, null);
    }

    public int getTextHeight() {
        return (int)Math.ceil(Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent) + strokeWidth * 2);
    }
    
    public int getLineHeight(){
        return (int)Math.ceil(Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.leading) + strokeWidth * 2);
    }

    protected int measureTextWidth() {
        CharSequence text = getText();
        return (int)Math.ceil(outlinePaint.measureText(text, 0, text.length()) + strokeWidth * 2);
    }

    @Override
    public void setHeight(int pixels) {
        super.setHeight(pixels);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wOld = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (wOld == 0)
            return;
        int w = getMeasuredWidth();
        /*if (getWidth() == 0 || getWidth() < w)
            return;*/

        //to make it squared
        setMeasuredDimension(w, w);
    }
    /*    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        Rect rect = new Rect();
        CharSequence text = getText();
        textPaint.setTextSize(getTextSize());

        textPaint.getTextBounds(text.toString(), 0, text.length(), rect);

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            
            width = Math.max(rect.width() + strokeWidth * 2, getSuggestedMinimumWidth());
            width += getCompoundPaddingLeft() + getCompoundPaddingRight();

            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {

            height = Math.max(rect.height() + strokeWidth * 2, getSuggestedMinimumHeight());
            width += getCompoundPaddingTop() + getCompoundPaddingBottom();

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }*/
}
