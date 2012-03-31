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
    protected static final float SIZE_MULT_FIT = 0.6f;
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
    private boolean isSquare;


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
        if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL)
            posx = (getWidth() - rect.width())/2 - rect.left;
        if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL)
            posy = (getHeight() -rect.top)/2;
        if ((gravity & Gravity.RIGHT) == Gravity.RIGHT)
            posx = getWidth() - rect.right - strokeWidth - getPaddingRight();
        if ((gravity & Gravity.TOP) == Gravity.TOP)
            posy = getPaddingTop() + strokeWidth - Math.round(fontMetrics.ascent);


        canvas.drawText(text, 0, text.length(), posx, posy, outlinePaint);
        canvas.drawText(text, 0, text.length(), posx, posy, textPaint);

        needRedraw = false;
    }

    public void setTextSizeToFit(boolean toFit){
        setTextSizeToFit = toFit;
    }

    public void setSquare(boolean square) {
        isSquare = square;
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
            size = (float)Math.ceil(size * width/ textWidth) * SIZE_MULT_FIT;
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
        setTextSize(size * SIZE_MULT_FIT);

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

}
