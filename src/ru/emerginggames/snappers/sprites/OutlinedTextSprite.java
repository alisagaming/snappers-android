package ru.emerginggames.snappers.sprites;

import android.content.Context;
import android.graphics.*;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.TextSprite;
import com.e3roid.drawable.texture.BitmapTexture;
import com.e3roid.drawable.texture.Texture;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 02.03.12
 * Time: 7:45
 */
public class OutlinedTextSprite extends Sprite {
    private static final float strokeWidth = 2;
    private final Context context;
    private Paint textPaint = new Paint();
    private Paint outlinePaint = new Paint();
    private Paint backPaint = new Paint();
    private Paint.FontMetrics fontMetrics;
    private Typeface typeFace = Typeface.DEFAULT;
    private int textSize;
    private boolean antiAlias = true;
    private int textColor = Color.BLACK;
    private int outlineColor = Color.BLACK;
    private int backColor = Color.TRANSPARENT;
    private int preferredWidth = 0;

    private String text;

    private boolean textChanged = false;
    private boolean sizeChanged = false;

    private static final int PADDING_LEFT  = 5;
    private static final int PADDING_RIGHT = 5;

    private int paddingLeft;
    private int paddingRight;

    /**
     * Constructs text sprite with given text size.
     * @param text text
     * @param textSize text size
     * @param context context
     */
    public OutlinedTextSprite(String text, int textSize, Context context) {
        this(text, textSize, Color.BLACK, Color.TRANSPARENT, 0, Typeface.DEFAULT, context);
    }

    /**
     * Constructs text sprite with given size, color and typeface.
     * @param text text
     * @param textSize text size
     * @param color foreground color
     * @param backColor background color
     * @param typeface typeface
     * @param context context
     */
    public OutlinedTextSprite(String text, int textSize, int color, int outlineColor, int backColor, Typeface typeface, Context context) {
        this(text, textSize, color, outlineColor, backColor, 0, typeface, context);
    }

    /**
     * Constructs text sprite with given size, color, width and typeface.
     * @param text text
     * @param textSize text size
     * @param color foreground color
     * @param backColor background color
     * @param preferredWidth preferred width
     * @param typeface typeface
     * @param context context
     */
    public OutlinedTextSprite(String text, int textSize, int color, int outlineColor, int backColor,
                      int preferredWidth, Typeface typeface, Context context) {
        this.context = context;
        this.preferredWidth = preferredWidth;

        this.paddingLeft  = PADDING_LEFT;
        this.paddingRight = PADDING_RIGHT;

        setPosition(0, 0);
        setText(text);
        setTextSize(textSize);
        setColor(color);
        setBackColor(backColor);
        setTypeface(typeface);

        preparePaint();

        textChanged = false;
    }

    protected void preparePaint() {
        textPaint.setColor(this.textColor);
        textPaint.setTypeface(this.typeFace);
        textPaint.setTextSize(this.textSize);
        textPaint.setAntiAlias(this.antiAlias);

        outlinePaint.setColor(this.outlineColor);
        outlinePaint.setTypeface(this.typeFace);
        outlinePaint.setTextSize(this.textSize);
        outlinePaint.setAntiAlias(this.antiAlias);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(strokeWidth);

        backPaint.setColor(this.backColor);
        backPaint.setStyle(Paint.Style.FILL);
        fontMetrics = outlinePaint.getFontMetrics();

        setSize(getTextWidth(), getTextHeight());
    }

    /**
     * Draw text and update texture.
     */
    public void createLabel(GL10 gl) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backPaint);
        float paddingLeft = getPreferredPaddingLeft();
        float paddingTop  = Math.abs(fontMetrics.ascent) + strokeWidth * 2;
        canvas.drawText(text, paddingLeft , paddingTop , outlinePaint);
        canvas.drawText(text, paddingLeft, paddingTop, textPaint);

        if (texture != null && texture.isLoaded())
            texture.unloadTexture(gl);
        Texture texture = new BitmapTexture(bitmap, getWidth(), getHeight(), context);
        texture.recycleBitmap(true);

        updateTexture(texture);
    }

    /**
     * Returns text width of the sprite.
     * @return text width of the sprite
     */
    public int getTextWidth() {
        int width =  measureTextWidthWithPadding();
        if (preferredWidth > width) return preferredWidth;
        return width;
    }

    protected int measureTextWidthWithPadding() {
        return measureTextWidth() + paddingLeft + paddingRight;
    }

    protected int measureTextWidth() {
        return (int)Math.ceil(outlinePaint.measureText(text) + strokeWidth * 2);
    }

    private int getPreferredPaddingLeft() {
        int width =  measureTextWidthWithPadding();
        if (preferredWidth < width) return paddingLeft;
        return (int)((preferredWidth - measureTextWidth()) * 0.5f);
    }

    /**
     * Returns text height of the sprite.
     * @return text height of the sprite
     */
    public int getTextHeight() {
        return (int)Math.ceil(Math.abs(fontMetrics.ascent) +
                Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.leading)) + (int)(strokeWidth * 4);
    }

    /**
     * Called when the sprite is created or recreated.
     */
    @Override
    public void onLoadSurface(GL10 gl) {
        onLoadSurface(gl, false);
    }

    @Override
    protected void setSize(int w, int h) {
        if (getWidth() != w || getHeight() != h)
            sizeChanged = true;
        super.setSize(w, h);
    }

    /**
     * Called when the sprite is created or recreated.
     */
    @Override
    public void onLoadSurface(GL10 gl, boolean force) {
        if (textChanged) {
            preparePaint();
        }
        createLabel(gl);
        createBuffers();
        super.onLoadSurface(gl, force);
    }

    /**
     * Called to draw the sprite.
     * This method is responsible for drawing the sprite.
     */
    @Override
    public void onDraw(GL10 gl) {
        if (textChanged) {
            preparePaint();
            createLabel(gl);
            texture.loadTexture(gl, false);

            if (sizeChanged) {
                createBuffers();
                loadVertexBuffer((GL11)gl);
                loadTextureBuffer((GL11)gl);
            }
            sizeChanged = false;
            textChanged = false;
        }
        super.onDraw(gl);
    }

    /**
     * Called when this sprite is removed.
     */
    @Override
    public void onRemove() {
        ((BitmapTexture)texture).recycleBitmap();
        super.onRemove();
    }

    /**
     * Called when this sprite is disposed.
     */
    @Override
    public void onDispose() {
        super.onDispose();
    }

    /**
     *  Reload label text.
     *  If the label size needs to be changed, use reload(true).
     *
     *  @deprecated  Calling reload() is not necessary because TextSprite is reloaded automatically when text attributes have been changed.
     */
    public void reload() {
        reload(false);
    }

    /**
     *  Reload label text.
     *  if resized equals true, vertex/indices/texture buffers are re-created.
     *  @param resized true if text size needs to be changed.
     */
    public void reload(boolean resized) {
        this.sizeChanged = resized;
        this.textChanged = true;
    }

    /**
     * Returns text of the sprite
     * @return text of the sprite
     */
    public String getText() {
        return this.text;
    }

    /**
     * Set text of the sprite
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
        this.textChanged = true;
        if (fontMetrics != null)
            setSize(getTextWidth(), getTextHeight());
    }

    /**
     * Set text size
     * @param size text size
     */
    public void setTextSize(int size) {
        this.textSize = size;
        this.textChanged = true;
    }

    /**
     * Set anti-alias
     * @param enable
     */
    public void setAntiAlias(boolean enable) {
        this.antiAlias = enable;
        this.textChanged = true;
    }

    /**
     * Set foreground color
     * @param textColor
     */
    public void setColor(int textColor) {
        this.textColor = textColor;
        this.textChanged = true;
    }

    /**
     * Set outline color
     * @param textColor
     */
    public void setOutlineColor(int textColor) {
        this.outlineColor = textColor;
        this.textChanged = true;
    }

    /**
     * Set background color
     * @param backColor
     */
    public void setBackColor(int backColor) {
        this.backColor = backColor;
        this.textChanged = true;
    }

    /**
     * Set typeface
     * @param typeface
     */
    public void setTypeface(Typeface typeface) {
        this.typeFace = typeface;
        this.textChanged = true;
    }

    public void setPaddingLeft(int left) {
        this.paddingLeft = left;
        reload(true);
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingRight(int right) {
        this.paddingRight = right;
        reload(true);
    }

    public int getPaddingRight() {
        return this.paddingRight;
    }
}
