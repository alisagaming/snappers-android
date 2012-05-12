package ru.emerginggames.snappers.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import ru.emerginggames.snappers.R;

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
    
    int mLayoutPaddingLeft;
    int mLayoutPaddingTop;



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
            if (isSquare){

                float textHeight = getLineHeight();
                textWidth = Math.max(textWidth, textHeight);
                size = (float)Math.ceil(size * width/ textWidth);
                setTextSize( TypedValue.COMPLEX_UNIT_PX, size);
                return;
            }

            size = (float)Math.ceil(size * width/ textWidth);
            setTextSize( TypedValue.COMPLEX_UNIT_PX, size);
            return;
        }
        
        if (mLineEnds == null)
            return;

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
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(widthMeasureSpec);

        if (wMode == MeasureSpec.UNSPECIFIED || hMode == MeasureSpec.UNSPECIFIED){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        needResize |=  (lastMeasuredWidth != wSize);
        if (needResize && isSquare && maxLines == 1 && setTextSizeToFit){
            setTextSizeToFit(wSize, hSize);
            setMeasuredDimension(wSize, wSize);
            return;
        }

        if (needResize && setTextSizeToFit){
            setTextSizeToFit(wSize, hSize);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        isPaintPrepared = false;
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
        isPaintPrepared = false;
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
            makeNewLayout();

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
