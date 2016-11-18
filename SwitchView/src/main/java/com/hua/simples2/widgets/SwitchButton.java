package com.hua.simples2.widgets;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hzw on 2016/10/11.
 */

public class SwitchButton extends View implements ValueAnimator.AnimatorListener, ValueAnimator.AnimatorUpdateListener{

    public static final String tag = "SwitchButton";

    /**整个控件宽高比例  p = h/w */
    public static final float DEFAULT_WIDTH_HEIGHT_PERCENT = 0.65f;
    /**控件底部阴影比例 */
    public static final float DEFAULT_SHADE_PERCENT = 0.2f;

    /**控件圆脸直径占控件有效高度比例 */
    public static final float DEFAULT_FACE_PERCENT = 0.95f;

    private Paint mPaint;

    private Path mBgPath;
    private Path mFacePath;

    private int mWidth, mHeight;

    private boolean isOn = false;
    private boolean isRunning = false;

    private int mBgColorOn = 0xff33ccff;
    private int mBgColorOff = 0xffcccccc;
    private int mFaceColor = 0xffffffff;
    private int mCurrentColor = mBgColorOff;

    private float mTranslationLength = 0;
    private float mCurrentTranslationLength = 0;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null); //禁止硬件加速

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mBgPath = new Path();
        mFacePath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * DEFAULT_WIDTH_HEIGHT_PERCENT);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        float left = 0;
        float top = 0;
        float right = mWidth;
        float bottom = mHeight * (1 - DEFAULT_SHADE_PERCENT);

        RectF bgRectF = new RectF(left, top, bottom, bottom);
        mBgPath.arcTo(bgRectF, 90, 180);
        bgRectF.left = right - bottom;
        bgRectF.right = right;
        mBgPath.arcTo(bgRectF, 270, 180);
        mBgPath.close();

        float faceRadius = (bottom - top) * DEFAULT_FACE_PERCENT / 2;
        float faceCenterX = (left + bottom) / 2;
        float faceCenterY = (top + bottom) / 2;
        mFacePath.addCircle(faceCenterX, faceCenterY, faceRadius, Path.Direction.CW);

        mTranslationLength = mWidth - bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawForeground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCurrentColor);
        canvas.drawPath(mBgPath, mPaint);
    }

    private void drawForeground(Canvas canvas) {
        canvas.save();
        canvas.translate(mCurrentTranslationLength, 0);
        drawFace(canvas);
        canvas.restore();
    }

    private void drawFace(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFaceColor);
        canvas.drawPath(mFacePath, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mCurrentTranslationLength = isOn ? -Math.abs(mTranslationLength) : Math.abs(mTranslationLength);
                isOn = !isOn;
                Log.i(tag, "mTranslationLength = " + mTranslationLength);
                Log.i(tag, "isOn = " + isOn);
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onAnimationStart(Animator animation) {
        isRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        isRunning = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        isRunning = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        isRunning = true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }
}
