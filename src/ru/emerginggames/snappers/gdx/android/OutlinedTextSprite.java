package ru.emerginggames.snappers.gdx.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ru.emerginggames.snappers.gdx.Elements.IPositionable;
import ru.emerginggames.snappers.gdx.helper.PositionHelper;

public class OutlinedTextSprite extends Sprite implements IPositionable{

    private Paint textPaint = new Paint();
    private Paint outlinePaint = new Paint();
    private Paint backPaint = new Paint();
    private Paint.FontMetrics fontMetrics;

    String text;
    int textSize;
    int color;
    int outlineColor;
    int backColor;
    int strokeWidth;
    Typeface typeface;

    public OutlinedTextSprite(String text, int textSize, int color, int outlineColor, int backColor, int strokeWidth, Typeface typeface) {
        super();
        this.text = text;
        this.textSize = textSize;
        this.color = color;
        this.outlineColor = outlineColor;
        this.backColor = backColor;
        this.typeface = typeface;
        this.strokeWidth = strokeWidth;
        Texture.setEnforcePotImages(false);
        setTextTexture();
    }
    
    public void setText(String text){
        this.text = text;
        setTextTexture();
    }

    protected void setTextTexture(){
        preparePaint();

        Bitmap bitmap = Bitmap.createBitmap(measureTextWidth(), getTextHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backPaint);
        float paddingTop  = Math.abs(fontMetrics.ascent) + strokeWidth * 2;
        canvas.drawText(text, 0, paddingTop , outlinePaint);
        canvas.drawText(text, 0, paddingTop, textPaint);

        Pixmap pixmap = BitmapPixmap.bitmapToPixmap(bitmap);

        Texture texture = getTexture();
        if (texture != null)
            texture.dispose();

        texture = new Texture(pixmap);
        setTexture(texture, 0, 0, texture.getWidth(), texture.getHeight());
        bitmap.recycle();
    }

    protected void preparePaint() {
        textPaint.setColor(this.color);
        textPaint.setTypeface(this.typeface);
        textPaint.setTextSize(this.textSize);
        textPaint.setAntiAlias(true);

        outlinePaint.setColor(this.outlineColor);
        outlinePaint.setTypeface(this.typeface);
        outlinePaint.setTextSize(this.textSize);
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(strokeWidth);

        backPaint.setColor(this.backColor);
        backPaint.setStyle(Paint.Style.FILL);
        fontMetrics = outlinePaint.getFontMetrics();
    }

    public int getTextHeight() {
        return (int)Math.ceil(Math.abs(fontMetrics.ascent) +
                Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.leading)) + (int)(strokeWidth * 2);
    }

    protected int measureTextWidth() {
        return (int)Math.ceil(outlinePaint.measureText(text) + strokeWidth * 2);
    }

    protected void setTexture(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        setTexture(texture);
        setRegion(srcX, srcY, srcWidth, srcHeight);
        setColor(1, 1, 1, 1);
        setSize(Math.abs(srcWidth), Math.abs(srcHeight));
        setOrigin(srcWidth / 2, srcHeight / 2);
    }

    @Override
    public void positionRelative(IPositionable other, Dir dir, float margin) {
        PositionHelper.Position(this, other, dir, margin);
    }

    @Override
    public void positionRelative(float x, float y, Dir dir, float margin) {
        PositionHelper.Position(x, y, this, dir, margin);
    }

    public void resume(){
        setTextTexture();
    }

    public void dispose(){
        getTexture().dispose();
    }
}
