package ru.emerginggames.snappers.gdx.core;

import android.graphics.*;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.emerginggames.snappers.gdx.helper.IPositionable;
import ru.emerginggames.snappers.gdx.Elements.PositionInfo;
import ru.emerginggames.snappers.gdx.helper.PositionHelper;

public class OutlinedTextSprite extends Sprite implements IPositionable, IOnTextureDataNeededHandler{

    private Paint textPaint = new Paint();
    private Paint outlinePaint = new Paint();
    private Paint backPaint = new Paint();
    private Paint.FontMetrics fontMetrics;
    private int allocatedTextureWidth;
    private int allocatedTextureHeight;
    private boolean updatingTexture;
    private boolean postponeTextureUpdate;
    private Bitmap updateBitmap;
    protected PositionInfo positionInfo;


    String text;
    int textSize;
    int color;
    int outlineColor;
    int backColor;
    int strokeWidth;
    Typeface typeface;
    int desiredWidth = 0;

    public OutlinedTextSprite(int width, int textSize, int color, int outlineColor, int strokeWidth, Typeface typeface) {
        this(width, textSize, color, outlineColor, Color.TRANSPARENT, strokeWidth, typeface);
    }

    public OutlinedTextSprite(String text, int textSize, int color, int outlineColor, int strokeWidth, Typeface typeface) {
        this(text, textSize, color, outlineColor, Color.TRANSPARENT, strokeWidth, typeface);
    }

    public OutlinedTextSprite(int width, int textSize, int color, int outlineColor, int backColor, int strokeWidth, Typeface typeface) {
        super();
        desiredWidth = width;
        this.textSize = textSize;
        this.color = color;
        this.outlineColor = outlineColor;
        this.backColor = backColor;
        this.typeface = typeface;
        this.strokeWidth = strokeWidth;
        preparePaint();
        setSize(width, getTextHeight());
    }

    public OutlinedTextSprite(String text, int textSize, int color, int outlineColor, int backColor, int strokeWidth, Typeface typeface) {
        super();
        this.text = text;
        this.textSize = textSize;
        this.color = color;
        this.outlineColor = outlineColor;
        this.backColor = backColor;
        this.typeface = typeface;
        this.strokeWidth = strokeWidth;
        preparePaint();
        setSize(desiredWidth = measureTextWidth(), getTextHeight());
        setTextTexture();
    }
    
    public void setText(String text){
        this.text = text;
        if (measureTextWidth() > allocatedTextureWidth && allocatedTextureWidth > 0)
            throw new RuntimeException("new string too long: " + text);
        setTextTexture();
    }
    
    public void setWidthForText(String text){
        desiredWidth = measureTextWidth(text);
    }

    protected void setTextTexture(){
        Texture texture = getTexture();
        if (texture == null){
            texture = new Texture(new BitmapManagedTextureData(this, Pixmap.Format.RGBA4444));
            setTexture(texture, 0, 0, measureTextWidth(), getTextHeight());
        }
        else
            startBitmapUpdate();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (getTexture() == null)
            setTextTexture();
        if (getTexture() == null)
            return;
        if (updatingTexture && updateBitmap != null)
            updateTexture();
        super.draw(spriteBatch);
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float alphaModulation) {
        if (getTexture() == null)
            setTextTexture();
        if (getTexture() == null)
            return;
        if (updatingTexture && updateBitmap != null)
            updateTexture();
        super.draw(spriteBatch, alphaModulation);
    }

    @Override
    public boolean recycleBitmap() {
        return true;
    }

    @Override
    public Bitmap textureInfoNeeded(int width, int height) {
        if (width == 0){
            width = allocatedTextureWidth = BitmapPixmap.nextPowerOfTwo(Math.max(measureTextWidth(), desiredWidth));
            height = allocatedTextureHeight = BitmapPixmap.nextPowerOfTwo(getTextHeight());
        }

        return makeTextBitmap(width, height);
    }
    
    protected Bitmap makeTextBitmap(int width, int height){
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backPaint);
        float paddingTop  = Math.abs(fontMetrics.ascent) + strokeWidth * 2;
        canvas.drawText(text, strokeWidth, paddingTop , outlinePaint);
        canvas.drawText(text, strokeWidth, paddingTop, textPaint);
        return bitmap;
    }

    protected void preparePaint() {
        textPaint.setColor(this.color);
        textPaint.setTypeface(this.typeface);
        textPaint.setTextSize(this.textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFilterBitmap(true);

        outlinePaint.setColor(this.outlineColor);
        outlinePaint.setTypeface(this.typeface);
        outlinePaint.setTextSize(this.textSize);
        outlinePaint.setAntiAlias(true);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(strokeWidth);
        outlinePaint.setFilterBitmap(true);

        backPaint.setColor(this.backColor);
        backPaint.setStyle(Paint.Style.FILL);
        fontMetrics = outlinePaint.getFontMetrics();
    }

    public int getTextHeight() {
        return (int)Math.ceil(Math.abs(fontMetrics.ascent) +
                Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.leading)) + (int)(strokeWidth * 2);
    }

    public int measureTextWidth() {
        if (text == null)
            return 0;
        return (int)Math.ceil(outlinePaint.measureText(text) + strokeWidth * 2);
    }

    public int measureTextWidth(String text) {
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
        if (positionInfo == null)
            positionInfo = new PositionInfo();
        positionInfo.set(other, dir, margin);

    }

    @Override
    public void positionRelative(float x, float y, Dir dir, float margin) {
        PositionHelper.Position(x, y, this, dir, margin);
        if (positionInfo == null)
            positionInfo = new PositionInfo();
        positionInfo.set(x, y, dir, margin);
    }

    @Override
    public float getRight() {
        return getX() + getWidth();
    }

    @Override
    public float getTop() {
        return getY() + getHeight();
    }

    public void dispose(){
        if (getTexture() != null)
            getTexture().dispose();
    }

    private void startBitmapUpdate(){
        if (updatingTexture){
            postponeTextureUpdate = true;
            return;
        }

        updatingTexture = true;
        new UpdateBitmapAsyncTask().execute();
    }

    private void updateTexture(){

        getTexture().bind();
        GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, updateBitmap);
        updateBitmap.recycle();
        updateBitmap = null;
        setTexture(getTexture(), 0, 0, measureTextWidth(), getTextHeight());

        if (postponeTextureUpdate){
            postponeTextureUpdate = false;
            updatingTexture = false;
            startBitmapUpdate();
        }
        else updatingTexture = false;
        if (positionInfo != null)
            PositionHelper.Position(this, positionInfo);
    }

    private class UpdateBitmapAsyncTask extends AsyncTask<Void, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(Void... params) {
            Texture texture = getTexture();
            return makeTextBitmap(texture.getWidth(), texture.getHeight());
        }

        @Override
        protected void onCancelled() {
            startBitmapUpdate();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            updateBitmap = bitmap;
        }
    }
}
