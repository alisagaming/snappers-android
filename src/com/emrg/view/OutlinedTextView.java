package com.emrg.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import com.emerginggames.snappers.R;


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
    private boolean setTextSizeToFit;
    private boolean isSquare;
    boolean isPaintPrepared;
    
    Layout mLayout;
    Layout mStrokeLayout;

    float mLineAdd = 0;
    float mLineMult = 1;
    
    int maxLines=0;
    
    int[] mLineEnds;
    
    int[] bgPad;


    public OutlinedTextView(Context context) {
        super(context);
    }

    public OutlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.OutlinedTextView);
        setTextSizeToFit = styledAttributes.getBoolean(R.styleable.OutlinedTextView_setTextSizeToFit, false);
        strokeColor = styledAttributes.getColor(R.styleable.OutlinedTextView_strokeColor, android.R.color.transparent);
        strokeWidth = styledAttributes.getDimensionPixelSize(R.styleable.OutlinedTextView_strokeWidth, 0);
        styledAttributes.recycle();
    }

    public void setStroke(int color, int width){
        strokeColor = color;
        strokeWidth = width;
        isPaintPrepared = false;
    }

    public void drawBoring(Canvas canvas){
        Drawable back = getBackground();
        if (back != null && back instanceof BitmapDrawable)
            ((BitmapDrawable)back).setAntiAlias(true);
        if (getBackground() != null)
            getBackground().draw(canvas);

        CharSequence text = getText();
        if (text.length() == 0)
            return;

        int posx = 0;
        int posy = 0;
        int gravity = getGravity();
        Rect rect = new Rect();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        textPaint.getTextBounds(text.toString(), 0, text.length(), rect);

        if ((gravity & Gravity.TOP) == Gravity.TOP)
            posy = getPaddingTop() + strokeWidth - Math.round(fontMetrics.ascent);
        else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM)
            posy = getHeight() - strokeWidth - getPaddingBottom();
        else if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL)
            posy = (height + rect.height())/2 + getPaddingTop();

        if ((gravity & Gravity.LEFT) == Gravity.LEFT)
            posx = getPaddingLeft() + strokeWidth;
        else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT)
            posx = getWidth() - rect.right - strokeWidth - getPaddingRight();
        else if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL)
            posx = (width - rect.width())/2 - rect.left + getPaddingLeft();

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

    public void setBackgroundPaddings(int[] backgroundPaddings) {
        this.bgPad = backgroundPaddings;
    }

    public void setMaxLines2(int maxlines) {
        maxLines = maxlines;
        needResize = true;
        mLayout = null;
    }

    protected void setTextSizeToFit(int measuredWidth, int measuredHeight){
        CharSequence text = getText();
        if (text.length() <1)
            return;

        if (!isPaintPrepared)
            preparePaint();

        if (maxLines == 1){

            int width = measuredWidth - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int height = measuredHeight - getCompoundPaddingTop() - getCompoundPaddingBottom();
            float size = getTextSize();

            float textWidth = outlinePaint.measureText(text, 0, text.length()) + strokeWidth * 2;
            float textHeight = getLineHeight();

            if (isSquare){
                textWidth = Math.max(textWidth, textHeight);
                size = (float)Math.ceil(size * width/ textWidth);
                setTextSize( TypedValue.COMPLEX_UNIT_PX, size);
                return;
            }

            size = (float)Math.ceil(size * Math.min(width/ textWidth, height / textHeight));
            setTextSize( TypedValue.COMPLEX_UNIT_PX, size);
            return;
        }
        
        if (mLineEnds == null)
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

        int desiredWidth = measuredWidth - getCompoundPaddingLeft() - getCompoundPaddingRight() - strokeWidth * 2;
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
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp.width > 0)
            wSize = Math.min(wSize, lp.width);
        if (lp.height > 0)
            hSize = Math.min(hSize, lp.height);

        if (wMode == MeasureSpec.UNSPECIFIED || hMode == MeasureSpec.UNSPECIFIED){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        needResize |=  (lastMeasuredWidth != wSize || getMeasuredHeight() != hSize);
        float scale = 0;

        if (needResize && bgPad != null){
            if (hMode == MeasureSpec.EXACTLY)
                scale =  (float)hSize / getBackground().getIntrinsicHeight();
            else if (wMode == MeasureSpec.EXACTLY)
                scale =  (float)wSize / getBackground().getIntrinsicWidth();
            if (scale != 0)
                setPadding((int)(bgPad[0] * scale), (int)(bgPad[1] * scale), (int)(bgPad[2] * scale), (int)(bgPad[3] * scale));
        }

        if (needResize && isSquare && maxLines == 1 && setTextSizeToFit){
            setTextSizeToFit(wSize, hSize);
            setMeasuredDimension(wSize, wSize);
            return;
        }

        if (needResize && setTextSizeToFit){
            setTextSizeToFit(wSize, hSize);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth + 2 * strokeWidth <= wSize)
            measuredWidth += 2 * strokeWidth;
        if (measuredHeight + 2 * strokeWidth <= hSize)
            measuredHeight += 2 * strokeWidth;
        setMeasuredDimension(measuredWidth, measuredHeight);
        needResize = false;
    }

    void makeNewLayout(int width){
        Layout.Alignment alignment;
        int gravity = getGravity();
        int w  = width - getCompoundPaddingLeft() - getCompoundPaddingRight();
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
        if (!isPaintPrepared || textPaint == null || outlinePaint == null)
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
        mLayout = null;
        invalidate();
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        isPaintPrepared = false;
        mLayout = null;
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
        isPaintPrepared = false;
        mLayout = null;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        isPaintPrepared = false;
        mLayout = null;
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        isPaintPrepared = false;
        mLayout = null;
    }



    public void setText2(CharSequence text){
        setText(text);
        mLayout = null;
    }

    public void setText2(int id){
        setText(id);
        mLayout = null;
    }

    protected void preparePaint(){
        Typeface typeface = getTypeface();
        ColorStateList colors = getTextColors();

        textPaint.setColor(colors.getDefaultColor());
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.DEV_KERN_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);



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
        if (mLayout == null)
            makeNewLayout(getMeasuredWidth());

        canvas.save();

        if (mLayout.getLineCount() == 1 || maxLines == 1){
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

        canvas.restore();


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
