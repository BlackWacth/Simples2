package com.example.customdrawable.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by Bruce on 2016/11/11.
 */

public class CircleImageDrawable extends Drawable {

    public static final String TAG = "CircleImageDrawable";

    private Paint mPaint;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private BitmapShader mBitmapShader;
    private Rect mBounds;

    public CircleImageDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMatrix = new Matrix();

        mBounds = getBounds();
        Log.i(TAG, "Bounds = " + mBounds.toString());
        float scale = Math.max(mBounds.width() * 1.0f / mBitmap.getWidth(), mBounds.height() * 1.0f / mBitmap.getHeight());
        mMatrix.setScale(scale, scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), Math.min(mBounds.width(), mBounds.height()), mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }
}
