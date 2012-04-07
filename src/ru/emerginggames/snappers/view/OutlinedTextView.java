package ru.emerginggames.snappers.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
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

    private TextPaint textPaint = new TextPaint();
    private TextPaint outlinePaint = new TextPaint();
    private Paint.FontMetrics fontMetrics;

    private boolean needResize = true;
    private boolean setTextSizeToFit = false;
    private boolean isSquare;
    
    Layout mLayout;
    Layout mStrokeLayout;

    float mLineAdd = 0;
    float mLineMult = 1;
    
    int maxLines=0;
    
    int[] mLineEnds;
    
    int mLayoutPaddingLeft;
    int mLayoutPaddingTop;



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
    }

    public void drawBoring(Canvas canvas){
        if (getBackground() != null)
            getBackground().draw(canvas);

        CharSequence text = getText();
        if (text.length() == 0)
            return;

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

        canvas.translate(posx, posy);

        canvas.drawText(text, 0, text.length(), 0, 0, outlinePaint);
        canvas.drawText(text, 0, text.length(), 0, 0, textPaint);
    }

    public void setTextSizeToFit(boolean toFit){
        setTextSizeToFit = toFit;
    }

    public void setSquare(boolean square) {
        isSquare = square;
    }

    public void setMaxLines2(int maxlines) {
        maxLines = maxlines;
        needResize = true;
    }

    protected void setTextSizeToFit(){
        CharSequence text = getText();
        if (text.length() <1)
            return;
        
        if (maxLines == 1){
            preparePaint();
            int width = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int height = getMeasuredHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
            float size = getTextSize();

            float textWidth = outlinePaint.measureText(text, 0, text.length()) + strokeWidth * 2;
            if (isSquare){

                float textHeight = getLineHeight();
                textWidth = Math.max(textWidth, textHeight);
                size = (float)Math.ceil(size * width/ textWidth);
                setTextSize( TypedValue.COMPLEX_UNIT_PX, size);
                return;
            }

/*            setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            super.onMeasure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int newHeight = getMeasuredHeight();
            if (newHeight > height){
                size = (float)Math.ceil(size * height/newHeight);
            }*/
            setTextSize( TypedValue.COMPLEX_UNIT_PX, size);

            return;
        }
        
        if (mLineEnds == null)
            return;

        preparePaint();
        if (mLayout == null)
            makeNewLayout();
        if (mLayout == null)
            return;
        
        float width = 0;
        int lastEnd = 0;
        for (int i=0; i< mLineEnds.length; i++){
            if (mLineEnds[i] == 0)
                break;
            width = Math.max(width, outlinePaint.measureText(text, lastEnd, mLineEnds[i]));
            lastEnd = mLineEnds[i];
        }

        float size = getTextSize();

        int desiredWidth = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight() - strokeWidth * 2;
        size = size * desiredWidth/width;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        outlinePaint.setTextSize(size);
        textPaint.setTextSize(size);

        needResize = false;
    }

    public void setLineEnds(int[] lineEnds) {
        mLineEnds = lineEnds;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lastMeasuredWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        needResize |=  (lastMeasuredWidth != getMeasuredWidth());
        if (needResize && setTextSizeToFit){
            setTextSizeToFit();
            if(maxLines > 1 )
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            makeNewLayout();
        }
        if (mLayout == null)
            makeNewLayout();
    }
    
    void makeNewLayout(){
        Layout.Alignment alignment;
        int gravity = getGravity();
        int w  = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                alignment = Layout.Alignment.ALIGN_CENTER;
                break;

            case Gravity.RIGHT:
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
                break;

            default:
                alignment = Layout.Alignment.ALIGN_NORMAL;
        }

        preparePaint();
        
        CharSequence text = getText();
        mLayout = new StaticLayout(text, textPaint,
                w, alignment, mLineMult, mLineMult, true);
        
        mStrokeLayout = new StaticLayout(text, outlinePaint,
                w, alignment, mLineMult, mLineMult, true);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        needResize = true;
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
        if (mLayout.getLineCount() == 1){
            drawBoring(canvas);
            return;
        }

        canvas.translate(getPaddingLeft(), getCompoundPaddingTop());
        int maxWidth = 0;
        for (int i=0; i< mLayout.getLineCount(); i++){
            maxWidth = Math.max(maxWidth, (int)mLayout.getLineRight(i));
        }

        mStrokeLayout.draw(canvas);
        mLayout.draw(canvas);


    }
    
    public int getUnpaddedWidth(){
        return getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingLeft();
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
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mLineAdd = add;
        mLineMult = mult;
    }
}
